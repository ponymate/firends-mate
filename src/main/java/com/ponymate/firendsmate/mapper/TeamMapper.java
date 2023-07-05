package com.ponymate.firendsmate.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ponymate.firendsmate.model.domain.Team;
import com.ponymate.firendsmate.model.vo.TeamUserVO;

import java.util.Date;
import java.util.List;

/**
* @author 22121
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-03-13 20:15:26
* @Entity com.ponymate.friendmate.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    List<TeamUserVO> listTeamsBySql(Long id, List<Long> idList, String searchText, String name, String description,  Integer maxNum,  Long userId,  Integer status,boolean isAdmin);

    List<Team> listTeamWithDelete(Date fiveMinutesAgoDate);
}




