package com.certimetergroup.easycv.bffwebapp.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "cors", value = "enable", havingValue = "true")
public class CorsConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public CorsFilter corsfilter(@Value("${allowed-cors-origin}") String allowedCorsOrigin) {
        logger.warn("Cors Filter: ACTIVE");

        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(allowedCorsOrigin));
        cors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Access-Control-Allow-Origin"));
        cors.setExposedHeaders(Arrays.asList(
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "Access-Control-Allow-Origin"
        ));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return new CorsFilter(source);
    }
}
