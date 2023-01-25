package dev.twd.take_a_walk_duo.configs;

import dev.twd.take_a_walk_duo.interceptors.CommonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.commonInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/**/resources/**");
    }
    @Bean
    public CommonInterceptor commonInterceptor() {
        return new CommonInterceptor();}
}
