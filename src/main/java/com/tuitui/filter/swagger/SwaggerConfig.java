package com.tuitui.filter.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author liujianxue
 * @since 2018/8/13
 */
@Configuration
@EnableSwagger2
@ConditionalOnExpression("${swagger.enabled}")
public class SwaggerConfig {

    @Bean
    public Docket createApi(){

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo()).useDefaultResponseMessages(false)
                .forCodeGeneration(false)
                .select()
                //.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.basePackage("com.tuitui.filter.controller"))
                .paths(PathSelectors.regex("/.*"))
//                .paths(PathSelectors.regex("/demo.*"))
                .build();
        return docket;
    }

    private ApiInfo getApiInfo(){

        return new ApiInfoBuilder().title("tuitui-filter").version("1.0").build();

    }
}
