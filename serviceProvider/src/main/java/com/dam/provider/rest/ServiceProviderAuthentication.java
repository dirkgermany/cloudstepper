package com.dam.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/login")
	public ResponseEntity<JsonNode> loginGet(@RequestBody String request, HttpServletRequest bla) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "login");
	}

	@PostMapping("/login")
	public ResponseEntity<JsonNode> loginPost(@RequestBody String request, HttpServletRequest bla) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "login");
	}

	@GetMapping("/logout")
	public ResponseEntity<JsonNode> logout(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "logout");

	}

}