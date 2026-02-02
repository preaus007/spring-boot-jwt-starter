package com.touhed.app.security;

import com.touhed.app.security.filter.JwtAuthenticationFilter;
import com.touhed.app.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor( onConstructor_ = {@Autowired} )
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String[] PUBLIC_API_ENDPOINTS = {
            "/auth/**",
            "/digihr-api-docs/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/job-applications/**",
            "/contactus/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider( customUserDetailsService );
        provider.setPasswordEncoder( passwordEncoder() );
        return new ProviderManager( List.of( provider ) );
    }

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ){
        return http
                .cors( Customizer.withDefaults() )
                .sessionManagement( AbstractHttpConfigurer::disable )
                .csrf( AbstractHttpConfigurer::disable )
                .httpBasic( AbstractHttpConfigurer::disable )
                .formLogin( AbstractHttpConfigurer::disable )
                .authorizeHttpRequests(
                auth ->
                        auth.requestMatchers( PUBLIC_API_ENDPOINTS ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class )
                .build();
    }
}
