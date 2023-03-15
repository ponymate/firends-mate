package com.ponymate.firendsmate.model.dto.Team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 20:24 2023/3/13
 */
@Data
public class TeamAddRequest implements Serializable {
    /**
     * id
     */
    private Long userId;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = 1L;
}
