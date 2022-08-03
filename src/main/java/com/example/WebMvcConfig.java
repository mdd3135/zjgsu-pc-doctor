package com.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /home/file/**为前端URL访问路径 后面 file:xxxx为本地磁盘映射
        registry.addResourceHandler("/file/**").addResourceLocations("file:/var/demo/");
        
    }
}

// 配置跨域访问
@Configuration
class CorsConfig implements WebMvcConfigurer{
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("*");
        // .exposedHeaders("Content_Type")
        // .exposedHeaders("X-Requested-With")
        // .exposedHeaders("accept")
        // .exposedHeaders("Origin")
        // .exposedHeaders("Access-Control-Request-Method")
        // .exposedHeaders("Access-Control-Request-Headers");
        // .allowCredentials(true);
    }
}
