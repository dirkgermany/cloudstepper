package com.dam.provider.rest;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class ServiceProviderAnyCallPatch extends MasterController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallPatch.class);

	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	@PatchMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPatch(requestBody, servletRequest, params, headers);

	}

	@PatchMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPatch(requestBody, servletRequest, params, headers);

	}

	@PatchMapping("*")
	public ResponseEntity<JsonNode> singleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {

		return anyPatch(requestBody, servletRequest, params, headers);

	}

	public ResponseEntity<JsonNode> anyPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest,
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
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; Service:" + dse.getServiceName());
		}

		try {
			return consumer.retrieveWrappedAuthorizedResponse(requestBody, decodedParams, headers, getServiceUrl(serviceDomain),
					subPath + apiMethod, serviceDomain, HttpMethod.PATCH);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; Service:" + dse.getServiceName());
		}
	}
}
