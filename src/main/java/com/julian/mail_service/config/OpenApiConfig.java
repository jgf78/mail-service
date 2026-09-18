package com.julian.mail_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mailServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mail Service API")
                        .description("""
                                Centralized email service for microservices.

                                Provides a REST API for sending emails through
                                the configured SMTP server.
                                """)
                        .version("1.0.0"));
    }
}
