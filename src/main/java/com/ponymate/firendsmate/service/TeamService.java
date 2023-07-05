package com.ponymate.firendsmate.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ponymate.firendsmate.model.domain.Team;
import com.ponymate.firendsmate.model.domain.User;
import com.ponymate.firendsmate.model.dto.Team.TeamJoinRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamQueryRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamQuitRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamUpdateRequest;
import com.ponymate.firendsmate.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 22121
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-03-13 20:15:26
*/
public interface TeamService extends IService<Team> ,SearchService<TeamUserVO>{

    Long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeamsBySql(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    Wrapper<Team> getWrapper(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    Page<TeamUserVO> listTeamsByPage(Page<Team> page, Wrapper<Team> wrapper);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(long id, User loginUser);


    List<TeamUserVO> setHasJoin(List<TeamUserVO> records,User loginUser);
}
