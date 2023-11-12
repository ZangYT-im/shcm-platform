package com.shcm.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.dto.Result;
import com.shcm.entity.Shop;
import com.shcm.mapper.ShopMapper;
import com.shcm.service.IShopService;
import lombok.val;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.shcm.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {

        //缓存穿透
//        Shop shop = queryWithPassThrough(id);


        //用互斥锁解决缓存击穿,包含了缓存穿透
        Shop shop = queryWithMutex(id);
        if (shop == null){
            return Result.fail("店铺不存在");
        }
        return Result.ok(shop);



    }

    public Shop queryWithMutex(Long id){
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
    public Shop queryWithPassThrough(Long id){
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
}
