package com.hardcodecoder.notes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    @NonNull
    public SecurityFilterChain configureSecurity(@NonNull HttpSecurity security) throws Exception {
        return security
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(FormLoginConfigurer::disable)
            .authorizeHttpRequests(it -> it
                .requestMatchers("/auth/signup", "/auth/token").permitAll()
                .anyRequest().authenticated())
            .build();
    }
}