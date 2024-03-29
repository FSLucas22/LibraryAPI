package com.flucas.libraryapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


@Configuration
public class SwaggerConfig {

    @Value("${application.mail.default-remetent}")
    private String email;

    @Bean
    public OpenAPI api() {

        var contact = new Contact();
        contact.setEmail(email);
        contact.setName("Lucas FS");
        contact.setUrl("https://github.com/FSLucas22");
        var mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        var info = new Info()
            .title("Library API")
            .version("1.0")
            .contact(contact)
            .description("API do projeto de controle de aluguel de livros")
            .license(mitLicense);
        
        return new OpenAPI().info(info);
    }
}
