package dev.teamproject.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Disable security for testing.
 */
@TestConfiguration
public class NoSecurityConfig {
  /**
   * Filter to disable security for testing.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Allow all requests
    return http.build();
  }
}
