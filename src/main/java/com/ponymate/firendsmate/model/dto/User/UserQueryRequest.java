package com.ponymate.firendsmate.model.dto.User;

import com.ponymate.firendsmate.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 20:50 2023/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest{

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签 json 列表
     */
    private String tags;

}
