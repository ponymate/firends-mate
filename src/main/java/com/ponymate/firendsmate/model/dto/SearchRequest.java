package com.ponymate.firendsmate.model.dto;

import com.ponymate.firendsmate.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 20:47 2023/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchRequest extends PageRequest {
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}
