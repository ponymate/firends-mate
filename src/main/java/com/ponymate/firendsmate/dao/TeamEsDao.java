package com.ponymate.firendsmate.dao;

import com.ponymate.firendsmate.model.es.TeamEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author:Ponymate
 * @Description:
 * @Date:Created in 21:50 2023/6/29
 */
@Repository
public interface TeamEsDao extends ElasticsearchRepository<TeamEsDTO, Long> {
}
