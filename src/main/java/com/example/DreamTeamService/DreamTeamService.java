package com.example.DreamTeamService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DreamTeamService {

	public static void main(String[] args) {

		ApplicationContext con =  SpringApplication.run(DreamTeamService.class, args);
	}

}
