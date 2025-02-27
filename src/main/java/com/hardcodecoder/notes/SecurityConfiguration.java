package com.hardcodecoder.notes;

import com.hardcodecoder.notes.core.DataValidator;
import com.hardcodecoder.notes.core.EmailValidator;
import com.hardcodecoder.notes.core.PasswordValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private final String[] openEndpoints = {
        "/auth/signup",
        "/auth/refresh",
        "/db/h2/**"
    };

    @Bean
    public SecurityFilterChain configureSecurity(@NonNull HttpSecurity security) throws Exception {
        return security
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(FormLoginConfigurer::disable)
            .authorizeHttpRequests(it -> it
                .requestMatchers(openEndpoints).permitAll()
                .anyRequest().authenticated())
            .headers(it -> it.frameOptions(FrameOptionsConfig::sameOrigin))
            .build();
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DataValidator<String> emailValidator() {
        return new EmailValidator();
    }

    @Bean
    public DataValidator<String> passwordValidator() {
        return new PasswordValidator();
    }
}