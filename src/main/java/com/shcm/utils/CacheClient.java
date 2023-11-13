package com.shcm.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shcm.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.shcm.utils.RedisConstants.*;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/13 2:40
 */

@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;

    //缓存重建需要用的线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        //设置逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    public <R,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                         Long time, TimeUnit unit) {
        String key = CACHE_SHOP_KEY + id;
        // 1.从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在
        if (StrUtil.isNotBlank(json)) {//isNotBlank只有是有实际字符才是true;null和空字符串都是false
            // 3.存在,直接返回
            return JSONUtil.toBean(json, type);
        }
        /**
         * 这里是解决缓存穿透的一个方案,如果是null,就把空字符串暂时先缓存进redis
         * */
        // 判断命中的是否是空值
        if (json != null) {//如果缓存的是空字符串 相当于.equals("");
            //返回错误信息
            return null;
        }
        // 4.不存在,根据id向数据库查询
        R r = dbFallback.apply(id);

        // 5.不存在,返回错误
        if (r == null) {//说明该数据不在数据库里
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            //返回错误信息
            return null;
        }
        // 6.存在,写入Redis
        this.set(key,r,time,unit);
        // 7.返回
        return r;
    }


    public <R,ID> R queryWithLogicalExpire(String kerPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                           Long time, TimeUnit unit)  {
        String key = kerPrefix + id;
        // 1.从redis查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在,
        if (StrUtil.isBlank(json)) {//不存在，直接返回null
            // 3.存在,直接返回
            return null;
        }
        // 4.命中，需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();

        // 5.判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 5.1.未过期，直接返回店铺信息
            return r;
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
            json = stringRedisTemplate.opsForValue().get(key);

            // 2.判断是否存在,
            if (StrUtil.isBlank(json)) {//不存在，直接返回null
                // 3.存在,直接返回
                unlock(lockKey);
                return null;
            }
            // 4.命中，需要先把json反序列化为对象
            r = JSONUtil.toBean((JSONObject) JSONUtil.toBean(json, RedisData.class).getData(), type);
            expireTime = redisData.getExpireTime();

            // 5.判断是否过期
            if (expireTime.isAfter(LocalDateTime.now())) {
                // 5.1.未过期，直接返回店铺信息
                unlock(lockKey);
                return r;
            }

            /*-------End_DoubleCheck缓存是否逻辑过期------------*/


            // 6.3.成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    //重建缓存
                    R r1 = dbFallback.apply(id);
                    this.setWithLogicalExpire(key,r1,time,unit);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    //释放锁
                    unlock(lockKey);
                }
            });

        }
        // 6.4.返回过期的商铺信息
        return r;
    }
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);//相当于setnx
        return BooleanUtil.isTrue(flag);//自动拆箱机制,防止出现空指针异常
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

}




