package com.ponymate.firendsmate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ponymate.firendsmate.model.dto.SearchRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 9:40 2023/6/29
 */

@Service
public interface SearchService<T> {
    /**
     * 统一搜索接口
     * @return
     */
    public Page<T> doSearch(SearchRequest searchRequest, HttpServletRequest request);

}
