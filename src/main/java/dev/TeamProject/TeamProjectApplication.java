package dev.TeamProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
// @EnableMethodSecurity // The corresponding Spring Security configuration is not implemented (TODO)
public class TeamProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamProjectApplication.class, args);
	}


}
