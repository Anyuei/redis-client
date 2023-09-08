package com.alibaba.redisclient.controller;

import com.alibaba.redisclient.entity.RedisSetRequest;
import com.alibaba.redisclient.service.serviceImpl.RedisServiceImpl;
import com.alibaba.redisclient.util.SocketClient2;
import com.alibaba.redisclient.util.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.alibaba.redisclient.service.serviceImpl.RedisServiceImpl.*;

/***
 * @author pei
 * @date 2023/8/24 10:18
 */
@RestController
@RequestMapping("/")
@Slf4j
public class TestController {
    @PostMapping("/start")
    public String start() {
        log.info("压测开始");
        connectToRedisAndSendPackets();
        return "success";
    }

    @PostMapping("/testRedis")
    public String testRedis() {
        try {
            RedisConnectionFactory connectionFactory = createConnectionFactory();
            connectionFactory.getConnection().isClosed();
        } catch (Exception e) {
            return "redis connect error：" + e.getMessage();
        }
        return "redis connect success,"+ REDIS_HOST;
    }


    /**
     * 修改redis连接设置，修改设置后，需要shutdown线程后，重新start生效
     *
     * @param redisSetRequest
     * @return
     */
    @PostMapping("/set")
    public String set(@RequestBody RedisSetRequest redisSetRequest) {
        RedisServiceImpl.REDIS_HOST = redisSetRequest.getHost();
        RedisServiceImpl.REDIS_PORT = redisSetRequest.getPort();
        RedisServiceImpl.PASSWORD = redisSetRequest.getPassword();
        return "success";
    }

    @PostMapping("/shutdown")
    public String shutdown() {
        RedisServiceImpl.shutdown = true;
        running = false;
        log.info("压测结束");
        return "success";
    }

    @PostMapping("/stop")
    public String stop() {
        RedisServiceImpl.running = false;
        log.info("压测暂停");
        return "success";
    }

    @PostMapping("/run")
    public String run() {
        RedisServiceImpl.running = true;
        log.info("压测继续执行");
        return "success";
    }

    @GetMapping("/startSocket")
    public String startSocket(String host, int port, Integer num) throws IOException {
        SocketUtil.sendRequest(host,port,num);
        log.info("压测Socket执行完毕");
        return "success";
    }

    @GetMapping("/startSocketClientAndSendData")
    public String startSocket(Integer seconds,String ip,Integer port) throws IOException, InterruptedException {
        SocketClient2.startSocketClient(seconds,ip,port);
        log.info("开启socket客户端发生数据");
        return "success";
    }

    @PostMapping("/closeSocket")
    public String closeSocket() {
        SocketUtil.closeSocket();
        log.info("关闭所有Socket");
        return "success";
    }
}
