package com.iecube.community.config;

import com.iecube.community.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 完成拦截器注册的功能
 */
@Configuration //加载当前的拦截器并注册
public class LoginInterceptorConfigurer implements WebMvcConfigurer {
    //创建自定义拦截器对象
    HandlerInterceptor interceptor = new LoginInterceptor();

    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器注册
        //配置白名单： List集合
        List<String> patterns = new ArrayList<>();
        patterns.add("/users/login");
        patterns.add("/teacher/login");
        patterns.add("/student/login");
        patterns.add("/files/e/image");
        patterns.add("/files/image/{fileName}");
        patterns.add("/md/**");
        patterns.add("/t/article/compose/**/**");
//        patterns.add("/direction/get_all");
//        patterns.add("/file/**");
        // addPathPatterns("表示要拦截的url是什么").excludePathPatterns("list集合 表示白名单")
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }
}
