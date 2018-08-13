拦截器配置的几种方式的实现及区别

1、@EnableWebMvc+implements WebMvcConfigurer

    /**
     * WebMvcConfigurer 实现拦截器
     * @EnableWebMvc + implements WebMvcConfigurer ： 会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置
     * @author liujianxue
     * @since 2018/8/13
     */
    @Configuration
    @EnableWebMvc
    public class WebMvcConfigurerConfig implements WebMvcConfigurer {
    
        @Bean
        public LoginInterceptor loginInterceptor(){
            return new LoginInterceptor();
        }
    
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
    
            registry.addInterceptor(loginInterceptor()).addPathPatterns("/tuitui/**")
                    .excludePathPatterns("/swagger-ui.html","/swagger-resources/*","/v2/api-docs/*","/error");
        }
    
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
    
            registry.addResourceHandler("/").addResourceLocations("/**");
    
            //swagger
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
    
        }
    }

2、继承WebMvcConfigurerAdapter

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

3、继承WebMvcConfigurationSupport

    @Configuration
    public class WebMvcConfigurationSupportConfig extends WebMvcConfigurationSupport {
    
        @Bean
        public LoginInterceptor loginInterceptor(){
            return new LoginInterceptor();
        }
    
        @Override
        protected void addInterceptors(InterceptorRegistry registry) {
    
            registry.addInterceptor(loginInterceptor()).addPathPatterns("/tuitui/**")
                    .excludePathPatterns("/swagger-ui.html","/swagger-resources/*","/v2/api-docs/*","/error");
            super.addInterceptors(registry);
        }
    
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
    
            registry.addResourceHandler("/").addResourceLocations("/**");
    
            //swagger
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
    
            super.addResourceHandlers(registry);
        }
    }

4、继承DelegatingWebMvcConfiguration

    /**
     * DelegatingWebMvcConfiguration 实现拦截器
     * @author liujianxue
     * @since 2018/8/13
     */
    @Configuration
    public class DelegatingWebMvcConfigurationConfig extends DelegatingWebMvcConfiguration {
    
        @Bean
        public LoginInterceptor loginInterceptor(){
            return new LoginInterceptor();
        }
    
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
    
            registry.addInterceptor(loginInterceptor()).addPathPatterns("/tuitui/**")
                    .excludePathPatterns("/swagger-ui.html","/swagger-resources/*","/v2/api-docs/*","/error");
        }
    
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
    
            registry.addResourceHandler("/").addResourceLocations("/**");
    
            //swagger
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
    
        }
    
    }



---

@EnableWebMvc是什么

直接看源码,@EnableWebMvc实际上引入一个DelegatingWebMvcConfiguration。

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Documented
    @Import({DelegatingWebMvcConfiguration.class})
    public @interface EnableWebMvc {
    }

DelegatingWebMvcConfiguration继承了WebMvcConfigurationSupport

    @Configuration
    public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
    ...

所以@EnableWebMvc=继承DelegatingWebMvcConfiguration=继承WebMvcConfigurationSupport

@EnableWebMvc和@EnableAutoConfiguration的关系

@EnableAutoConfiguration是springboot项目的启动类注解@SpringBootApplication的子元素，主要功能为自动配置。

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(
        excludeFilters = {@Filter(
        type = FilterType.CUSTOM,
        classes = {TypeExcludeFilter.class}
    ), @Filter(
        type = FilterType.CUSTOM,
        classes = {AutoConfigurationExcludeFilter.class}
    )}
    )
    public @interface SpringBootApplication {
    ...
    }

@EnableAutoConfiguration实际是导入了EnableAutoConfigurationImportSelector和Registrar两个类

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @AutoConfigurationPackage
    @Import({AutoConfigurationImportSelector.class})
    public @interface EnableAutoConfiguration {
    ...
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @Import({Registrar.class})
    public @interface AutoConfigurationPackage {
    }
    这两个类的具体原理有些复杂，不太清除，主要内容是通过SpringFactoriesLoader.loadFactoryNames()导入jar下面的配置文件META-INF/spring.factories

     protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
            List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
            Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.");
            return configurations;
        }

    配置文件中的内容如下

    # Auto Configure
    org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
    ...
    org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
    org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
    ...

    其中有WebMvcAutoConfiguration,WebMvcAutoConfiguration源码如下

    @Configuration
    @ConditionalOnWebApplication(
        type = Type.SERVLET
    )
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
    @AutoConfigureOrder(-2147483638)
    @AutoConfigureAfter({DispatcherServletAutoConfiguration.class, ValidationAutoConfiguration.class})
    public class WebMvcAutoConfiguration {
    ...
    }

@ConditionalOnMissingBean({WebMvcConfigurationSupport.class})意思是如果存在它修饰的类的bean

,则不需要再创建这个bean。

由此可得出结论：

如果有配置文件继承了DelegatingWebMvcConfiguration，

或者WebMvcConfigurationSupport，或者配置文件有@EnableWebMvc，那么 @EnableAutoConfiguration 中的

WebMvcAutoConfiguration 将不会被自动配置，而是使用WebMvcConfigurationSupport的配置。

@EnableWebMvc,WebMvcConfigurationSupport,WebMvcConfigurer和WebMvcConfigurationAdapter使用

WebMvcConfigurationAdapter已经废弃，最好用implements WebMvcConfigurer代替

    @Configuration
    public class MyConfig implements WebMvcConfigurer {
        
    }

如果使用继承,WebMvcConfigurationSupport，DelegatingWebMvcConfiguration，或者使用@EnableWebMvc，

需要注意会覆盖application.properties中关于WebMvcAutoConfiguration的设置，需要在自定义配置中实现，如

拦截器配置WebMvcConfigurerAdapter过时使用WebMvcConfigurationSupport来代替 新坑

示例如下

    Configuration
    @EnableWebMvc
    public class MyConfig implements WebMvcConfigurer {
    
    }
    @Configuration
    public class MyConfig extends WebMvcConfigurationSupport {
    
    }
    @Configuration
    public class MyConfig extends DelegatingWebMvcConfiguration {
    
    }
    上面代码中需要在类中实现关于WebMvcAutoConfiguration的配置，而不是在application.properties中。

总结

implements WebMvcConfigurer ： 不会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置

@EnableWebMvc + implements WebMvcConfigurer ： 会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置

extends WebMvcConfigurationSupport ：会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置

extends DelegatingWebMvcConfiguration ：会覆盖@EnableAutoConfiguration关于WebMvcAutoConfiguration的配置
