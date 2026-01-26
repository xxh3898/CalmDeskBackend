package com.code808.calmdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CalmdeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalmdeskApplication.class, args);
	}

}
