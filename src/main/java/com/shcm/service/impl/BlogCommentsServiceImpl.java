package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.BlogComments;
import com.shcm.mapper.BlogCommentsMapper;
import com.shcm.service.IBlogCommentsService;
import org.springframework.stereotype.Service;


@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
