package com.shcm.utils;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/13 22:07
 */

public class SimpleRedisLock implements ILock {

    private String name;
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX = "lock:";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        //ClassPath去resource目录下去找
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryLock(long timeoutSec) {
        /**
         * 优化线程id:
         * 由于分布式情况下多个jvm，线程id有存在相同的可能行
         * 所以给他拼接一个uuid保证唯一性
         * */
        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);//如果是null，返回false
    }

    /**
     * 使用lua脚本释放锁，来保证原子性
     */
    @Override
    public void unlock() {
        /**
         * 释放锁需要优化
         * 极端情况下有可能加锁的和释放锁的线程id不一致
         * 所以要保证加锁和释放锁的线程是同一个
         * */
        //调用lua脚本
        stringRedisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().getId());
    }

    /**
     * 没有用lua脚本，只用java代码检查线程id和释放锁这两个步不能够保证原子性
     * */
    /*
      @Override
    public void unlock() {

         * 释放锁需要优化，
         * 极端情况下有可能加锁的和释放锁的线程id不一致
         * 所以要保证加锁和释放锁的线程是同一个

    //获取线程标识
    String threadId = ID_PREFIX + Thread.currentThread().getId();
    //获取锁中的标识
    String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
    //判断标识是否一致，不一致就不释放
        if (threadId.equals(id)){
        //释放锁
        stringRedisTemplate.delete(KEY_PREFIX + name);
    }

    * */


}
