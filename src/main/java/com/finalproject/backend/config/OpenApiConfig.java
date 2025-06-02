package com.finalproject.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "FinalProject API", version = "v1"),
        security = @SecurityRequirement(name = "github-oauth")
)
@SecurityScheme(
        name = "github-oauth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "https://github.com/login/oauth/authorize",
                        tokenUrl = "https://github.com/login/oauth/access_token",
                        scopes = {
                                @OAuthScope(name = "read:user", description = "Read user profile"),
                                @OAuthScope(name = "user:email", description = "Read user email")
                        }
                )
        )
)

@Configuration
public class OpenApiConfig {
}
