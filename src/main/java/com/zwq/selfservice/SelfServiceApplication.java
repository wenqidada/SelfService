package com.zwq.selfservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zwq.selfservice.dao")
public class SelfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SelfServiceApplication.class, args);
	}

}
