package com.certimetergroup.easycv.bffwebapp.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "cors", value = "enable", havingValue = "true")
public class CorsConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${allowed-cors-origin}")
    String allowedCorsOrigin;

    @Bean
    @Order(1)
    public CorsFilter corsfilter() {
        logger.warn("CorsConfiguration: ACTIVE");
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of(allowedCorsOrigin));
        corsConfiguration.setAllowedHeaders(List.of("Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
                "Authorization", "Origin, Accept", "X-Requested-With"));
        corsConfiguration.setExposedHeaders(List.of("Origin", "ContentType", "Accept", "Authorizaton",
                "Access-Control-Allow-Origin"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}