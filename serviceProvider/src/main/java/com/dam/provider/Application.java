package com.dam.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.dam.provider.rest.consumer.Client;

@EnableConfigurationProperties (ConfigProperties.class)
@SpringBootApplication
public class Application {
	
	@Autowired
	Client client;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}