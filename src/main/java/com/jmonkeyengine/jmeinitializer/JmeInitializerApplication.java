package com.jmonkeyengine.jmeinitializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JmeInitializerApplication {

	public static void main(String[] args) {
		SpringApplication.run(JmeInitializerApplication.class, args);
	}

}
