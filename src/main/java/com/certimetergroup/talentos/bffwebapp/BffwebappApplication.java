package com.certimetergroup.talentos.bffwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BffwebappApplication {

	static void main(String[] args) {
		SpringApplication.run(BffwebappApplication.class, args);
	}

}
