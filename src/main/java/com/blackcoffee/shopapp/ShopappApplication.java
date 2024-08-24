package com.blackcoffee.shopapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication

@OpenAPIDefinition(
		info = @Info(
				title = "TechX| Shop App REST API Documentation",
				description = "Spring boot project REST API",
				version = "v1.0",
				contact = @Contact(
						name = "TRAN NGOC TIEN - PATRICK",
						email = "patricktrandev@gmail.com",
						url = "https://www.linkedin.com/in/patricktrandev/"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Github Repo for this project",
				url = "https://github.com/patricktrandev/project_management_backend"
		)
)
public class ShopappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopappApplication.class, args);
	}

}
