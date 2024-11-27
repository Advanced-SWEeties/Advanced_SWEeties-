package dev.teamproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for handling Cross-Origin Resource Sharing (CORS) settings.
 * This configuration allows requests from specified frontend URLs and enables 
 * specific HTTP methods and headers.
 */
@Configuration
public class CorsConfig {

  /**
   * Configures the CORS settings for the application.
   * This method allows specific origins, HTTP methods, headers, and 
   * enables credentials for cross-origin requests.
   *  
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfig = new CorsConfiguration();

    corsConfig.addAllowedOrigin("http://localhost:3000");
    corsConfig.addAllowedOrigin("http://localhost:3001");  
    corsConfig.addAllowedOrigin("http://localhost:3002");
    corsConfig.addAllowedOrigin("https://vite-react-d721tb6rf-philluples-projects.vercel.app");
    corsConfig.addAllowedMethod("GET");
    corsConfig.addAllowedMethod("POST");
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedMethod("DELETE");
    corsConfig.addAllowedMethod("OPTIONS");

    corsConfig.addAllowedHeader("Authorization");
    corsConfig.addAllowedHeader("Content-Type");

    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return source;
  }
}
