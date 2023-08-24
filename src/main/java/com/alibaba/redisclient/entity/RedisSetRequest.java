package com.alibaba.redisclient.entity;

import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

/***
 * @author pei
 * @date 2023/8/24 10:29
 */
@Data
public class RedisSetRequest {

    //主机
    private String host;

    //端口
    private int port;

    //密码
    private String password;
}
