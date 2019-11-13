package com.dam.provider.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins="*")
@RestController
public class TestController {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;
	
	private static ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;

	@GetMapping ("/test")
	ResponseEntity<JsonNode> getTest(@RequestBody String requestBody, HttpServletRequest httpRequest) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
	
//			JsonNode jsonData = consumer.retrieveResponse(requestBody, config.getServiceUrl(index), "login");
			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));

			return consumer.retrieveWrappedResponse(requestBody, config.getServiceUrl(index), "login", null);

	}

}
