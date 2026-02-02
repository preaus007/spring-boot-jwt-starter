package com.touhed.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final Long MAX_AGE_SECS = 3600L;

    @Override
    public void addCorsMappings( CorsRegistry registry ){

        registry.addMapping( "/**" )
                .allowedOriginPatterns( "*" )
                .allowedMethods( "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS" )
                .allowedHeaders( "*" )
                .exposedHeaders( "content-disposition", "Content-Length" )
                .allowCredentials( true )
                .maxAge( MAX_AGE_SECS );
    }
}
