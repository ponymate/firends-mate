package com.ponymate.firendsmate.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ponymate.firendsmate.mapper.UserTeamMapper;
import com.ponymate.firendsmate.model.domain.UserTeam;
import com.ponymate.firendsmate.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 22121
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-03-13 20:15:37
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




