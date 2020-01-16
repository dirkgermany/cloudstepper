package com.dam.provider.rest.consumer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;

import com.dam.exception.CsServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Client {
	@Autowired
	ConfigProperties config;

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public ResponseEntity<JsonNode> postLogin(JsonNode requestBody) throws CsServiceException {
		int index = config.getIndexPerDomain("AUTHENTICATION");
		String URI = config.getServiceUrl(index) + "/login";
		ResponseEntity<JsonNode> response = sendMessageWithBodyAsOptional(URI, requestBody, null, null,
				HttpMethod.POST);

		// Check the status of the response body, not of the transport layer
		HttpStatus httpStatus = HttpStatus.valueOf(response.getBody().get("httpStatus").asText());
		if (null == httpStatus || !httpStatus.is2xxSuccessful()) {
			// token could not be validated
			ResponseEntity<JsonNode> invalidTokenResponse = new ResponseEntity<JsonNode>(response.getBody(),
					httpStatus);
			return invalidTokenResponse;
		}
		return response;
	}

	public ResponseEntity<JsonNode> retrieveWrappedAuthorizedResponse(JsonNode request,
			Map<String, String> requestParams, @RequestHeader Map<String, String> headers, String serviceUrl,
			String action, String serviceDomain, HttpMethod httpMethod) throws CsServiceException {

		return retrieveResponse(request, requestParams, headers, serviceUrl, action, serviceDomain, httpMethod);
	}

	public ResponseEntity<JsonNode> retrieveResponse(JsonNode request, Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, String url, String action, String serviceDomain,
			HttpMethod httpMethod) throws CsServiceException {

		String tokenId = headers.get("tokenid");

		// calls AuthenticationService
		// and validates token
		JsonHelper jsonHelper = new JsonHelper();
		ResponseEntity<JsonNode> validatedTokenNode = validateToken(request, tokenId, serviceDomain.toString());

		JsonNode jsonBody = validatedTokenNode.getBody();
		HttpStatus httpStatus = HttpStatus.valueOf(jsonBody.get("httpStatus").asText());
		if (null == httpStatus || !httpStatus.is2xxSuccessful()) {
			// token could not be validated
			ResponseEntity<JsonNode> invalidTokenResponse = new ResponseEntity<JsonNode>(validatedTokenNode.getBody(),
					httpStatus);
			return invalidTokenResponse;
		}

		JsonNode permission = jsonHelper.extractNodeFromNode(jsonBody, "permission");
		String rights = jsonHelper.extractStringFromJsonNode(permission, "rights");
		Long loggedInUserId = jsonHelper.extractLongFromNode(jsonBody, "userId");
		String rightOption = jsonHelper.extractStringFromJsonNode(permission, "rightOption");

		headers.put("requestorUserId", loggedInUserId.toString());
		headers.put("rights", rights);
		if (null != rightOption && !rightOption.isEmpty()) {
			headers.put("rightOption", rightOption);
		}

		action = action.replace("/", "");
		String URI = url + "/" + action;

		try {
			return sendMessageWithBodyAsOptional(URI, request, requestParams, headers, httpMethod);
		} catch (Exception e) {
			logger.error("Service Provider :: Consumer {}: Message could not be send. URI {} - Request: {}",
					dateTimeFormatter.format(LocalDateTime.now()), URI, request);
			throw new CsServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}
	}

	/*
	 * Validates if the userName with the tokenId is logged in. This check is done
	 * by calling the Authentication Service.
	 */
	private ResponseEntity<JsonNode> validateToken(JsonNode request, String tokenId, String domainName)
			throws CsServiceException {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("tokenId", tokenId);
		headers.put("serviceDomain", domainName);

		Integer index = config.getIndexPerDomain("AUTHENTICATION");
		String url = config.getServiceUrl(index);
		String URI = url + "/" + "validateToken";

		try {
			return sendMessageWithBodyAsOptional(URI, request, null, headers, HttpMethod.POST);
		} catch (Exception e) {
			logger.error("Service Provider :: Consumer {}: Message could not be send. URI {} - Request: {}",
					dateTimeFormatter.format(LocalDateTime.now()), URI, request);
			throw new CsServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}
	}

	private ResponseEntity<JsonNode> sendMessageWithBodyAsOptional(String URI, JsonNode jsonNode,
			Map<String, String> requestParams, @RequestHeader Map<String, String> headers, HttpMethod httpMethod)
			throws CsServiceException {

		HttpEntity<JsonNode> requestBody = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();

		if (null != headers && null != jsonNode) {
			headers.forEach((key, value) -> {
				httpHeaders.add(key, value);
			});
			requestBody = new HttpEntity<>(jsonNode, httpHeaders);
		} else if (null != headers) {
			headers.forEach((key, value) -> {
				httpHeaders.add(key, value);
			});
			requestBody = new HttpEntity<>(httpHeaders);
		} else if (null != jsonNode) {
			requestBody = new HttpEntity<>(jsonNode);
		}

		if (null != requestParams && !requestParams.isEmpty()) {
			URI += "/?";

			String empersant = "";
			Iterator<Map.Entry<String, String>> it = requestParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				URI += empersant + entry.getKey() + "=" + encodeValue(entry.getValue());
				empersant = "&";
			}
		}
	
		//ToDo
		// Austauschen, damit auch Responses mit HttpStatus!= 2xxx behandelt werden können
		// https://www.baeldung.com/spring-5-webclient
		if (null == requestParams) {
			return restTemplate.exchange(URI, httpMethod, requestBody, JsonNode.class);
		} else {
			return restTemplate.exchange(URI, httpMethod, requestBody, JsonNode.class, requestParams);
		}
	}

	private String encodeValue(String value) throws CsServiceException {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new CsServiceException(404L, "Value could not be encoded for URL", e.getMessage());
		}
	}
}
