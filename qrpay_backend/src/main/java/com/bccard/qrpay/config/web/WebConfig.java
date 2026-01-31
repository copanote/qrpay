package com.bccard.qrpay.config.web;

import com.bccard.qrpay.config.web.argumentresolver.LoginMemberArgumentResolver;
import com.bccard.qrpay.filter.GlobalLoggingPreparationFilter;
import com.bccard.qrpay.interceptor.LoginRedirectInterceptor;
import jakarta.servlet.Filter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginRedirectInterceptor loginRedirectInterceptor;
    private final GlobalLoggingPreparationFilter globalLoggingPreparationFilter;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRedirectInterceptor)
                .addPathPatterns("/pages/**")
                .excludePathPatterns("/pages/login", "/pages/auth/**");
    }

    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(globalLoggingPreparationFilter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.addUrlPatterns("/qrpay/api/*", "/auth/*");
        return filterRegistrationBean;
    }
}
