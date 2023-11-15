package com.shcm.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.dto.Result;
import com.shcm.entity.Shop;
import com.shcm.mapper.ShopMapper;
import com.shcm.service.IShopService;
import com.shcm.utils.CacheClient;
import com.shcm.utils.RedisData;
import com.shcm.utils.SystemConstants;
import lombok.val;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.shcm.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    //缓存重建需要用的线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public Result queryById(Long id) {

        //缓存穿透
//        Shop shop = queryWithPassThrough(id);

        //用互斥锁解决缓存击穿,包含了缓存穿透
//        Shop shop = queryWithMutex(id);
//        if (shop == null){
//            return Result.fail("店铺不存在");
//        }

//        //用逻辑过期解决缓存击穿问题
//        Shop shop = queryWithLogicalExpire(id);

//        //用封装的方法解决缓存穿透问题
//        Shop shop =  cacheClient.
//                queryWithPassThrough(CACHE_SHOP_KEY,id,Shop.class,this::getById,CACHE_SHOP_TTL,TimeUnit.MINUTES);

        //用封装的方法采用逻辑过期解决缓存击穿问题
        Shop shop = cacheClient.
                queryWithLogicalExpire(CACHE_SHOP_KEY,id,Shop.class,this::getById,CACHE_SHOP_TTL,TimeUnit.MINUTES);
        return Result.ok(shop);
    }

    public Shop queryWithLogicalExpire(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1.从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在,
        if (StrUtil.isBlank(shopJson)) {//不存在，直接返回null
            // 3.存在,直接返回
            return null;
        }
        // 4.命中，需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();

        // 5.判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 5.1.未过期，直接返回店铺信息
            return shop;
        }
        // 5.2.已过期，需要重建缓存
        // 6.缓存重建
        // 6.1.获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        // 6.2.判断是否获取锁成功
        if (isLock) {
            /**
             * 多线程高并发情况下需要再次检测缓存是否过期，否则会有多次查询数据库的情况
             * 如果没有过期，则无需缓存重建
             * */
            /*-------DoubleCheck缓存是否逻辑过期------------*/
            // 1.从redis查询商铺缓存
            shopJson = stringRedisTemplate.opsForValue().get(key);

            // 2.判断是否存在,
            if (StrUtil.isBlank(shopJson)) {//不存在，直接返回null
                // 3.存在,直接返回
                unlock(lockKey);
                return null;
            }
            // 4.命中，需要先把json反序列化为对象
            shop = JSONUtil.toBean((JSONObject) JSONUtil.toBean(shopJson, RedisData.class).getData(), Shop.class);
            expireTime = redisData.getExpireTime();

            // 5.判断是否过期
            if (expireTime.isAfter(LocalDateTime.now())) {
                // 5.1.未过期，直接返回店铺信息
                unlock(lockKey);
                return shop;
            }

            /*-------End_DoubleCheck缓存是否逻辑过期------------*/


            // 6.3.成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    //重建缓存
                    this.saveShopRedis(id, 20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    //释放锁
                    unlock(lockKey);
                }
            });

        }
        // 6.4.返回过期的商铺信息
        return shop;
    }

    public Shop queryWithMutex(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1.从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {//isNotBlank只有是有实际字符才是true;null和空字符串都是false
            // 3.存在,直接返回
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        /**
         * 这里是解决缓存穿透的一个方案,如果是null,就把空字符串暂时先缓存进redis
         * */
        // 判断命中的是否是空值
        if (shopJson != null) {//如果缓存的是空字符串 相当于.equals("");
            //返回错误信息
            return null;
        }

        /*
         * 实现缓存重建
         * */

        // 4.1.获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        Shop shop = null;
        try {
            boolean isLock = tryLock(lockKey);
            // 4.2.判断是否获取锁成功
            if (!isLock) {
                // 4.3.失败,则休眠并重试
                Thread.sleep(50);
                return queryWithMutex(id);
            }

            /*-----DoubleCheck缓存,如果存在,不需要重建缓存------*/

            // 1.从redis查询商铺缓存
            shopJson = stringRedisTemplate.opsForValue().get(key);

            // 2.判断是否存在
            if (StrUtil.isNotBlank(shopJson)) {//isNotBlank只有是有实际字符才是true;null和空字符串都是false
                // 3.存在,直接返回
                return JSONUtil.toBean(shopJson, Shop.class);
            }
            /*-----End - DoubleCheck缓存,如果存在,不需要重建缓存------*/


            // 4.4.成功,根据id查询数据库
            shop = getById(id);

            //模拟重建延时
            Thread.sleep(200);


            // 5.不存在,返回错误
            if (shop == null) {//说明该数据不在数据库里
                //将空值写入redis
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                //返回错误信息
                return null;
            }
            // 6.存在,写入Redis
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 7.释放互斥锁
            unlock(lockKey);
        }
        // 8.返回
        return shop;
    }

    public Shop queryWithPassThrough(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 1.从redis查询商铺缓存
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {//isNotBlank只有是有实际字符才是true;null和空字符串都是false
            // 3.存在,直接返回
            return JSONUtil.toBean(shopJson, Shop.class);
        }
        /**
         * 这里是解决缓存穿透的一个方案,如果是null,就把空字符串暂时先缓存进redis
         * */
        // 判断命中的是否是空值
        if (shopJson != null) {//如果缓存的是空字符串 相当于.equals("");
            //返回错误信息
            return null;
        }
        // 4.不存在,根据id向数据库查询
        Shop shop = getById(id);
        // 5.不存在,返回错误

        if (shop == null) {//说明该数据不在数据库里
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            //返回错误信息
            return null;
        }
        // 6.存在,写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 7.返回
        return shop;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);//相当于setnx
        return BooleanUtil.isTrue(flag);//自动拆箱机制,防止出现空指针异常
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    //expireSeconds :逻辑过期时间
    public void saveShopRedis(Long id, Long expireSeconds) throws InterruptedException {
        // 1.查询店铺数据
        Shop shop = getById(id);
        Thread.sleep(200);//模拟缓存重建需要一定延迟
        // 2.封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        // 3.写入redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 这里因为是一个单体项目,
     * 可以直接加事务保证原子性,
     * 如果是个分布式的就会麻烦一点
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 1.更新数据库
        updateById(shop);
        // 2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return null;
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 1.判断是否需要根据坐标查询
        if (x == null || y == null){
            //不需要坐标查询,按数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return Result.ok(page.getRecords());
        }
        // 2.计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        // 3.查询redis、按照距离排序、分页。结果：shopId、distance
        String key = SHOP_GEO_KEY + typeId;

        // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        // 4.解析出id
        if (results == null) {
            return Result.ok(Collections.emptyList());
        }

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // 没有下一页了，结束
            return Result.ok(Collections.emptyList());
        }
        // 4.1.截取 from ~ end的部分
        List<Long> ids = new ArrayList<>(list.size());
        //key : 店铺id
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 4.2.获取店铺id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            // 4.3.获取距离
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        // 5.根据id查询Shop
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 6.返回
        return Result.ok(shops);

    };
}
