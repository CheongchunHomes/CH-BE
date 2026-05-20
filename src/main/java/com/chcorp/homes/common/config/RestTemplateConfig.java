package com.chcorp.homes.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// 외부 api 호출에 사용할 RestTemplate Bean 등록
// 공고 api, 청년정책 api, 공공서비스 api 등에서 공통으로 사용 예정
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
