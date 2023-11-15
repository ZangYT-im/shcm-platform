package com.shcm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shcm.dto.Result;
import com.shcm.entity.Follow;


public interface IFollowService extends IService<Follow> {

    Result follow(Long followUserId, Boolean isFollow);

    Result isFollow(Long followUserId);
}
