package com.core.back9.common.config;

import com.core.back9.common.logging.CachingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CachingFilter> contentCachingFilter(){
        FilterRegistrationBean<CachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingFilter());
        registrationBean.addUrlPatterns("/api/*", "/public-api/*", "/app/*");
        registrationBean.setOrder(1);
        registrationBean.setName("CachingFilter");
        return registrationBean;
    }
}
