package com.tenco.blog._core.config;

import com.tenco.blog._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // IoC 처리 (싱글톤 패턴 관리)
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // DI 처리 (생존자 의존 주입)
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                // 인터셉터가 동작할 URI 패턴을 지정
                .addPathPatterns("/board/**", "/user/**")
                // 인터셉터에서 제외할 URI 패턴을 지정
                .excludePathPatterns("/board/{id:\\d+}");
                // \\d+ 는 정규 표현식으로 1개 이상의 숫자를 의미
                // /board/1, /board/22
    }
}
