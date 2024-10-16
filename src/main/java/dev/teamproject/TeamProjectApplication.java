package dev.teamproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "dev.teamproject.repository")
//@SpringBootApplication
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
}