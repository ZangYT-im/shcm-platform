package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.Blog;
import com.shcm.mapper.BlogMapper;
import com.shcm.service.IBlogService;
import org.springframework.stereotype.Service;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

}
