package com.dam.serviceProvider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.rest.consumer.Consumer;
import com.dam.serviceProvider.types.ServiceDomain;

@RestController
public class ServiceProviderUser {
	@Autowired
	ConfigProperties config;
	
	@Autowired
	Consumer consumer;
	
	private ServiceDomain serviceDomain = ServiceDomain.USER;

	/**
	 * Retrieves user data from user repository.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/user/get")
	public JsonNode getUser(@RequestBody String request, HttpServletRequest servletRequest) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getUserService().getServiceUrl(), "getUser", serviceDomain);
		System.out.println("HOST: " + servletRequest.getRemoteHost());
		
		return response;
	}

	@PostMapping("/user/create")
	public JsonNode createUser(@RequestBody String request) throws DamServiceException {

		// No token validation here for new user registration
		JsonNode response = consumer.retrieveResponse(request, config.getUserService().getServiceUrl(), "createUser");

		return response;
	}
	
	@PostMapping("/user/update")
	public JsonNode updateUser(@RequestBody String request) throws DamServiceException{

		// No token validation here for new user registration
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getUserService().getServiceUrl(), "updateUser", serviceDomain);

		return response;
	}
	
	@PostMapping("/user/drop")
	public JsonNode dropUser(@RequestBody String request) throws DamServiceException{

		// No token validation here for new user registration
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getUserService().getServiceUrl(), "dropUser", serviceDomain);

		return response;
	}
	
}