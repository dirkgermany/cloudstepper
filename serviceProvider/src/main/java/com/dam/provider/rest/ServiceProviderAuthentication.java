package com.dam.provider.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAuthentication {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	private static ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;

	@PostMapping("/login")
	public ResponseEntity<String> loginPost(@RequestBody JsonNode loginBody, @RequestHeader Map<String, String> headers) {
		try {
			int index = config.getIndexPerDomain(serviceDomain.name());
			ResponseEntity<String> bla = consumer.retrieveWrappedPostResponse(loginBody,  null, headers, config.getServiceUrl(index), "login", null);
			
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

	@GetMapping("/login")
	public ResponseEntity<String> loginGet(@RequestParam Map<String, String> requestParams, @RequestHeader Map<String, String> headers) {
		try {
			int index = config.getIndexPerDomain(serviceDomain.name());
			return consumer.retrieveWrappedGetResponse(requestParams,  headers, config.getServiceUrl(index), "login", null);
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
	public ResponseEntity<String> logout(Map<String, String> requestParams) throws DamServiceException {
		int index = config.getIndexPerDomain(serviceDomain.name());
		return consumer.retrieveWrappedGetResponse(requestParams, null, config.getServiceUrl(index), "logout", null);

	}

}