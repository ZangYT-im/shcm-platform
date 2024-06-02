package com.shcm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shcm.dto.Result;
import com.shcm.entity.BlogComments;
import com.shcm.entity.User;
import com.shcm.service.IBlogCommentsService;
import com.shcm.service.IUserService;
import com.shcm.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

    @Resource
    IBlogCommentsService blogCommentsService;

    @Resource
    IUserService userService;

    /**
     * 根据博客 id 查询对应的评论
     * @return
     */
    @GetMapping("/queryCommentsListByBlogId/{blogId}")
    public Result queryCommentsListByBlogId(@PathVariable("blogId") String blogId) {
        QueryWrapper<BlogComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        List<BlogComments> blogCommentsList = blogCommentsService.getBaseMapper().selectList(queryWrapper);
        for (BlogComments blogComments : blogCommentsList) {
            User user = userService.getById(blogComments.getUserId());
            blogComments.setNickName(user.getNickName());
            blogComments.setIcon(user.getIcon());
        }
        System.out.println(blogCommentsList.toArray().toString());
        return Result.ok(blogCommentsList);
    }

    @PostMapping("/saveComments")
    public Result saveBlogCommit(@RequestBody BlogComments blogComment) {
        blogComment.setUserId( UserHolder.getUser().getId());
        System.out.println(blogComment);
        boolean save = blogCommentsService.save(blogComment);
        return Result.ok(save);
    }
}
