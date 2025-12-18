package com.certimetergroup.talentos.bffwebapp.config.web;

import com.certimetergroup.talentos.bffwebapp.errorhandler.RestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${user-api.endpoint.baseurl}")
    String userApiBaseUrl;

    @Value("${curriculum-api.endpoint.baseurl}")
    String curriculumApiBaseUrl;

    @Value("${domain-api.endpoint.baseurl}")
    String domainApiBaseUrl;

    @Bean
    RestTemplate restTemplateUserApi(RestTemplateBuilder builder) {
        return builder
                .rootUri(userApiBaseUrl)
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    @Bean
    RestTemplate restTemplateCurriculumApi(RestTemplateBuilder builder) {
        return builder
                .rootUri(curriculumApiBaseUrl)
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    @Bean
    RestTemplate restTemplateDomainApi(RestTemplateBuilder builder) {
        return builder
                .rootUri(domainApiBaseUrl)
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }
}
