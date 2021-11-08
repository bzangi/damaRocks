package com.qulture.draughts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DraughtsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DraughtsApplication.class, args);
		System.out.println("SERVIDOR TA ONLINE");
	}

}
