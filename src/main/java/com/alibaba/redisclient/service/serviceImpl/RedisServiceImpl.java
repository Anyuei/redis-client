package com.alibaba.redisclient.service.serviceImpl;

import com.alibaba.redisclient.util.ThreadPoolUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * @author pei
 * @date 2023/8/24 10:21
 */
@Slf4j
@Data
public class RedisServiceImpl {

    public static final int NUM_CONNECTIONS_ACTIVE_EVERY_SECOND = 600;

    public static final int NUM_CONNECTIONS_ACTIVE_EVERY_MINUTES = 400;

    public static String REDIS_HOST = "r-bp1e72xo07rlzy9ccd.redis.rds.aliyuncs.com";

    public static int REDIS_PORT = 6379;

    public static String PASSWORD = "Yaoyou@123456";

    //运行状态，用于暂停压测
    public static Boolean running = false;

    //是否结束压测，用于关闭压测
    public static Boolean shutdown = false;

    public static void main(String[] args) {
        connectToRedisAndSendPackets();
    }

    public static AtomicInteger threadNum=new AtomicInteger(0);
    /**
     * 连接redis并发送数据
     */
    public synchronized static String connectToRedisAndSendPackets() {
        if (running) {
            return "program is running";
        }
        running = true;
        //建立一千个线程的线程池
        ThreadPoolUtil threadPoolUtil = new ThreadPoolUtil(1000);

        RedisConnectionFactory connectionFactory = createConnectionFactory();
        try {
            connectionFactory.getConnection().isClosed();
        } catch (Exception e) {
            log.error("redis连接失败 ,info：" + e.getMessage());
            return e.getMessage();
        }

        //600个线程，每个线程每秒发一次数据
        for (int i = 0; i < NUM_CONNECTIONS_ACTIVE_EVERY_SECOND; i++) {
            RedisTemplate<String, String> redisTemplate=createRedisTemplate(connectionFactory);
            threadPoolUtil.execute(() -> {
                log.info("创建了新线程"+threadNum.addAndGet(1)+","+Thread.currentThread().getName());
                while (!shutdown) {
                    log.info("线程："+Thread.currentThread().getName()+"运行了一次");
                    if (running) {
                        Random random = new Random();
                        int key = random.nextInt(10);
                        int value = random.nextInt(10);
                        redisTemplate.opsForValue().set("DAN_HE_TEST_" + key, value + "");
                        log.info("redis insert data K:" + "DAN_HE_TEST_" + key + "    V:" + value);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            });
        }

        //400个线程 每分钟发一次数据
        for (int i = 0; i < NUM_CONNECTIONS_ACTIVE_EVERY_MINUTES; i++) {
            RedisTemplate<String, String> redisTemplate=createRedisTemplate(connectionFactory);
            threadPoolUtil.execute(() -> {
                log.info("创建了新线程"+threadNum.addAndGet(1));
                while (!shutdown) {
                    log.info("线程："+Thread.currentThread().getName()+"运行了一次");
                    if (running) {
                        Random random = new Random();
                        int key = random.nextInt(10);
                        int value = random.nextInt(10);
                        redisTemplate.opsForValue().set("DAN_HE_TEST_" + key, value + "");
                        log.info("redis insert data K:" + "DAN_HE_TEST_" + key + "    V:" + value);
                    }
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            });
        }
        return "success";
    }

    public static RedisConnectionFactory createConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(REDIS_HOST);
        connectionFactory.setPort(REDIS_PORT);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    public static RedisTemplate<String, String> createRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
