package com.mipt.angelikaliber.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("To-Do List API")
                .version("2.0.0")
                .description("REST API for managing tasks, attachments, favorites and preferences")
                .contact(new Contact().name("aliber").email("fwnd80@gmail.com")));
    }
}
