package com.example.ChatDatabaseApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ChatDatabaseAppApplication {

	public static void main(String[] args) {

		ApplicationContext con =  SpringApplication.run(ChatDatabaseAppApplication.class, args);
	}

}
