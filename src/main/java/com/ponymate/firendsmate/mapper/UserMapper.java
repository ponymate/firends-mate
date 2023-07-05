package com.ponymate.firendsmate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ponymate.firendsmate.model.domain.User;

import java.util.Date;
import java.util.List;


/**
 * @author ponymate
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> listUserWithDelete(Date fiveMinutesAgoDate);
}




