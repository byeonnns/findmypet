package com.findmypet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession(
        maxInactiveIntervalInSeconds = 1800,  // 세션 유효기간 30분
        redisNamespace = "findmypet:sessions" // Redis key prefix
)
public class RedisHttpSessionConfig {
}
