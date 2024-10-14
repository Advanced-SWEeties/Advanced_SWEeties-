package dev.TeamProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.*;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// TODO# this is only here for building when no data source supplied. One should uncomment the next
//  line (and delete this one) after setting up its own Mysql instance and change accordingly in
//  "application.properties".
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@SpringBootApplication
// @EnableMethodSecurity // The corresponding Spring Security configuration is not implemented (TODO)
public class TeamProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamProjectApplication.class, args);
	}


}
