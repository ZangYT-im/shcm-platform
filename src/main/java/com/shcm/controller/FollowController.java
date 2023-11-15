package com.shcm.controller;


import com.shcm.dto.Result;
import com.shcm.service.IFollowService;
import com.shcm.service.impl.FollowServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private IFollowService followService;

    //关注
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {

        return followService.follow(followUserId, isFollow);
    }
    //取消关注
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }


    //获取共同关注列表
    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable("id")Long id){
        return followService.followCommons(id);
    }
}
