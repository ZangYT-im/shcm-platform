package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.dto.Result;
import com.shcm.entity.SeckillVoucher;
import com.shcm.entity.VoucherOrder;
import com.shcm.mapper.VoucherOrderMapper;
import com.shcm.service.ISeckillVoucherService;
import com.shcm.service.IVoucherOrderService;
import com.shcm.utils.RedisIdWorker;
import com.shcm.utils.SimpleRedisLock;
import com.shcm.utils.UserHolder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;


@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;


    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        //ClassPath去resource目录下去找
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * 秒杀优惠券，异步解耦优化版
     * */
    @Override
    public Result seckillVoucher(Long voucherId) {
        //获取用户
        Long userId = UserHolder.getUser().getId();
        long orderId = redisIdWorker.nextId("order");
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }

        // 2.2.位0，有购买资格，保存到阻塞队列
        //TODO 保存阻塞队列
        // 3.返回订单id
        return Result.ok(orderId);
    }


    /**
     * 秒杀优惠券，没有实现异步解耦，效率较低
     * */
//    @Override
    public Result seckillVoucher2(Long voucherId) {
        // 1.查询优惠券
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // 2.判断秒杀是否开始
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀尚未开始！");
        }
        // 3.判断秒杀是否已经结束
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("秒杀已经结束！");
        }
        // 4.判断库存是否充足
        if (voucher.getStock() < 1) {
            // 库存不足
            return Result.fail("库存不足！");
        }

        /**
         * 这里还有个事务场景之一，this调用
         * 解决是用动态代理获取对象，这样事务就不会失效了
         * 用动态代理需要的改变：
         *  1.添加依赖<groupId>org.aspectj</groupId>
         *             <artifactId>aspectjweaver</artifactId>
         *  2.在启动类上添加注解@EnableAspectJAutoProxy(exposeProxy = true)//暴露动态代理的对象
         * */
        Long userId = UserHolder.getUser().getId();
        ////intern代表去字符串常量池里去找，因为toString底层是new String()


        /**
         * 采用redisson分布式锁
         *
         * */
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        boolean isLock = lock.tryLock();//无参就是失败后不等待


        /**
         * 通过手动实现的redis分布式锁来实现
         * */
           // 创建redis锁对象
//        SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);

        //获取锁
//        boolean isLock = lock.tryLock(1200);//设置一个时间超时自动释放，防止锁不能够正常手动释放
        //判断获取锁是否成功
        if (!isLock){
            //获取锁失败，返回错误或重试
            return Result.fail("一个人只允许一下单，不允许重复下单");
        }
        try {
            // 获取代理对象（事务）
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.creatVoucherOrder(voucherId);
        }finally {
            //释放锁
            lock.unlock();
        }






        /**
         * 通过syn关键字来实现，只能保证单体，不能保证分布式情况下线程安全
         * */
//        synchronized (userId.toString().intern()) {
//            // 获取代理对象（事务）
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.creatVoucherOrder(voucherId);
//        }
    }

    @Transactional
    public Result creatVoucherOrder(Long voucherId) {

        /**
         * 这里有个问题，如果释放锁后事务还没提交，
         * 其他线程进入，依然会有线程安全问题
         * 所以锁的范围应该是整个函数，也就说事务提交后再释放锁是合理的
         * */
        // 5.一人一单
        Long userId = UserHolder.getUser().getId();
        // 5.1.查询订单
        int count = query().eq("user_id", userId).
                eq("voucher_id", voucherId).count();
        // 5.2.判断是否存在
        if (count > 0) {
            // 用户已经购买过了
            return Result.fail("抱歉，不可重复购买");
        }

        // 6.扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherId).gt("stock", 0)
                .update();//where id = ? and stock = ? 乐观锁cas判断
        if (!success) {
            //扣减库存
            return Result.fail("库存不足！");
        }

        //7.创建订单
        VoucherOrder voucherOrder = new VoucherOrder();
        // 7.1.订单id
        long orderId = redisIdWorker.nextId("order");
        voucherOrder.setId(orderId);
        // 7.2.用户id
        voucherOrder.setUserId(userId);
        // 7.3.代金券id
        voucherOrder.setVoucherId(voucherId);
        save(voucherOrder);
        return Result.ok(orderId);

    }
}
