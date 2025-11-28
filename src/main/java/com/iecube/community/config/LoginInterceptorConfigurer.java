package com.iecube.community.config;

import com.iecube.community.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 完成拦截器注册的功能
 */
@Configuration //加载当前的拦截器并注册
public class LoginInterceptorConfigurer implements WebMvcConfigurer {
    private AuthInterceptor authInterceptor;

    @Autowired
    public void InterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }


    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器注册
        //配置白名单： List集合
        // addPathPatterns("表示要拦截的url是什么").excludePathPatterns("list集合 表示白名单")
        List<String> patterns = new ArrayList<>();
        patterns.add("/users/login");
        patterns.add("/teacher/login");
        patterns.add("/student/login");
        patterns.add("/student/jlogin");
//        patterns.add("/student/sign/code");
        patterns.add("/files/e/image");
        patterns.add("/files/image/{fileName}");
        patterns.add("/files/file/{fileName}");
//        patterns.add("/t/article/compose/**/**");
        patterns.add("/video/m3u8/**/**");
//        patterns.add("/elaborate/md/**/**");
        patterns.add("/emd_task/dlog/upload/**/**");
        patterns.add("/device/connect");
        patterns.add("/dashboard");
        patterns.add("/ai/chat/list");
//        patterns.add("/ai/**/**");
        patterns.add("/emdv4/analysis/**");

        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }
}
