package com.dam.serviceProvider.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.JsonHelper;
import com.dam.serviceProvider.rest.consumer.Consumer;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class ServiceProviderDemo {
	@Autowired
	ConfigProperties config;
	
	@Autowired
	Consumer consumer;
	
	@GetMapping("/person/getPersonDemo")
	public String getPersonDemo(Long personId) {
		return consumer.getMessage(config.getPersonService().getServiceUrl() + "/getPersonDemo", "personId=" + personId.toString());
	}

	@GetMapping("/address/getAddressDemo")
	public String getAddressDemo(Long personId) {
		return consumer.getMessage(config.getAddressService().getServiceUrl() + "/getAddressDemo", "personId=" + personId.toString());
	}
	
	@GetMapping("/demo")
	public String getDemoSet(Long personId) {
		String response = consumer.getMessage(config.getPersonService().getServiceUrl() + "/getPersonDemo", "personId=" + personId.toString());
		response += consumer.getMessage(config.getAddressService().getServiceUrl() + "/getAddressDemo", "personId=" + personId.toString());
		return response;	
	}
	
	@PostMapping("/demo/write")
	public String createPerson(@RequestBody String request) throws DamServiceException {
		JsonNode jsonNode = consumer.retrieveResponse(request, config.getPersonService().getServiceUrl(), "createPerson");
		Long personId = new JsonHelper().extractLongFromNode(jsonNode, "personId");
		
		String response = consumer.getMessage(config.getPersonService().getServiceUrl() + "/getPersonDemo", "personId=" + personId.toString());
		response += consumer.getMessage(config.getAddressService().getServiceUrl() + "/getAddressDemo", "personId=" + personId.toString());
		
		return response;	
	}
	
	

}
