package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.User;
import com.shcm.mapper.UserMapper;
import com.shcm.service.IUserService;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
