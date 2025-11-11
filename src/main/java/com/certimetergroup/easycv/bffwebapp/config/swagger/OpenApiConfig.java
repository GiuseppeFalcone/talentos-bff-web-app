package com.certimetergroup.easycv.bffwebapp.config.swagger;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Backend for Frontend API")
                        .version("v1.0")
                        .description("Gateaway for EasyCv architecture")
                        .contact(new Contact().name("Giuseppe Falcone").email("giuseppe.falcone@lutech.it"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Developer guide")
                        .url("http://localhost:8080/bff-web-app/docs"));
    }
}
