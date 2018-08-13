package com.tuitui.filter.interceptor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用WebMvcConfigurerAdapter 实现的拦截器
 * @author liujianxue
 * @since 2018/8/13
 */
@Configuration
public class WebMvcConfigurerAdapterConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @ConfigurationProperties(prefix = "intercept")
    public InterceptUrlConfig interceptUrlConfig(){
        return new InterceptUrlConfig();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/").addResourceLocations("/**");
        super.addResourceHandlers(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> urlList = new ArrayList<>();
        /*urlList.add("/swagger-ui.html");
        urlList.add("/swagger-resources");
        urlList.add("/v2/api-docs");
        urlList.add("/configuration/ui");*/

        urlList.addAll(interceptUrlConfig().getExcluded());

        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/tuitui/**")
                .addPathPatterns("/tuitui/auth/**")
                .excludePathPatterns(urlList);
    }

    public static class InterceptUrlConfig{

        private List<String> excluded = new ArrayList<>();

        public List<String> getExcluded() {
            return excluded;
        }

        public void setExcluded(List<String> excluded) {
            this.excluded = excluded;
        }
    }


}
