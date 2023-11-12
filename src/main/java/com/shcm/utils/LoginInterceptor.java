package com.shcm.utils;

import com.shcm.dto.UserDTO;
import com.shcm.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * description
 * </p>
 *
 * @author ZangYT
 * @since 2023/11/12 17:42
 */

public class LoginInterceptor implements HandlerInterceptor {

    /*
    * controller执行之前
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取session
        HttpSession session = request.getSession();
        //2.获取session中的用户
        Object user = session.getAttribute("user");
        //3.判断用户是否存在
        if(user == null){
            //4.不存在，拦截，返回401状态码
            response.setStatus(401);
            return false;
        }
        //5.存在，保存用户信息到Threadlocal
        UserHolder.saveUser((UserDTO) user);
        //6.放行
        return true;
    }

    /*
    * post: controller执行之后
    * */

    /*
    * 页面渲染完成之后，返回给用户之前
    * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //释放用户
        /**
         * 因为用户信息存放在threadLocal里面，如果不手动释放会有内存泄露问题
         * */
        UserHolder.removeUser();

    }
}
