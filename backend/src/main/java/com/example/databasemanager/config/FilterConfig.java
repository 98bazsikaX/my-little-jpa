package com.example.databasemanager.config;

import com.example.databasemanager.security.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers servlet filters in execution order: {@link QueryMethodFilter} (0)
 * runs before {@link JwtFilter} (1) so that QUERY method rewriting happens before
 * authentication checks.
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<QueryMethodFilter> queryMethodFilterRegistration() {
        FilterRegistrationBean<QueryMethodFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new QueryMethodFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
