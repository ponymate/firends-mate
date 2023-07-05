package com.ponymate.firendsmate.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ponymate.firendsmate.common.BaseResponse;
import com.ponymate.firendsmate.common.ErrorCode;
import com.ponymate.firendsmate.common.ResultUtils;
import com.ponymate.firendsmate.contant.SearchTypeEnum;
import com.ponymate.firendsmate.exception.BusinessException;
import com.ponymate.firendsmate.model.dto.SearchRequest;
import com.ponymate.firendsmate.model.vo.SearchVO;
import com.ponymate.firendsmate.model.vo.TeamUserVO;
import com.ponymate.firendsmate.model.vo.UserVO;
import com.ponymate.firendsmate.service.TeamService;
import com.ponymate.firendsmate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @author ponymate
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        String type = searchRequest.getType();
        if(StringUtils.isBlank(searchText)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索框不能为空");
        }
        if (SearchTypeEnum.getEnumByValue(type) == null) {

            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                return userService.doSearch(searchRequest,request);
            });

            CompletableFuture<Page<TeamUserVO>> teamTask = CompletableFuture.supplyAsync(() -> {
                return teamService.doSearch(searchRequest,request);
            });

            CompletableFuture.allOf(userTask, teamTask).join();
            try {
                Page<UserVO> userPage = userTask.get();
                Page<TeamUserVO> teamPage = teamTask.get();

                SearchVO searchVO = new SearchVO();
                if(ObjectUtils.isNotEmpty(userPage)){
                    searchVO.setUserList(userPage.getRecords());
                    searchVO.setPageNum(Integer.parseInt(String.valueOf(userPage.getCurrent())));
                    searchVO.setPageSize(Integer.parseInt(String.valueOf(userPage.getSize())));
                }
                if(ObjectUtils.isNotEmpty(teamPage)){
                    searchVO.setTeamList(teamPage.getRecords());
                    searchVO.setPageNum(Integer.parseInt(String.valueOf(teamPage.getCurrent())));
                    searchVO.setPageSize(Integer.parseInt(String.valueOf(teamPage.getSize())));
                }
                return ResultUtils.success(searchVO);
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        }else {
            SearchVO searchVO = new SearchVO();
            switch (type){
                case "user":
                    Page<UserVO> user = userService.doSearch(searchRequest, request);
                    if(ObjectUtils.isNotEmpty(user)){
                        searchVO.setUserList(user.getRecords());
                        searchVO.setPageNum(Integer.parseInt(String.valueOf(user.getCurrent())));
                        searchVO.setPageSize(Integer.parseInt(String.valueOf(user.getSize())));
                    }
                    break;
                case "team":
                    Page<TeamUserVO> teamPage = teamService.doSearch(searchRequest, request);
                    if(ObjectUtils.isNotEmpty(teamPage)){
                        searchVO.setTeamList(teamPage.getRecords());
                        searchVO.setPageNum(Integer.parseInt(String.valueOf(teamPage.getCurrent())));
                        searchVO.setPageSize(Integer.parseInt(String.valueOf(teamPage.getSize())));
                    }
                    break;
                default:
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"不存在该类型");
            }
            return ResultUtils.success(searchVO);
        }
    }
}