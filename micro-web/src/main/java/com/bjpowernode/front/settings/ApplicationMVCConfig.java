package com.bjpowernode.front.settings;

import com.bjpowernode.front.interceptors.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationMVCConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor = new LoginInterceptor();

        //拦截的地址
        String [] addPath = {"/invest/**","/recharge/**","/user/**"};
        //不拦截的
        String [] excludePath = {"/user/login",
                "/user/logout",
                "/user/page/login",
                "/user/page/register",
                "/user/phone",
                "/user/register"
        };

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(addPath)
                .excludePathPatterns(excludePath);
    }
}
