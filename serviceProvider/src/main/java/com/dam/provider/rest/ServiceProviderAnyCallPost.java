package com.dam.provider.rest;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.dam.provider.types.RequestType;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCallPost extends MasterController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallPost.class);

	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	@Autowired
	ServiceProviderPing serviceProviderPing;

	@PostMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPost(@RequestBody JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPost(requestBody, servletRequest, params, headers);

	}

	@PostMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPost(@RequestBody JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPost(requestBody, servletRequest, params, headers);

	}

	@PostMapping("*")
	public ResponseEntity<JsonNode> singleSlashPost(@RequestBody JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPost(requestBody, servletRequest, params, headers);

	}

	public ResponseEntity<JsonNode> anyPost(@RequestBody JsonNode requestBody, HttpServletRequest servletRequest,
			Map<String, String> params, @RequestHeader Map<String, String> headers) {

		String requestUri;
		String[] pathParts;
		ServiceDomain serviceDomain;
		String subPath;
		String apiMethod;
		Map<String, String> decodedParams = new HashMap<>();

		try {
			decodedParams = decodeHttpMap(params);
			requestUri = servletRequest.getRequestURI();
			pathParts = getPathParts(servletRequest);
			serviceDomain = getServiceDomain(pathParts);
			subPath = getSubPath(pathParts);
			apiMethod = getApiMethod(pathParts, requestUri);

		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

		try {
			return consumer.retrieveWrappedAuthorizedResponse(requestBody, decodedParams, headers, getServiceUrl(serviceDomain),
					subPath + apiMethod, serviceDomain, RequestType.POST);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}
}
