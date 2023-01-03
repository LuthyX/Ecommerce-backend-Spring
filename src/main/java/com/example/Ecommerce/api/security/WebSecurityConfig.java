package com.example.Ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {
    private JWTRequestFIlter jwtRequestFIlter;

    public WebSecurityConfig(JWTRequestFIlter jwtRequestFIlter) {
        this.jwtRequestFIlter = jwtRequestFIlter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http.csrf().disable().cors().disable();
    http.addFilterBefore(jwtRequestFIlter, AuthorizationFilter.class);
    http.authorizeHttpRequests()
            .requestMatchers("/auth/register", "/auth/login", "/auth/verify", "/product").permitAll()
            .anyRequest().authenticated();
    return http.build();
    }

}
