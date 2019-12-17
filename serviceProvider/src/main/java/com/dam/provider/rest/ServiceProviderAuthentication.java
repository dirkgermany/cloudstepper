package com.dam.provider.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAuthentication {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	private static ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;

	@GetMapping("/login")
	public ResponseEntity<String> loginGet(@RequestParam Map<String, String> requestParams) {
		try {
			int index = config.getIndexPerDomain(serviceDomain.name());
			
			ResponseEntity<String> bla = consumer.retrieveWrappedGetResponse(requestParams, config.getServiceUrl(index), "login", null);
			
			return bla;
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User or Password not valid.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

	}

	@GetMapping("/logout")
	public ResponseEntity<JsonNode> logout(@RequestBody String request) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedResponse(request, config.getServiceUrl(index), "logout", null);

	}

}