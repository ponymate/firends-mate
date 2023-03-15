package com.ponymate.firendsmate.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ponymate.firendsmate.mapper.TagMapper;
import com.ponymate.firendsmate.model.domain.Tag;
import com.ponymate.firendsmate.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 22121
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-03-11 16:52:10
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




