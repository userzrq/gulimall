package com.atguigu.gulimall.wms.config;

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
public class WmsSwaggerConfig {

    @Bean("库存系统")
    public Docket userApis(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("库存系统")
                .select()
                //含有@Api注解的方法
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/wms.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("谷粒商城-库存系统接口文档")
                .description("提供库存系统的文档")
                .termsOfServiceUrl("http://www.atguigu.com")
                .version("1.0")
                .build();
    }
}
