package com.ponymate.firendsmate.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ponymate.firendsmate.mapper.UserMapper;
import com.ponymate.firendsmate.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 21:52 2023/3/12
 */
@Component
@Slf4j
public class ScheduledRedis {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    UserMapper userMapper;

    @Resource
    RedisTemplate<String,Object> redisTemplate;


    /**
     * 定时任务，每天5点刷新数据
     */
    @Scheduled(cron="0 0 5 * * *")
    public void cacheRecommendUsers() {
        RLock lock = redissonClient.getLock("preheat:recommend");
        try {
            boolean isGetLock = lock.tryLock(0L, 30L, TimeUnit.SECONDS);
            if(!isGetLock){
                return ;
            }
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            List<User> userList = userMapper.selectList(wrapper);
            try {
                redisTemplate.opsForValue().set("recommend",userList , 1 , TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("redis set key error",e);
            }
        } catch (InterruptedException e) {
            log.error("redis set key error",e);
        }finally{
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
