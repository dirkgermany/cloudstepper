package com.dam.serviceProvider.rest.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.JsonHelper;
import com.dam.serviceProvider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Consumer {
	@Autowired
	ConfigProperties config;

	/**
	 * Send Request to any service
	 * 
	 * @param request
	 * @param url
	 * @param action
	 * @return
	 */
	public JsonNode retrieveResponse(String request, String url, String action) throws DamServiceException {
		JsonHelper jsonHelper = new JsonHelper();

		action = action.replace("/", "");
		String URI = url + "/" + action;
		String tokenId = null;

		if (null != request && !request.isEmpty()) {
			tokenId = jsonHelper.extractStringFromRequest(request, "tokenId");
		}

		String serviceResponse = null;
		try {
			serviceResponse = sendMessage(URI, request);
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}

		if (null != serviceResponse) {
			return createJsonResponse(serviceResponse, tokenId);
		}

		return null;
	}

	public JsonNode retrieveGetResponse(String url, String action) {
		JsonHelper jsonHelper = new JsonHelper();
		String URI = url + "/" + action;

		String serviceResponse = getMessage(URI);
		if (null != serviceResponse) {
			return jsonHelper.convertStringToNode(serviceResponse);
		}
		return null;
	}

	/**
	 * Calls a Microservice.
	 * 
	 * @param request       The original received request PLUS userId of the calling
	 *                      user PLUS the rights of the user
	 * @param serviceUrl
	 * @param action
	 * @param serviceDomain Needed to find out the rights associated to the service
	 *                      domain represented by the called Microservice.
	 * @return
	 * @throws DamServiceException 
	 */
	public JsonNode retrieveAuthorizedResponse(String request, String serviceUrl, String action,
			ServiceDomain serviceDomain) throws DamServiceException{
		// calls AuthenticationService
		// and validates token
		JsonNode validatedToken = validateToken(request);
		Long returnCode = new JsonHelper().extractLongFromNode(validatedToken, "returnCode");

		if (null == returnCode || 200 != returnCode.longValue()) {
			// token could not be validated
			return validatedToken;
		}

		// calls AuthenticationService again
		// to get the rights (RWD-RWD) dependent to the user role
		Long userId = new JsonHelper().extractLongFromNode(validatedToken, "userId");
		String rights = requestPermission(userId, serviceDomain.toString());

		// Lookup for logged in user id and
		// enrich the request with the user id and rights
		request = enrichRequest(request, validatedToken, rights);

		// DomainService call
		JsonNode response = retrieveResponse(request, serviceUrl, action);

		return response;
	}

	private String requestPermission(Long userId, String serviceDomain) throws DamServiceException{
		JsonHelper jsonHelper = new JsonHelper();

		JsonNode node = jsonHelper.createEmptyNode();
		jsonHelper.addToJsonNode(node, "userId", userId);
		jsonHelper.addToJsonNode(node, "serviceDomain", serviceDomain);

		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		JsonNode response = retrieveResponse(jsonHelper.extractStringFromJsonNode(node),
				config.getServiceUrl(index), "getUserPermission");

		if (null != response && !response.isMissingNode()) {
			JsonNode permission = jsonHelper.extractNodeFromNode(response, "permission");
			if (null != permission && !permission.isMissingNode()) {
				return jsonHelper.extractStringFromJsonNode(permission, "rights");
			}
		}
		throw new AuthorizationServiceException(new Long(1), "Unknown Error", "Error could not be evaluated");
	}

	/*
	 * Validates if the userName with the tokenId is logged in. This check is done
	 * by calling the Authentication Service.
	 * 
	 * @ToDo Later this should throw an exception to ensure that no caller can
	 * ignore invalid Tokens
	 */
	private JsonNode validateToken(String request) throws  DamServiceException {
		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		JsonNode response = retrieveResponse(request, config.getServiceUrl(index),
				"validateToken");
		return response;
	}

	/*
	 * 
	 */
	private String enrichRequest(String request, JsonNode validatedToken, String rights) {
		JsonHelper jsonHelper = new JsonHelper();
		Long loggedInUserId = jsonHelper.extractLongFromNode(validatedToken, "userId");

		request = jsonHelper.putToJsonNode(request, "requestorUserId", loggedInUserId.toString());
		request = jsonHelper.putToJsonNode(request, "rights", rights);

		return request;
	}

	private JsonNode createJsonResponse(String serviceResponse, String tokenId) {
		return new JsonHelper().convertStringToNode(serviceResponse);

	}

	private String postMessage(String URI, String request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBody = new HttpEntity<String>(request, headers);
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.postForObject(URI, requestBody, String.class);

		return response;
	}

	public String getMessage(String URI, String params) {
		String extendedUri = URI + "?" + params;
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(extendedUri, String.class);
		return response;
	}

	private String getMessage(String URI) {
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.getForObject(URI, String.class);
		return response;
	}

	private String sendMessage(String URI, String request) {
		if (null == request || request.isEmpty()) {
			return getMessage(URI);
		} else {
			return postMessage(URI, request);
		}
	}
}
