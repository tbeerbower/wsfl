package org.tbeerbower.wsfl_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI wsflOpenAPI() {
        Server devServer = new Server()
            .url("http://localhost:9000")
            .description("Development server");
            
        Contact contact = new Contact()
            .email("your.email@example.com")
            .name("API Support")
            .url("https://www.example.com");
            
        License mitLicense = new License()
            .name("MIT License")
            .url("https://choosealicense.com/licenses/mit/");
            
        Info info = new Info()
            .title("WSFL (Winter Series Fantasy League) API")
            .version("1.0")
            .contact(contact)
            .description("API for managing fantasy running league competitions")
            .termsOfService("https://www.example.com/terms")
            .license(mitLicense);
            
        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer));
    }
} 