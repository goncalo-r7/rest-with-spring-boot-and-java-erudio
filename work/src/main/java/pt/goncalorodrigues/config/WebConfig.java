package pt.goncalorodrigues.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        // Via EXTENSION.   http://localhost:8080/api/person/v1/10.xml http://localhost:8080/api/person/v1/10.json -> Deprecated on Spring Boot 2.6

        // Via QUERY PARAM. http://localhost:8080/api/person/v1/10?mediaType=xml
        /*configurer.favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(true) // se tiver accept application/json -> sobrescreve
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
*/
        // Via HEADER PARAM. http://localhost:8080/api/person/v1/10
        configurer.favorParameter(false)
                .ignoreAcceptHeader(false) // se tiver accept application/json -> sobrescreve
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("yaml", MediaType.APPLICATION_YAML);

    }
}
