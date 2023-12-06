package com.crisis.esp32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Annotation SpringBoot Permettant de spécifier que cette classe est une application SpringBoot
@SpringBootApplication
public class Esp32Application {

	public static void main(String[] args) {
		SpringApplication.run(Esp32Application.class, args);
	}

}
