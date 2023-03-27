package com.ponymate.firendsmate.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ponymate.firendsmate.common.BaseResponse;
import com.ponymate.firendsmate.common.DeleteRequest;
import com.ponymate.firendsmate.common.ErrorCode;
import com.ponymate.firendsmate.common.ResultUtils;
import com.ponymate.firendsmate.exception.BusinessException;
import com.ponymate.firendsmate.exception.ThrowUtils;
import com.ponymate.firendsmate.model.domain.Team;
import com.ponymate.firendsmate.model.domain.User;
import com.ponymate.firendsmate.model.domain.UserTeam;
import com.ponymate.firendsmate.model.dto.Team.*;
import com.ponymate.firendsmate.model.vo.TeamUserVO;
import com.ponymate.firendsmate.service.TeamService;
import com.ponymate.firendsmate.service.UserService;
import com.ponymate.firendsmate.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  队伍接口
 *
*/
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    TeamService teamService;

    @Resource
    UserService userService;

    @Resource
    UserTeamService userTeamService;

    /**
     * 添加队伍
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        ThrowUtils.throwIf(teamAddRequest==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        Team team = new Team();
        User loginUser = userService.getLoginUser(request);
        BeanUtils.copyProperties(teamAddRequest,team);
        return ResultUtils.success(teamService.addTeam(team,loginUser));
    }

    /**
     * 根据id获得队伍
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询所有符合条件的队伍（通过写sql联表查询）
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQueryRequest teamQueryRequest,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser==null||teamQueryRequest==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        // 1.获取队伍列表（通过sql方式)
        List<TeamUserVO> teams =teamService.listTeamsBySql(teamQueryRequest, userService.isAdmin(loginUser));
        // 2、判断当前用户是否已加入队伍
        teams = teamService.setHasJoin(teams,loginUser);
        return ResultUtils.success(teams);
    }

    /**
     * 分页查询所有符合条件的队伍（通过wrapper）
     * @param teamQuery
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<TeamUserVO>> listTeamsByPage(TeamQueryRequest teamQuery,HttpServletRequest request) {
        //检查变量
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser==null||teamQuery==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        //1.获取队伍列表（通过wrapper的方式）
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        Wrapper<Team> wrapper = teamService.getWrapper(teamQuery, userService.isAdmin(loginUser));
        Page<TeamUserVO> resultPage = teamService.listTeamsByPage(page, wrapper);
        List<TeamUserVO> records = resultPage.getRecords();
        // 2、判断当前用户是否已加入队伍
        records = teamService.setHasJoin(records,loginUser);
        return ResultUtils.success(resultPage);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQueryRequest teamQuery, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(teamQuery==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));

        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery,true);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQueryRequest teamQuery, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(teamQuery==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));

        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(teamUpdateRequest==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));

        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        ThrowUtils.throwIf(!result,new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败"));
        return ResultUtils.success(true);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(teamJoinRequest==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));

        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(teamQuitRequest==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));

        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 删除队伍
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(deleteRequest==null||loginUser==null,new BusinessException(ErrorCode.SYSTEM_ERROR));
        long id = deleteRequest.getId();
        boolean result = teamService.deleteTeam(id, loginUser);
        ThrowUtils.throwIf(!result,new BusinessException(ErrorCode.SYSTEM_ERROR));
        return ResultUtils.success(true);
    }

}
