package com.lifeverse.lifeverse_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.lifeverse")
@EnableJpaRepositories(basePackages = "com.lifeverse.auth.repository")
@EntityScan(basePackages = "com.lifeverse.auth.model")
public class LifeverseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeverseBackendApplication.class, args);
	}

}
