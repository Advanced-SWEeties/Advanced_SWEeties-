package dev.teamproject.config;

import dev.teamproject.filter.JwtRequestFilter;
import dev.teamproject.service.UserService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Controller for managing kitchen-related endpoints.
 * This class handles HTTP requests related to kitchen operations.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtRequestFilter jwtRequestFilter;
  /**
   * Controller for managing kitchen-related endpoints.
   * This class handles HTTP requests related to kitchen operations.
   */
  //https://www.youtube.com/watch?v=nN68jjUP_rQ&t=239s
  
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/api/users/login").permitAll()
            .requestMatchers("/api/users/add").hasRole("MANAGER")
            .requestMatchers("/api/users/delete/**").hasRole("MANAGER")
            .requestMatchers("/api/kitchens/update").hasAnyRole("MANAGER", "SUPER_GOLDEN_PLUS")
            .requestMatchers("/api/kitchens/add").hasAnyRole("MANAGER", "SUPER_GOLDEN_PLUS")
            .requestMatchers("/api/kitchens/delete").hasAnyRole("MANAGER", "SUPER_GOLDEN_PLUS")
          .requestMatchers("/api/users/**").authenticated()
          .anyRequest().permitAll()
      )

        .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();

  }

  /**
   * Password Encoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Authentication.
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}