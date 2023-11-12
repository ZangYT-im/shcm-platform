package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.Follow;
import com.shcm.mapper.FollowMapper;
import com.shcm.service.IFollowService;
import org.springframework.stereotype.Service;


@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

}
