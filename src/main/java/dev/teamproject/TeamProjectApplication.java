package dev.teamproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Main application class for the Team Project.
 * This class serves as the entry point for the Spring Boot application.
 * 
 * <p>
 * The application is currently configured to exclude DataSourceAutoConfiguration.
 * Uncomment the appropriate lines to enable database support once a MySQL instance is set up.
 * </p>
 */
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
// @EnableMethodSecurity 
// The corresponding Spring Security configuration is not implemented (TODO)
public class TeamProjectApplication {

  /**
   * The main method which serves as the entry point for the application.
   * 
   */
  public static void main(String[] args) {
    SpringApplication.run(TeamProjectApplication.class, args);
  }

  /**
   * RestTemplate bean configuration.
   * Provides a centralized RestTemplate instance for the entire application.
   */

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}