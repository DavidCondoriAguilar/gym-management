package com.gymmanagement.gym_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for API versioning and documentation.
 * Sets up versioned API endpoints and Swagger/OpenAPI documentation.
 */
@Configuration
@OpenAPIDefinition(
    servers = {
        @Server(url = "/api/v1", description = "API Version 1")
    },
    info = @Info(
        title = "Gym Management API",
        version = "1.0.0",
        description = "API for managing gym members, memberships, and payments"
    )
)
public class ApiVersioningConfig implements WebMvcConfigurer {

    /**
     * Configures path matching to handle versioned API endpoints.
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Enable trailing slash matching for all endpoints
        configurer.setUseTrailingSlashMatch(true);
    }

    /**
     * Configures the OpenAPI documentation for v1 of the API.
     *
     * @return GroupedOpenApi configuration for v1
     */
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(openApi -> 
                    openApi.info(new io.swagger.v3.oas.models.info.Info()
                        .title("Gym Management API v1")
                        .version("1.0.0")
                        .description("Version 1 of the Gym Management API"))
                )
                .build();
    }

    /**
     * Configures the OpenAPI documentation for all versions.
     *
     * @return GroupedOpenApi configuration for all versions
     */
    @Bean
    public GroupedOpenApi allVersionsApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/**")
                .build();
    }
}
