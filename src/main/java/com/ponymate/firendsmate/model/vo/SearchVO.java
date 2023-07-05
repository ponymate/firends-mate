package com.ponymate.firendsmate.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 20:48 2023/6/28
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<TeamUserVO> teamList;

    private Integer pageSize;

    private Integer pageNum;

}
