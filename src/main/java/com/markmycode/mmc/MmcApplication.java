package com.markmycode.mmc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.markmycode.mmc.post")
public class MmcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmcApplication.class, args);
	}

}
