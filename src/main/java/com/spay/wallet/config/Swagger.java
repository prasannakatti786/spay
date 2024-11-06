package com.spay.wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentation")
                        .version("1.0.0")
                        .description("Description of API"))
                .components(
                        new io.swagger.v3.oas.models.Components().addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))




         .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/customer-registration/**", "/api/v1/auth/**","/api/v1/miscellaneous/**","/api/v1/micro_customer/**",
                        "/api/v1/micro_to_mono_apis_create_customer/**","/api/v1/customer/micro_registration/**","/api/v1/micro_service_customer_creation/**","/api/v1/customer/micro_to_mono_registration/**")
                .build();
    }

    @Bean
    public GroupedOpenApi securedApi() {
        return GroupedOpenApi.builder()
                .group("secured")
                .pathsToMatch("/api/v1/account-balance/**", "/api/v1/account/**", "/api/v1/account-creation/**",
                        "", "/api/v1/quick-pay/**", "",
                        "", "", "","api/v1/download/**","","/api/v1/transaction/**","/api/v1/credential/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(new SecurityRequirement().addList("BearerAuth")))
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/v1/admin-account/**", "/api/v1/admin-settlement/**", "/api/v1/admin-agent/**",
                        "/api/v1/admin-customer/**", "/api/v1/admin-transaction/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes("AdminBearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                        .addSecurityItem(new SecurityRequirement().addList("AdminBearerAuth")))
                .build();
    }

}

