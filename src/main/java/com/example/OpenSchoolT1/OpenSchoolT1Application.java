package com.example.OpenSchoolT1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.example.OpenSchoolT1", "com.example.OpenSchoolT1Starter"})
public class OpenSchoolT1Application {

	public static void main(String[] args) {
		SpringApplication.run(OpenSchoolT1Application.class, args);
	}

}
