package com.ponymate.firendsmate.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ponymate.firendsmate.common.ErrorCode;
import com.ponymate.firendsmate.contant.TeamStatusEnum;
import com.ponymate.firendsmate.exception.BusinessException;
import com.ponymate.firendsmate.exception.ThrowUtils;
import com.ponymate.firendsmate.mapper.TeamMapper;
import com.ponymate.firendsmate.mapper.UserTeamMapper;
import com.ponymate.firendsmate.model.domain.Team;
import com.ponymate.firendsmate.model.domain.User;
import com.ponymate.firendsmate.model.domain.UserTeam;
import com.ponymate.firendsmate.model.dto.Team.TeamJoinRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamQueryRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamQuitRequest;
import com.ponymate.firendsmate.model.dto.Team.TeamUpdateRequest;
import com.ponymate.firendsmate.model.vo.TeamUserVO;
import com.ponymate.firendsmate.model.vo.UserVO;
import com.ponymate.firendsmate.service.TeamService;
import com.ponymate.firendsmate.service.UserService;
import com.ponymate.firendsmate.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 22121
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-03-13 20:15:26
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Resource
    UserService userService;

    @Resource
    TeamMapper teamMapper;

    @Resource
    UserTeamMapper userTeamMapper;

    @Resource
    UserTeamService userTeamService;

    @Resource
    RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTeam(Team team, User loginUser) {
        //参数不为空
        ThrowUtils.throwIf(team==null||loginUser==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        //队伍 人数<20 and >1
        ThrowUtils.throwIf(team.getMaxNum()==null||team.getMaxNum()>20||team.getMaxNum()<1,new BusinessException(ErrorCode.PARAMS_ERROR));
        //标题 <20
        ThrowUtils.throwIf(team.getName()==null||team.getName().length()>20,new BusinessException(ErrorCode.PARAMS_ERROR));
        //描述<512
        Optional.ofNullable(team.getDescription()).orElse("无描述");
        ThrowUtils.throwIf(team.getDescription().length()>512,new BusinessException(ErrorCode.PARAMS_ERROR));
        //根据队伍状态 查询密码是否为空
        ThrowUtils.throwIf(team.getStatus()==null|| TeamStatusEnum.SECRET.getValue()==team.getStatus()&&team.getPassword()==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        //超时时间>当前时间
        System.out.println(new Date());
        ThrowUtils.throwIf(team.getExpireTime()==null||new Date().after(team.getExpireTime()),new BusinessException(ErrorCode.PARAMS_ERROR));
        //最多创建五个队伍
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getUserId,loginUser.getId());
        Long teamCount = teamMapper.selectCount(wrapper);
        ThrowUtils.throwIf(teamCount>=5,new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍达到上限"));
        //插入队伍数据
        team.setUserId(loginUser.getId());
        int insert = teamMapper.insert(team);
        ThrowUtils.throwIf(insert==0,new BusinessException(ErrorCode.SYSTEM_ERROR,"插入队伍失败"));
        //插入队伍用户关系
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        insert = userTeamMapper.insert(userTeam);
        ThrowUtils.throwIf(insert==0,new BusinessException(ErrorCode.SYSTEM_ERROR,"插入队伍用户关系失败"));
        return team.getId();
    }

    /**
     *  通过sql语句进行查询，
     * @return
     */
    @Override
    public List<TeamUserVO> listTeamsBySql(@RequestParam TeamQueryRequest teamQueryRequest, boolean isAdmin){
        Long id = teamQueryRequest.getId();
        List<Long> idList = teamQueryRequest.getIdList();
        String searchText = teamQueryRequest.getSearchText();
        String name = teamQueryRequest.getName();
        String description = teamQueryRequest.getDescription();
        Integer maxNum = teamQueryRequest.getMaxNum();
        Long userId = teamQueryRequest.getUserId();
        Integer status = teamQueryRequest.getStatus();

        List<TeamUserVO> teamUserVOS = teamMapper.listTeamsBySql(id, idList, searchText, name, description, maxNum, userId, status, isAdmin);

        //添加队伍创建人的详细信息
        teamUserVOS.forEach(teamUserVO -> {
            User byId = userService.getById(teamUserVO.getUserId());
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(byId,userVO);
            teamUserVO.setCreateUser(userVO);
        });
        return teamUserVOS;
    }

    /**
     * 通过函数方式进行查询
     * @param teamQueryRequest
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin) {
        Wrapper<Team> wrapper = getWrapper(teamQueryRequest, isAdmin);
        List<Team> teams = teamMapper.selectList(wrapper);
        // 添加创建人信息
        ArrayList<TeamUserVO> teamUserVOS = new ArrayList<>();
        for (Team team :teams) {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            Long userId1 = team.getUserId();
            User byId = userService.getById(userId1);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(byId,userVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOS.add(teamUserVO);
        }
        return teamUserVOS;
    }

    /**
     * 通过函数方式分页查询
     * @param page
     * @param wrapper
     * @return
     */
    @Override
    public Page<TeamUserVO> listTeamsByPage(Page<Team> page, Wrapper<Team> wrapper) {
        Page<Team> teamPage = teamMapper.selectPage(page, wrapper);
        List<Team> records = teamPage.getRecords();
        // 添加创建人信息
        ArrayList<TeamUserVO> teamUserVOS = new ArrayList<>();
        for (Team team :records) {
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            User byId = userService.getById(team.getUserId());
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(byId,userVO);
            teamUserVO.setCreateUser(userVO);
            teamUserVOS.add(teamUserVO);
        }
        Page<TeamUserVO> teamUserVOPage = new Page<>(page.getCurrent(),page.getSize());
        teamUserVOPage.setRecords(teamUserVOS);
        return teamUserVOPage;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        Long id = teamUpdateRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0,ErrorCode.PARAMS_ERROR);

        Team oldTeam = this.getById(id);
        ThrowUtils.throwIf(oldTeam == null,new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在"));

        // 只有管理员或者队伍的创建者可以修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (teamUpdateRequest.getStatus().equals(TeamStatusEnum.SECRET.getValue())) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getById(teamId);
        //队伍不能过期
        Date expireTime = team.getExpireTime();
        ThrowUtils.throwIf(expireTime != null && expireTime.before(new Date()),new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期"));
        //队伍是否私有
        Integer status = team.getStatus();
        ThrowUtils.throwIf(status.equals(TeamStatusEnum.PRIVATE.getValue()), new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍"));
        //如果队伍加密，检查密码
        String password = teamJoinRequest.getPassword();
        if (status.equals(TeamStatusEnum.SECRET.getValue())) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }

        long userId = loginUser.getId();

        // 只有一个线程能获取到锁（锁的粒度为同一用户，同一队伍）
        RLock lock = redissonClient.getLock("team:"+teamId+":user:"+userId);
        try {
            // 抢到锁并执行
            if (lock.tryLock(0, 5, TimeUnit.SECONDS)) {
                // 不能加入超过5个队伍
                LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserTeam::getUserId, userId);
                long hasJoinNum = userTeamService.count(wrapper);
                ThrowUtils.throwIf(hasJoinNum > 5,new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍"));

                // 不能重复加入已加入的队伍
                wrapper.clear();
                wrapper.eq(UserTeam::getUserId, userId);
                wrapper.eq(UserTeam::getTeamId, teamId);
                long hasJoinTeam = userTeamService.count(wrapper);
                ThrowUtils.throwIf(hasJoinTeam > 0,new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍"));
                System.out.println("getLock: " + Thread.currentThread().getId());
                // 开始事务（粒度为队伍，防止队伍人数超过需要的人数）
                redisTemplate.multi();
                // 将用户加入队伍
                redisTemplate.opsForSet().add("team:"+teamId, userId);
                //redisTemplate.watch("team:"+teamId);
                // 获取队伍成员数量
                long teamHasJoinNum = this.countUsersInTeam(teamId);
                ThrowUtils.throwIf(teamHasJoinNum >= team.getMaxNum(),new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满"));
                // 修改队伍信息
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                userTeamService.save(userTeam);

                teamHasJoinNum = this.countUsersInTeam(teamId);
                ThrowUtils.throwIf(teamHasJoinNum > team.getMaxNum(),new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满"));
                // 提交事务
                List<Object> results = redisTemplate.exec();

                return results!=null;
            }
        } catch (Exception e) {
            log.error("doCacheRecommendUser error", e);
            redisTemplate.discard();
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                //redisTemplate.unwatch();
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    @Override
    @Transactional
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getById(teamId);
        long userId = loginUser.getId();
        //检查是否加入队伍
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        long teamHasJoinNum = this.countUsersInTeam(teamId);
        // 队伍只剩一人，解散
        if (teamHasJoinNum == 1) {
            // 删除队伍
            this.removeById(teamId);
        } else {
            // 队伍还剩至少两人
            // 是队长
            if (team.getUserId() == userId) {
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                //获取id顺序在第二的用户，第一个是当前的队长
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean deleteTeam(long id, User loginUser){
        Team team = teamMapper.selectById(id);
        if(team.getIsDelete() == 1){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不能重复删除");
        }
        if(!Objects.equals(loginUser.getId(), team.getUserId())){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",team.getId());
        boolean removeUserTeam = userTeamService.remove(userTeamQueryWrapper);

        boolean removeTeam = this.removeById(id);
        return removeUserTeam&&removeTeam;
    }



    @Override
    public Wrapper<Team> getWrapper(TeamQueryRequest teamQueryRequest, boolean isAdmin){
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        Long id = teamQueryRequest.getId();
        wrapper.eq(id!=null,Team::getId,id);
        List<Long> idList = teamQueryRequest.getIdList();
        wrapper.in(!CollectionUtils.isEmpty(idList),Team::getId,idList);
        String searchText = teamQueryRequest.getSearchText();
        wrapper.and(!StringUtils.isBlank(searchText),text->{
            wrapper.like(Team::getName,searchText).or().like(Team::getDescription,searchText);
        });
        String name = teamQueryRequest.getName();
        wrapper.like(!StringUtils.isBlank(name),Team::getName,name);
        String description = teamQueryRequest.getDescription();
        wrapper.like(!StringUtils.isBlank(description),Team::getDescription,description);
        Integer maxNum = teamQueryRequest.getMaxNum();
        wrapper.eq(maxNum!=null,Team::getMaxNum,maxNum);
        Long userId = teamQueryRequest.getUserId();
        wrapper.eq(userId!=null,Team::getUserId,userId);
        Integer status = teamQueryRequest.getStatus();
        wrapper.eq(status!=null&&status==TeamStatusEnum.PUBLIC.getValue(),Team::getStatus,status);
        wrapper.eq(status!=null&&isAdmin&&(status==TeamStatusEnum.SECRET.getValue()||status==TeamStatusEnum.PRIVATE.getValue()),Team::getStatus,status);
        wrapper.gt(Team::getExpireTime,new Date());
        return wrapper;
    }


    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countUsersInTeam(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 给TeamUserVO添加当前用户是否加入的字段
     * @param records
     * @return
     */
    @Override
    public List<TeamUserVO> setHasJoin(List<TeamUserVO> records,User loginUser) {
        //符合条件的team的id列表
        final List<Long> teamIdList = records.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、判断当前用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 已加入的队伍 id 集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            records.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception ignored) {}
        return records;
    }

}




