package com.shcm.utils;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/13 22:03
 */

public interface ILock {


    /**
     * 尝试获取锁
     *
     * @param timeoutSec 锁持有的超时时间，过期后自动释放
     * @return true代表获取锁成功，false获取锁失败
     */
    public boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();


}
