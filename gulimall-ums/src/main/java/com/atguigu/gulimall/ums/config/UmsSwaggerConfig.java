package com.atguigu.gulimall.ums.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class UmsSwaggerConfig {

    @Bean("用户信息系统")
    public Docket userApis(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户信息系统")
                .select()
                //含有@Api注解的方法
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/ums.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("谷粒商城-用户信息系统接口文档")
                .description("提供用户信息系统的文档")
                .termsOfServiceUrl("http://www.atguigu.com")
                .version("1.0")
                .build();
    }
}
