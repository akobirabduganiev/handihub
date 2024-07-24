package tech.nuqta.handihub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * The OpenApiConfig class specifies the OpenAPI configuration for the HandiHub API.
 * It includes information such as the API contact details, description, version, licensing, terms of service,
 * servers, and security requirements.
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Akobir Abduganiev",
                        email = "akobir.abduganiev@ya.ru",
                        url = "https://nuqta.tech"
                ),
                description = "OpenApi specification for the HandiHub project",
                title = "HandiHub API",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://nuqta.tech"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080/"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "http://5.8.64.22:8080/"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
