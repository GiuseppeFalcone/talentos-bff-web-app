package com.certimetergroup.easycv.bffwebapp.config.web;

import com.certimetergroup.easycv.bffwebapp.errorhandler.RestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${user-api.endpoint.baseurl}")
    String userApiBaseUrl;

    @Bean
    RestTemplate restTemplateUserApi(RestTemplateBuilder builder) {
        return builder
                .rootUri(userApiBaseUrl)
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }
}
