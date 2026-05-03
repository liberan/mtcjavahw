package com.mipt.angelikaliber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AngelikaliberApplication {

	public static void main(String[] args) {
		SpringApplication.run(AngelikaliberApplication.class, args);
	}

}
