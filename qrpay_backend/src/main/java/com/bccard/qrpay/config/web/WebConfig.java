package com.bccard.qrpay.config.web;

import com.bccard.qrpay.config.web.argumentresolver.LoginMemberArgumentResolver;
import com.bccard.qrpay.interceptor.LoginRedirectInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginRedirectInterceptor loginRedirectInterceptor;

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
}
