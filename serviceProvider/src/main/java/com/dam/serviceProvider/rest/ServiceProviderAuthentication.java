package com.dam.serviceProvider.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.types.ServiceDomain;

@RestController
public class ServiceProviderAuthentication extends ServiceProviderInternal{
	
	private static ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;
	
	@PostMapping("/login")
	public JsonNode login(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		JsonNode response = consumer.retrieveResponse(request, config.getServiceUrl(index), "login");

		return response;
	}

	@PostMapping("/logout")
	public JsonNode logout(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		JsonNode response = consumer.retrieveResponse(request, config.getServiceUrl(index), "logout");

		return response;
	}

}