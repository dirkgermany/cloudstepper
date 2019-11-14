package com.dam.provider.rest.consumer;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.JsonHelper;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Consumer {
	@Autowired
	ConfigProperties config;

	private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public ResponseEntity<JsonNode> retrieveWrappedResponse(String request, String url, String action, String tokenId)
			throws DamServiceException {
		return new ResponseEntity<>(retrievePostResponse(request, url, action, tokenId), new HttpHeaders(),
				HttpStatus.OK);
	}

	public ResponseEntity<String> retrieveWrappedGetResponse(Map<String, String> requestParams, String url,
			String action, String tokenId) throws DamServiceException {
		return new ResponseEntity<>(retrieveGetResponse(requestParams, url, action, tokenId), new HttpHeaders(),
				HttpStatus.OK);
	}

	public ResponseEntity<String> retrieveWrappedAuthorizedGetResponse(Map<String, String> requestParams,
			String serviceUrl, String action, ServiceDomain serviceDomain, String tokenId) throws DamServiceException {
		return new ResponseEntity<>(
				retrieveAuthorizedGetResponse(requestParams, serviceUrl, action, serviceDomain, tokenId),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<JsonNode> retrieveWrappedAuthorizedPostResponse(String request, String serviceUrl,
			String action, ServiceDomain serviceDomain, String tokenId) throws DamServiceException {
		return new ResponseEntity<JsonNode>(
				retrieveAuthorizedPostResponse(request, serviceUrl, action, serviceDomain, tokenId), new HttpHeaders(),
				HttpStatus.OK);
	}

	/**
	 * Send Request to any service
	 * 
	 * @param request
	 * @param url
	 * @param action
	 * @return
	 */
	public JsonNode retrievePostResponse(String request, String url, String action, String tokenId)
			throws DamServiceException {
//		JsonHelper jsonHelper = new JsonHelper();

		action = action.replace("/", "");
		String URI = url + "/" + action;
//		String tokenId = null;
//
//		if (null != request && !request.isEmpty()) {
//			tokenId = jsonHelper.extractStringFromRequest(request, "tokenId");
//		}

		String serviceResponse = null;
		try {
			serviceResponse = sendPostMessage(URI, request, tokenId);
		} catch (Exception e) {
			logger.error("Service Provider :: Consumer {}: Message could not be send. URI {} - Request: {}",
					dateTimeFormatter.format(LocalDateTime.now()), URI, request);
			throw new DamServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}

		if (null != serviceResponse) {
			return createJsonResponse(serviceResponse);
		}

		return null;
	}

	public String retrieveGetResponse(Map<String, String> requestParams, String url, String action, String tokenId)
			throws DamServiceException {
		return getMessage(url, action, requestParams, tokenId);
	}

	/**
	 * Calls a Microservice.
	 * 
	 * @param request       The original received request PLUS userId of the cal
	 *                      ling user PLUS the rights of the user
	 * @param serviceUrl
	 * @param action
	 * @param serviceDomain Needed to find out the rights associated to the service
	 *                      domain represented by the called Microservice.
	 * @return
	 * @throws DamServiceException
	 */
	public JsonNode retrieveAuthorizedPostResponse(String request, String serviceUrl, String action,
			ServiceDomain serviceDomain, String tokenId) throws DamServiceException {

		request = buildCompletedRequest(request, serviceDomain, tokenId);

		// DomainService call
		JsonNode response = retrievePostResponse(request, serviceUrl, action, tokenId);

		return response;
	}

	public String retrieveAuthorizedGetResponse(Map<String, String> requestParams, String serviceUrl, String action,
			ServiceDomain serviceDomain, String tokenId) throws DamServiceException {
		JsonHelper jsonHelper = new JsonHelper();

		JsonNode node = jsonHelper.createNodeFromMap(requestParams);
		JsonNode validatedTokenNode = validateToken(node.toString(), tokenId);
		Long returnCode = jsonHelper.extractLongFromNode(validatedTokenNode, "returnCode");

		if (null == returnCode || 200 != returnCode.longValue()) {
			// token could not be validated
			throw new DamServiceException(440L, "Invalid Token", "Token invalid, user not logged in");
		}
//		String validatedToken= jsonHelper.extractStringFromJsonNode(validatedTokenNode, "tokenId");
		requestParams.put("tokenId", tokenId);

		// calls AuthenticationService again
		Long loggedInUserId = new JsonHelper().extractLongFromNode(validatedTokenNode, "userId");
		String rights = requestPermission(loggedInUserId, serviceDomain.toString(), tokenId);
		requestParams.put("requestorUserId", loggedInUserId.toString());
		requestParams.put("rights", rights);

		return retrieveGetResponse(requestParams, serviceUrl, action, tokenId);
	}

	private String buildCompletedRequest(String request, ServiceDomain serviceDomain, String tokenId)
			throws DamServiceException {
		// calls AuthenticationService
		// and validates token
		JsonNode validatedToken = validateToken(request, tokenId);
		Long returnCode = new JsonHelper().extractLongFromNode(validatedToken, "returnCode");

		if (null == returnCode || 200 != returnCode.longValue()) {
			// token could not be validated
			throw new DamServiceException(440L, "Invalid Token", "Token invalid, user not logged in");
		}

		// calls AuthenticationService again
		// to get the rights (RWD-RWD) dependent to the user role
		Long loggedInUserId = new JsonHelper().extractLongFromNode(validatedToken, "userId");
		String rights = requestPermission(loggedInUserId, serviceDomain.toString(), tokenId);

		// Lookup for logged in user id and
		// enrich the request with the user id and rights
		return enrichRequest(request, validatedToken, rights);
	}

	private String requestPermission(Long userId, String serviceDomain, String tokenId) throws DamServiceException {
		JsonHelper jsonHelper = new JsonHelper();

		JsonNode node = jsonHelper.createEmptyNode();
		jsonHelper.addToJsonNode(node, "userId", userId);
		jsonHelper.addToJsonNode(node, "serviceDomain", serviceDomain);

		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		JsonNode response = retrievePostResponse(jsonHelper.extractStringFromJsonNode(node),
				config.getServiceUrl(index), "getUserPermission", tokenId);

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
	private JsonNode validateToken(String request, String tokenId) throws DamServiceException {
		// as long as Authentication Server doesn't works with GET the tokenId must be part of the request
		request = new JsonHelper().putToJsonNode(request, "tokenId", tokenId);
		
		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		JsonNode response = retrievePostResponse(request, config.getServiceUrl(index), "validateToken", tokenId);
		return response;
	}

	private String enrichRequest(String request, JsonNode validatedToken, String rights) {
		JsonHelper jsonHelper = new JsonHelper();
		Long loggedInUserId = jsonHelper.extractLongFromNode(validatedToken, "userId");

		request = jsonHelper.putToJsonNode(request, "requestorUserId", loggedInUserId.toString());
		request = jsonHelper.putToJsonNode(request, "rights", rights);

		return request;
	}

	private JsonNode createJsonResponse(String serviceResponse) {
		return new JsonHelper().convertStringToNode(serviceResponse);
	}

	private String postMessage(String URI, String request, String tokenId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (null != tokenId && !tokenId.isEmpty()) {
			headers.add("tokenId", tokenId);
		}
		HttpEntity<String> requestBody = new HttpEntity<String>(request, headers);
		RestTemplate restTemplate = new RestTemplate();
		String response = restTemplate.postForObject(URI, requestBody, String.class);

		return response;
	}

	public String getMessage(String URI, String params, String tokenId) {
		String extendedUri = URI + "?" + params;
		HttpHeaders headers = new HttpHeaders();
		headers.add("tokenId", tokenId);
		RestTemplate restTemplate = new RestTemplate();
//		String response = restTemplate.getForObject(extendedUri, String.class);

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(extendedUri, HttpMethod.GET, entity, String.class,
				params);
		return response.getBody();
	}

//	private String getMessage(String URI) {
//		RestTemplate restTemplate = new RestTemplate();
//		String response = restTemplate.getForObject(URI, String.class);
//		return response;
//	}

	private String getMessage(String URI, String action, Map<String, String> requestParams, String tokenId)
			throws DamServiceException {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		if (null != tokenId && !tokenId.isEmpty()) {
			headers.add("tokenId", tokenId);
		}

		String url = URI + action;
		if (null != requestParams && !requestParams.isEmpty()) {
			url += "/?";

			String empersant = "";
			Iterator<Map.Entry<String, String>> it = requestParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				url += empersant + entry.getKey() + "= " + encodeValue(entry.getValue());
				empersant = "&";
			}
		}

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class,
				requestParams);

		return response.getBody();

//		return restTemplate.getForObject(url, String.class);
	}

	private String encodeValue(String value) throws DamServiceException {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be encoded for URL", e.getMessage());
		}
	}

	private String sendPostMessage(String URI, String request, String tokenId) {
		return postMessage(URI, request, tokenId);
	}
}
