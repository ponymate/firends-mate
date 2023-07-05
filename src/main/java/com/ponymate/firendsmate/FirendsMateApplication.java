package com.ponymate.firendsmate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author 22121
 */
@SpringBootApplication
@EnableRedisHttpSession
@EnableScheduling
@MapperScan("com.ponymate.firendsmate.mapper")
public class FirendsMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirendsMateApplication.class, args);
    }

}
