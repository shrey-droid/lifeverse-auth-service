package com.lifeverse.lifeverse_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.lifeverse")  // ✅ scans all sub-packages
@EnableJpaRepositories(basePackages = "com.lifeverse")  // ✅ not just auth.repository
@EntityScan(basePackages = "com.lifeverse")  // ✅ includes resume.entity too
public class LifeverseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifeverseBackendApplication.class, args);
	}
}
