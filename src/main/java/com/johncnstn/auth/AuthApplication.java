package com.johncnstn.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class AuthApplication {

	public static void main(final String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}
