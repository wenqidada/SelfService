package com.zwq.selfservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.zwq.selfservice.dao")
@EnableScheduling
public class SelfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SelfServiceApplication.class, args);
	}

}
