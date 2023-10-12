package com.iecube.community.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 定义一个拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 检测全局session对象中是否有用户id数据，如果有则放行，如果没有则重定向到登录页
     * @param request current HTTP request  请求对象
     * @param response current HTTP response  响应对象
     * @param handler chosen handler to execute, for type and/or instance evaluation 处理器（usl+controller的映射）
     * @return  如果返回值为true 表示放行当前请求  如果为false 则表示拦截
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 通过HttpServletRequest 对象来获取session对象
        Object obj =  request.getSession().getAttribute("userid");
        if(obj == null){
            // 说明用户没有登录过系统， 则发送302响应，前端校验状态码进行重定向到login
//            response.sendRedirect("//login");
            response.sendError(HttpServletResponse.SC_FOUND, "/login");
            return false;
        }
        //放行
        return true;
    }
}
