package dev.dogeared.ctfdaccounthook.config;

import dev.dogeared.ctfdaccounthook.service.ApiKeyAuthenticationService;
import dev.dogeared.ctfdaccounthook.filter.ApiKeyAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {

    private final ApiKeyAuthenticationService apiKeyAuthenticationService;
    public SecurityConfig(ApiKeyAuthenticationService apiKeyAuthenticationService) {
        this.apiKeyAuthenticationService = apiKeyAuthenticationService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(config -> config.ignoringRequestMatchers("/api/v1/**"))
            .authorizeHttpRequests(req -> req.anyRequest().authenticated())
            .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
            .addFilterBefore(
                new ApiKeyAuthenticationFilter(apiKeyAuthenticationService), UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
