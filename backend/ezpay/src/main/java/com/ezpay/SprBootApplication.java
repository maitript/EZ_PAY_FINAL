package com.ezpay;
/**
 * 
 * 
 * @author Ravva Amrutha
 * @date 11-09-29024 */
import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.ezpay")
public class SprBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprBootApplication.class, args);
	}

}
