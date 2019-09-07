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
public class ServiceProviderAddress {
	@Autowired
	ConfigProperties config;
	
	@Autowired
	Consumer consumer;
	
	private ServiceDomain serviceDomain = ServiceDomain.ADDRESS;


	/**
	 * Retrieves address data from address repository.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/address/get")
	public JsonNode getAddress(@RequestBody String request, HttpServletRequest servletRequest) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getAddressService().getServiceUrl(), "getAddress", serviceDomain);		
		return response;
	}

	@PostMapping("/address/list")
	public JsonNode getAddressList(@RequestBody String request, HttpServletRequest servletRequest) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getAddressService().getServiceUrl(), "listAddresses", serviceDomain);		
		return response;
	}

	@PostMapping("/address/create")
	public JsonNode createAddress(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getAddressService().getServiceUrl(), "createAddress", serviceDomain);
		return response;
	}
	
	@PostMapping("/address/update")
	public JsonNode updateAddress(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getAddressService().getServiceUrl(), "updateAddress", serviceDomain);
		return response;
	}
	
	@PostMapping("/address/drop")
	public JsonNode dropAddress(@RequestBody String request) throws DamServiceException{
		JsonNode response = consumer.retrieveAuthorizedResponse(request, config.getAddressService().getServiceUrl(), "dropAddress", serviceDomain);
		return response;
	}
	
}