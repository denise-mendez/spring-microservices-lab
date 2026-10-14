package dev.storylab.story_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.GET, "/api/stories/**"
                        )
                        .permitAll()
                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST, "/api/stories/**"
                        )
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/stories/**"
                        )
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "api/stories/**"
                        )
                        .hasAnyRole("AUTHOR", "ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
