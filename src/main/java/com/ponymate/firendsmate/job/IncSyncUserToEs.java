package com.ponymate.firendsmate.job;

import com.ponymate.firendsmate.dao.UserEsDao;
import com.ponymate.firendsmate.mapper.UserMapper;
import com.ponymate.firendsmate.model.domain.User;
import com.ponymate.firendsmate.model.es.UserEsDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步帖子到 es
 *
 * @author ponymate
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncUserToEs {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserEsDao userEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 10 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 10 * 60 * 1000L);
        List<User> userList = userMapper.listUserWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(userList)) {
            log.info("no inc user");
            return;
        }
        List<UserEsDTO> userEsDTOList = userList.stream()
                .map(UserEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = userEsDTOList.size();
        log.info("IncSyncUserToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            userEsDao.saveAll(userEsDTOList.subList(i, end));
        }
        log.info("IncSyncUserToEs end, total {}", total);
    }
}
