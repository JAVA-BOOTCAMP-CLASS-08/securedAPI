package com.sicos.secured.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public Contact getContact() {
        Contact contact = new Contact();
        contact.setName("Andres Ramos");
        contact.setEmail("andres.alberto.ramos@gmail.com");
        contact.setUrl("https://www.linkedin.com/in/andresramos/");

        return contact;
    }

    @Bean
    public License getLicense() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("http://www.apache.org/licenses/LICENSE-2.0.html");

        return license;
    }

    @Bean
    public List<Server> getServers() {
        Server localServer = new Server();

        localServer.setUrl("/secured");
        localServer.setDescription("Local API Server");

        return List.of(localServer);
    }

    @Bean
    public Info getInfo(Contact contact, License license) {
        Info info = new Info();
        info.version("1.0");
        info.title("API Service REST");
        info.description("API REST of AWS Java Course");
        info.contact(contact);
        info.license(license);

        return info;
    }

    @Bean
    public Components getSecuritySchemas() {
        Components components = new Components();

        components.addSecuritySchemes("bearerAuth", new SecurityScheme()
                                                            .type(SecurityScheme.Type.HTTP)
                                                            .in(SecurityScheme.In.HEADER)
                                                            .description("JWT Bearer Authorization Scheme")
                                                            .scheme("bearer")
                                                            .bearerFormat("JWT")
                                                            .name("Authorization"));

        components.addSecuritySchemes("ApiKeyHeaderAuth", new SecurityScheme()
                                                        .type(SecurityScheme.Type.APIKEY)
                                                        .in(SecurityScheme.In.HEADER)
                                                        .description("API KEY Authorization Scheme")
                                                        .scheme("header")
                                                        .name("X-API-KEY"));

        components.addSecuritySchemes("BasicAuth", new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .in(SecurityScheme.In.HEADER)
                                                                .description("Basic Authorization Scheme")
                                                                .scheme("basic")
                                                                .name("Authorization"));

        return components;
    }

    @Bean
    public OpenAPI getOpenApi(Info info, List<Server> servers, Components components) {
        return new OpenAPI()
                .info(info)
                .servers(servers)
                .components(components);
    }
}
