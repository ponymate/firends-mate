package com.ponymate.firendsmate.model.es;

import com.ponymate.firendsmate.model.domain.Team;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ponymate
 */
@Document(indexName = "team")
@Data
public class TeamEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd :hh:mm:ss";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    @Field(store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param team
     * @return
     */
    public static TeamEsDTO objToDto(Team team) {
        if (team == null) {
            return null;
        }
        TeamEsDTO teamEsDTO = new TeamEsDTO();
        BeanUtils.copyProperties(team, teamEsDTO);
        return teamEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param teamEsDTO
     * @return
     */
    public static Team dtoToObj(TeamEsDTO teamEsDTO) {
        if (teamEsDTO == null) {
            return null;
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamEsDTO, team);
        return team;
    }
}