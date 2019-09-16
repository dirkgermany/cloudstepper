package com.dam.serviceProvider.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.rest.consumer.Consumer;

@RestController
public class ServiceProviderInternal {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

}
