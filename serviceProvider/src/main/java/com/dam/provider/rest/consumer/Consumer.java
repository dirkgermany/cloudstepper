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

	public ResponseEntity<JsonNode> postLogin(JsonNode requestBody) throws DamServiceException {
		ServiceDomain serviceDomain = ServiceDomain.AUTHENTICATION;
		int index = config.getIndexPerDomain(serviceDomain.name());
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
			String action, ServiceDomain serviceDomain, HttpMethod httpMethod) throws DamServiceException {

		return retrieveResponse(request, requestParams, headers, serviceUrl, action, serviceDomain, httpMethod);
	}

	public ResponseEntity<JsonNode> retrieveResponse(JsonNode request, Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, String url, String action, ServiceDomain serviceDomain,
			HttpMethod httpMethod) throws DamServiceException {

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

		headers.put("requestorUserId", loggedInUserId.toString());
		headers.put("rights", rights);

		action = action.replace("/", "");
		String URI = url + "/" + action;

		try {
			return sendMessageWithBodyAsOptional(URI, request, requestParams, headers, httpMethod);
		} catch (Exception e) {
			logger.error("Service Provider :: Consumer {}: Message could not be send. URI {} - Request: {}",
					dateTimeFormatter.format(LocalDateTime.now()), URI, request);
			throw new DamServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}
	}

	/*
	 * Validates if the userName with the tokenId is logged in. This check is done
	 * by calling the Authentication Service.
	 */
	private ResponseEntity<JsonNode> validateToken(JsonNode request, String tokenId, String domainName)
			throws DamServiceException {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("tokenId", tokenId);
		headers.put("serviceDomain", domainName);

		Integer index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
		String url = config.getServiceUrl(index);
		String URI = url + "/" + "validateToken";

		try {
			return sendMessageWithBodyAsOptional(URI, request, null, headers, HttpMethod.POST);
		} catch (Exception e) {
			logger.error("Service Provider :: Consumer {}: Message could not be send. URI {} - Request: {}",
					dateTimeFormatter.format(LocalDateTime.now()), URI, request);
			throw new DamServiceException(new Long(500), "Message could not be send. URI: " + URI, e.getMessage());
		}
	}

	private ResponseEntity<JsonNode> sendMessageWithBodyAsOptional(String URI, JsonNode jsonNode,
			Map<String, String> requestParams, @RequestHeader Map<String, String> headers, HttpMethod httpMethod)
			throws DamServiceException {

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

		if (null == requestParams) {
			return restTemplate.exchange(URI, httpMethod, requestBody, JsonNode.class);
		} else {
			return restTemplate.exchange(URI, httpMethod, requestBody, JsonNode.class, requestParams);
		}
	}

	private String encodeValue(String value) throws DamServiceException {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be encoded for URL", e.getMessage());
		}
	}
}
