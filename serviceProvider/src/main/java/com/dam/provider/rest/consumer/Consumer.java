package com.dam.provider.rest.consumer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

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

	public ResponseEntity<String> retrieveWrappedGetResponse(Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, String url, String action, String tokenId)
			throws DamServiceException {
		return new ResponseEntity<>(retrieveGetResponse(requestParams, headers, url, action), new HttpHeaders(),
				HttpStatus.OK);
	}

	public ResponseEntity<String> retrieveWrappedAuthorizedGetResponse(Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, String serviceUrl, String action, ServiceDomain serviceDomain) throws DamServiceException {
		return new ResponseEntity<>(
				retrieveAuthorizedGetResponse(requestParams, headers, serviceUrl, action, serviceDomain),
				new HttpHeaders(), HttpStatus.OK);
	}

	public ResponseEntity<JsonNode> retrieveWrappedAuthorizedPostResponse(JsonNode request, Map<String, String> params,
			@RequestHeader Map<String, String> headers, String serviceUrl, String action, ServiceDomain serviceDomain)
			throws DamServiceException {
		return new ResponseEntity<JsonNode>(
				retrieveAuthorizedPostResponse(request, params, headers, serviceUrl, action, serviceDomain),
				new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Send Request to any service
	 * 
	 * @param request
	 * @param url
	 * @param action
	 * @return
	 */
	public JsonNode retrievePostResponse(JsonNode request, Map<String, String> params,
			@RequestHeader Map<String, String> headers, String url, String action, ServiceDomain serviceDomain)
			throws DamServiceException {

		String tokenId = headers.get("tokenid");

		// calls AuthenticationService
		// and validates token
		JsonHelper jsonHelper = new JsonHelper();
		JsonNode validatedTokenNode = validateToken(request, tokenId, serviceDomain.toString());
		Long returnCode = jsonHelper.extractLongFromNode(validatedTokenNode, "returnCode");

		if (null == returnCode || 200 != returnCode.longValue()) {
			// token could not be validated
			throw new DamServiceException(440L, "Invalid Token", "Token invalid, user not logged in");
		}

		JsonNode permission = jsonHelper.extractNodeFromNode(validatedTokenNode, "permission");
		String rights = jsonHelper.extractStringFromJsonNode(permission, "rights");
		Long loggedInUserId = jsonHelper.extractLongFromNode(validatedTokenNode, "userId");

		headers.put("requestorUserId", loggedInUserId.toString());
		headers.put("rights", rights);

		action = action.replace("/", "");
		String URI = url + "/" + action;
		String serviceResponse = null;
		String requestBody = request.toString();

		try {
			serviceResponse = sendPostMessage(URI, requestBody, params, headers);
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

	public String retrieveGetResponse(Map<String, String> requestParams, @RequestHeader Map<String, String> headers,
			String url, String action) throws DamServiceException {
		return getMessage(url, action, requestParams, headers);
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
	public JsonNode retrieveAuthorizedPostResponse(JsonNode request, Map<String, String> params,
			@RequestHeader Map<String, String> headers, String serviceUrl, String action, ServiceDomain serviceDomain)
			throws DamServiceException {

		JsonNode response = retrievePostResponse(request, params, headers, serviceUrl, action, serviceDomain);
		return response;
	}

	public String retrieveAuthorizedGetResponse(Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, String serviceUrl, String action, ServiceDomain serviceDomain) throws DamServiceException {
		String tokenId = headers.get("tokenid");
		JsonHelper jsonHelper = new JsonHelper();

		JsonNode node = jsonHelper.createNodeFromMap(requestParams);
		JsonNode validatedTokenNode = validateToken(node, tokenId, serviceDomain.toString());
		Long returnCode = jsonHelper.extractLongFromNode(validatedTokenNode, "returnCode");

		if (null == returnCode || 200 != returnCode.longValue()) {
			// token could not be validated
			throw new DamServiceException(440L, "Invalid Token", "Token invalid, user not logged in");
		}

		JsonNode permission = jsonHelper.extractNodeFromNode(validatedTokenNode, "permission");
		String rights = jsonHelper.extractStringFromJsonNode(permission, "rights");

		// calls AuthenticationService again
		Long loggedInUserId = jsonHelper.extractLongFromNode(validatedTokenNode, "userId");

		headers.put("requestorUserId", loggedInUserId.toString());
		headers.put("rights", rights);

		return retrieveGetResponse(requestParams, headers, serviceUrl, action);
	}

	/*
	 * Validates if the userName with the tokenId is logged in. This check is done
	 * by calling the Authentication Service.
	 * 
	 * @ToDo Later this should throw an exception to ensure that no caller can
	 * ignore invalid Tokens
	 */
	private JsonNode validateToken(JsonNode request, String tokenId, String domainName) throws DamServiceException {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("tokenId", tokenId);
		headers.put("serviceDomain", domainName);

		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		String url = config.getServiceUrl(index);
		String URI = url + "/" + "validateToken";
		String serviceResponse = null;
		String requestBody = request.toString();

		try {
			serviceResponse = sendPostMessage(URI, requestBody, null, headers);
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

	private JsonNode createJsonResponse(String serviceResponse) {
		return new JsonHelper().convertStringToNode(serviceResponse);
	}

	private String postMessage(String URI, String request, Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
		if (null != headers) {
			headers.forEach((key, value) -> {
				httpHeaders.add(key, value);
			});
		}

		HttpEntity<String> requestBody = new HttpEntity<String>(request, httpHeaders);
		RestTemplate restTemplate = new RestTemplate();

		if (null == params) {
			return restTemplate.postForObject(URI, requestBody, String.class);
		} else {
			return restTemplate.postForObject(URI, requestBody, String.class, params);
		}

	}

	public String getMessage(String URI, String params, String tokenId) {
		String extendedUri = URI + "?" + params;
		HttpHeaders headers = new HttpHeaders();
		headers.add("tokenId", tokenId);
		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(extendedUri, HttpMethod.GET, entity, String.class,
				params);
		return response.getBody();
	}

	private String getMessage(String URI, String action, Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers) throws DamServiceException {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders httpHeaders = new HttpHeaders();
		if (null != headers) {
			headers.forEach((key, value) -> {
				httpHeaders.add(key, value);
			});
		}

		String url = URI + action;
		if (null != requestParams && !requestParams.isEmpty()) {
			url += "/?";

			String empersant = "";
			Iterator<Map.Entry<String, String>> it = requestParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				url += empersant + entry.getKey() + "=" + encodeValue(entry.getValue());
				empersant = "&";
			}
		}

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class,
				requestParams);

		return response.getBody();
	}

	private String encodeValue(String value) throws DamServiceException {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be encoded for URL", e.getMessage());
		}
	}

	private String sendPostMessage(String URI, String request, Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return postMessage(URI, request, params, headers);
	}
}
