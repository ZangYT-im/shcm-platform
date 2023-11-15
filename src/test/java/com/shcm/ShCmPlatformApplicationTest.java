package com.shcm;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.Shop;
import com.shcm.entity.User;
import com.shcm.mapper.UserMapper;
import com.shcm.service.IUserService;
import com.shcm.service.impl.ShopServiceImpl;
import com.shcm.utils.CacheClient;
import com.shcm.utils.RedisIdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.shcm.utils.RedisConstants.CACHE_SHOP_KEY;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/13 1:41
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class ShCmPlatformApplicationTest {

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private RedisIdWorker redisIdWorker;

    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    public void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id:" + id);
                
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));

    }

    @Test
    public void testSaveShop() throws InterruptedException {
        Shop shop = shopService.getById(1L);
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 1L, shop, 10L, TimeUnit.SECONDS);
    }
    @Resource
    ServiceImpl<UserMapper, User> service;

    @Resource
    IUserService iUserService;

    @Test
    public void test(){
        User user = iUserService.query().eq("phone", "13361337361").one();
        System.out.println(user.toString());

    }
}
