package com.ponymate.firendsmate;

import com.ponymate.firendsmate.service.impl.TeamServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class FirendsMateApplicationTests {

    @Resource
    TeamServiceImpl teamService;

}
