package com.ponymate.firendsmate.model.es;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ponymate.firendsmate.model.domain.User;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author 22121
 */
@Document(indexName = "user")
@Data
public class UserEsDTO implements Serializable {

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 标题
     */
    private String username;

    /**
     * 内容
     */
    private String userAccount;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    private static final Gson GSON = new Gson();

    /**
     * 对象转包装类
     *
     * @param user
     * @return
     */
    public static UserEsDTO objToDto(User user) {
        if (user == null) {
            return null;
        }
        UserEsDTO userEsDTO = new UserEsDTO();
        BeanUtils.copyProperties(user, userEsDTO);
        String tagsStr = user.getTags();
        if (StringUtils.isNotBlank(tagsStr)) {
            userEsDTO.setTags(GSON.fromJson(tagsStr, new TypeToken<List<String>>() {
            }.getType()));
        }
        return userEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param userEsDTO
     * @return
     */
    public static User dtoToObj(UserEsDTO userEsDTO) {
        if (userEsDTO == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userEsDTO, user);
        List<String> tagList = userEsDTO.getTags();
        if (CollectionUtils.isNotEmpty(tagList)) {
            user.setTags(GSON.toJson(tagList));
        }
        return user;
    }
}