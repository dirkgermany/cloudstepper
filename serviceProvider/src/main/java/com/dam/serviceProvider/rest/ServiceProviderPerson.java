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
public class ServiceProviderPerson {
	@Autowired
	ConfigProperties config;
	
	@Autowired
	Consumer consumer;
	
	private ServiceDomain serviceDomain = ServiceDomain.PERSON;
	

	/**
	 * Retrieves person data from person repository.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/person/get")
	public JsonNode getPerson(@RequestBody String request, HttpServletRequest servletRequest) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getPersonService().getServiceUrl(), "getPerson", serviceDomain);		
		return response;
	}

	@PostMapping("/person/create")
	public JsonNode createPerson(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getPersonService().getServiceUrl(), "createPerson", serviceDomain);
		return response;
	}
	
	@PostMapping("/person/update")
	public JsonNode updatePerson(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getPersonService().getServiceUrl(), "updatePerson", serviceDomain);
		return response;
	}
	
	@PostMapping("/person/drop")
	public JsonNode dropPerson(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getPersonService().getServiceUrl(), "dropPerson", serviceDomain);
		return response;
	}
	
}