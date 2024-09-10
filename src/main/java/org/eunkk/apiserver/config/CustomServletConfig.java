package org.eunkk.apiserver.config;

import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.controller.formatter.LocalDateFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// method 오버라이딩 할 것이 많아짐
@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        log.info("----------------------------------");
        log.info("addFormatters");
        registry.addFormatter(new LocalDateFormatter());
    }

    // web에 cors 적용 -> 추후 시큐리티로 변경 예정
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 모든 경로 지정
//                .maxAge(500) // 최대 TTL 5초
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
//                .allowedOrigins("*");
//    }
}
