package com.shcm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.dto.Result;
import com.shcm.entity.SeckillVoucher;
import com.shcm.entity.VoucherOrder;
import com.shcm.mapper.VoucherOrderMapper;
import com.shcm.service.ISeckillVoucherService;
import com.shcm.service.IVoucherOrderService;
import com.shcm.utils.RedisIdWorker;
import com.shcm.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Slf4j
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
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT_STREAM;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        //ClassPath去resource目录下去找
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);

        SECKILL_SCRIPT_STREAM = new DefaultRedisScript<>();
        //ClassPath去resource目录下去找
        SECKILL_SCRIPT_STREAM.setLocation(new ClassPathResource("seckill_stream.lua"));
        SECKILL_SCRIPT_STREAM.setResultType(Long.class);
    }
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    // 获取代理对象（事务）
    IVoucherOrderService proxy;

    /**
     * 该注解意思是在当前类初始化完毕后执行
     */
    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandle());
    }
    private class VoucherOrderHandle implements Runnable {
        String queueName = "stream.orders";
        @Override
        public void run() {
            while (true) {

                try {
                    // 1.获取消息队列Redis中的Stream队列中的订单信息
                    // XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.orders >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    // 2.判断消息是否获取成功
                    if (list == null || list.isEmpty()){
                        // 2.1.如果获取失败，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 3.解析订单中的信息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    // 4.如果获取成功，可以下单
                    handleVoucherOrder(voucherOrder);
                    // 5.ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    try {
                        handlePendingList();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }

        }

        private void handlePendingList() throws InterruptedException {
            while (true) {
                try {
                    // 1.获取pending-list中的订单信息
                    // XREADGROUP GROUP g1 c1 COUNT 1 STREAMS stream.orders 0
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    // 2.判断消息是否获取成功
                    if (list == null || list.isEmpty()){
                        // 2.1.如果获取失败，说明pending-list中没有异常消息，结束循环
                        break;
                    }
                    // 3.解析订单中的信息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    // 4.如果获取成功，可以下单
                    handleVoucherOrder(voucherOrder);
                    // 5.ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());
                } catch (Exception e) {
                    log.error("处理pending-list订单异常", e);
                    Thread.sleep(20);
                }

            }
        }
    }


    /**
     * 这段是异步的阻塞队列，
     * 采用redis的Stream流可以代替
     * */
//    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
//    private class VoucherOrderHandle implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//
//                try {
//                    // 1.获取队列中的订单信息
//                    VoucherOrder voucherOrder = orderTasks.take();
//                    // 2.创建订单
//                    handleVoucherOrder(voucherOrder);
//                } catch (InterruptedException e) {
//                    log.error("处理订单异常", e);
//                }
//
//            }
//
//        }
//    }

    private void handleVoucherOrder(VoucherOrder voucherOrder) {

        /**
         * 这里去userId不能从userHold里取了，
         * 因为这里是用了多线程，threadLocal里取不到
         * */
        // 1.获取用户
        Long userId = voucherOrder.getVoucherId();

        /**
         * 采用redisson分布式锁
         * 这里也是兜底，理论不会有线程安全问题，
         * 前面判断过了，这里不加锁也没问题
         * */
        // 2.创建锁对象
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // 3.获取锁
        boolean isLock = lock.tryLock();//无参就是失败后不等待


        // 4.判断获取锁是否成功
        if (!isLock) {
            /**
             * 这里这次理论上不会再出现重复下单了，因为之前在redis里已经判断过了
             * 之前这里是通过查询数据库判断重复下单，效率太低，
             * 这次是异步解耦，订单信息已经在redis里了，之前已经做过判断了
             * 所以这里是再次兜底，以防万一吧
             * */
            //获取锁失败，返回错误或重试
            log.error("一个人只允许一下单，不允许重复下单");
            return;
        }
        try {

            /**
             * 这里也不用spring的代理来获取对象了，
             * 因为currentProxy底层也是threadLocal
             * 解决办法：
             *  1.把代理对象提前在主线程里获取
             *  2.放到成员变量里
             * */
            proxy.creatVoucherOrder(voucherOrder);
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    /**
     * 秒杀优惠券，异步解耦优化版
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        // 获取用户
        Long userId = UserHolder.getUser().getId();
        // 获取订单id
        long orderId = redisIdWorker.nextId("order");
        /**
         * 该脚本实现：
         *  1.判断用户是否有资格
         *  2.库存是否充足
         * */
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT_STREAM,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(),String.valueOf(orderId)
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }

        /**
         * 下面的逻辑采用Stream时在上面的lua脚本里已经完成了，
         * 就不需要在java中异步执行
         * */
//        // 2.2.为0，有购买资格，创建订单，保存到阻塞队列
//        VoucherOrder voucherOrder = new VoucherOrder();
//        // 2.3.获取订单id
//        long orderId = redisIdWorker.nextId("order");
//        voucherOrder.setId(orderId);
//        // 2.4.用户id
//        voucherOrder.setUserId(userId);
//        // 2.5.代金券id
//        voucherOrder.setVoucherId(voucherId);


        // 3.获取代理对象（事务）
        proxy = (IVoucherOrderService) AopContext.currentProxy();

//        // 2.6.放入阻塞队列
//        orderTasks.add(voucherOrder);

        // 4.返回订单id
        return Result.ok(orderId);
    }


    /**
     * 秒杀优惠券，没有实现异步解耦，效率较低
     */
//    @Override
//    public Result seckillVoucher2(Long voucherId) {
//        // 1.查询优惠券
//        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
//        // 2.判断秒杀是否开始
//        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
//            // 尚未开始
//            return Result.fail("秒杀尚未开始！");
//        }
//        // 3.判断秒杀是否已经结束
//        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
//            // 尚未开始
//            return Result.fail("秒杀已经结束！");
//        }
//        // 4.判断库存是否充足
//        if (voucher.getStock() < 1) {
//            // 库存不足
//            return Result.fail("库存不足！");
//        }
//
//        /**
//         * 这里还有个事务场景之一，this调用
//         * 解决是用动态代理获取对象，这样事务就不会失效了
//         * 用动态代理需要的改变：
//         *  1.添加依赖<groupId>org.aspectj</groupId>
//         *             <artifactId>aspectjweaver</artifactId>
//         *  2.在启动类上添加注解@EnableAspectJAutoProxy(exposeProxy = true)//暴露动态代理的对象
//         * */
//        Long userId = UserHolder.getUser().getId();
//        ////intern代表去字符串常量池里去找，因为toString底层是new String()
//
//
//        /**
//         * 采用redisson分布式锁
//         *
//         * */
//        RLock lock = redissonClient.getLock("lock:order:" + userId);
//        boolean isLock = lock.tryLock();//无参就是失败后不等待
//
//
//        /**
//         * 通过手动实现的redis分布式锁来实现
//         * */
//        // 创建redis锁对象
////        SimpleRedisLock lock = new SimpleRedisLock("order:" + userId, stringRedisTemplate);
//
//        //获取锁
////        boolean isLock = lock.tryLock(1200);//设置一个时间超时自动释放，防止锁不能够正常手动释放
//        //判断获取锁是否成功
//        if (!isLock) {
//            //获取锁失败，返回错误或重试
//            return Result.fail("一个人只允许一下单，不允许重复下单");
//        }
//        try {
//            // 获取代理对象（事务）
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.creatVoucherOrder(voucherId);
//        } finally {
//            //释放锁
//            lock.unlock();
//        }


        /**
         * 通过syn关键字来实现，只能保证单体，不能保证分布式情况下线程安全
         * */
//        synchronized (userId.toString().intern()) {
//            // 获取代理对象（事务）
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.creatVoucherOrder(voucherId);
//        }


    @Transactional
    public void creatVoucherOrder(VoucherOrder voucherOrder) {

        /**
         * 这里有个问题，如果释放锁后事务还没提交，
         * 其他线程进入，依然会有线程安全问题
         * 所以锁的范围应该是整个函数，也就说事务提交后再释放锁是合理void
         * */
        // 5.一人一单
        Long userId = voucherOrder.getUserId();
        // 5.1.查询订单
        int count = query().eq("user_id", userId).
                eq("voucher_id", voucherOrder.getVoucherId()).count();
        // 5.2.判断是否存在
        if (count > 0) {
            // 用户已经购买过了
            /**
             * 这里也是不可能出现重复
             * */
            log.error("抱歉，不可重复购买");
            return;
        }

        // 6.扣减库存
        boolean success = seckillVoucherService.update()
                .setSql("stock = stock - 1")
                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock", 0)
                .update();//where id = ? and stock = ? 乐观锁cas判断
        if (!success) {
            log.error("库存不足！");
            return;
        }

//        //7.创建订单
//        VoucherOrder voucherOrder = new VoucherOrder();
//        // 7.1.订单id
//        long orderId = redisIdWorker.nextId("order");
//        voucherOrder.setId(orderId);
//        // 7.2.用户id
//        voucherOrder.setUserId(userId);
//        // 7.3.代金券id
//        voucherOrder.setVoucherId(voucherOrder);
        save(voucherOrder);
        /**
         * 这里也不需要返回id，因为现在是异步执行
         * */
//        return Result.ok(orderId);

    }
}
