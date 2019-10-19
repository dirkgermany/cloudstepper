package com.dam.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;

@CrossOrigin(origins="*")
@RestController
public class ServiceProviderAuthentication {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;
	
	
	private static ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;
	
	@PostMapping("/login")
	public JsonNode login(@RequestBody String request, HttpServletRequest bla) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		bla.getRemoteUser();
		bla.getRemoteAddr();
		bla.getRemoteHost();

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