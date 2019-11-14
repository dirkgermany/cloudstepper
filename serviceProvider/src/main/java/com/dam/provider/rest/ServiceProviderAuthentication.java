package com.dam.provider.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping("/login")
	public ResponseEntity<String> loginGet(@RequestParam Map<String, String> requestParams) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedGetResponse(requestParams, config.getServiceUrl(index), "login", null);
	}

	@PostMapping("/login")
	public ResponseEntity<JsonNode> loginPost(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "login", null);
	}

	@GetMapping("/logout")
	public ResponseEntity<JsonNode> logout(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "logout", null);

	}

}