package com.ponymate.firendsmate.dao;

import com.ponymate.firendsmate.model.es.UserEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 21:48 2023/6/29
 */
@Repository
public interface UserEsDao extends ElasticsearchRepository<UserEsDTO, Long> {

}
