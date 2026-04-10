package com.yvliangbao.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger API 文档配置
 *
 * @author 余量宝
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                // 只扫描自己项目的 Controller 包
                .apis(RequestHandlerSelectors.basePackage("com.yuliangbao"))
                // 匹配所有路径（但只扫描 com.yuliangbao 包下的接口）
                .paths(PathSelectors.any())
                .build()
                // 安全认证配置
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                // 是否启用
                .enable(true);
    }

    /**
     * API 基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("余量宝 API 文档")
                .description("余量宝盲盒交易平台接口文档")
                .contact(new Contact("余量宝团队", "https://yuliangbao.com", "support@yuliangbao.com"))
                .version("1.0.0")
                .build();
    }

    /**
     * 安全认证方案
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        securitySchemes.add(new ApiKey("Authorization", "Authorization", "header"));
        return securitySchemes;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(operationContext -> true)
                .build());
        return securityContexts;
    }

    /**
     * 默认认证要求
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }
}
