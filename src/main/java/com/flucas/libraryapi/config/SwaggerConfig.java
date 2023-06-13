package com.flucas.libraryapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
public class SwaggerConfig {

    @Value("${openapi.dev-url}")
    private String dev_url;

    @Value("${application.mail.default-remetent}")
    private String email;

    @Bean
    public OpenAPI api() {
        var server = new Server();
        server.setUrl(dev_url);
        server.setDescription("Server URL in Development enviroment");

        var contact = new Contact();
        contact.setEmail(email);
        contact.setName("Lucas FS");
        
        var mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        var info = new Info()
            .title("Library API")
            .version("0.1")
            .contact(contact)
            .description("This API exposes endpoints to a library database")
            .license(mitLicense);
        
        return new OpenAPI().info(info).servers(List.of(server));
    }
}
