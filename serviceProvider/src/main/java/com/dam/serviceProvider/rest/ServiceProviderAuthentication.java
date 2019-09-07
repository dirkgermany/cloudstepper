package com.dam.serviceProvider.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.rest.consumer.Consumer;

@RestController
public class ServiceProviderAuthentication {
	@Autowired
	ConfigProperties config;
	
	@Autowired
	Consumer consumer;

	@PostMapping("/login")
	public JsonNode login(@RequestBody String request) throws DamServiceException {
		JsonNode response = consumer.retrieveResponse(request, config.getAuthenticationService().getServiceUrl(), "login");

		return response;
	}

	@PostMapping("/logout")
	public JsonNode logout(@RequestBody String request) throws DamServiceException {
		JsonNode response = consumer.retrieveResponse(request, config.getAuthenticationService().getServiceUrl(), "logout");

		return response;
	}

}