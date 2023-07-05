package com.ponymate.firendsmate.job;

import com.ponymate.firendsmate.dao.TeamEsDao;
import com.ponymate.firendsmate.mapper.TeamMapper;
import com.ponymate.firendsmate.model.domain.Team;
import com.ponymate.firendsmate.model.es.TeamEsDTO;
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
public class IncSyncTeamToEs {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private TeamEsDao teamEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 10 分钟内的数据
        Date fiveMinutesAgoDate = new Date(System.currentTimeMillis() - 10 * 60 * 1000L);
        List<Team> teamList = teamMapper.listTeamWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(teamList)) {
            log.info("no inc team");
            return;
        }
        List<TeamEsDTO> teamEsDTOList = teamList.stream()
                .map(TeamEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = teamEsDTOList.size();
        log.info("IncSyncTeamToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            teamEsDao.saveAll(teamEsDTOList.subList(i, end));
        }
        log.info("IncSyncTeamToEs end, total {}", total);
    }
}
