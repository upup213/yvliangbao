package com.yvliangbao.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web 配置
 *
 * @author 余量宝
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 不需要登录的接口
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/user/login/**",
            "/merchant/register",
            "/merchant/login",
            "/admin/login",            // 平台管理员登录
            "/banner/list",            // 轮播图列表（公开）
            "/product/list",           // 用户端浏览商品不需要登录
            "/product/detail/**",      // 商品详情不需要登录（如果是数字ID路径）
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }
}
