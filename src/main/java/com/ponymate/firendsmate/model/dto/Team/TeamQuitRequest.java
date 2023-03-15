package com.ponymate.firendsmate.model.dto.Team;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author ponymate
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long teamId;

}
