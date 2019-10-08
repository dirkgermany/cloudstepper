package com.dam.stock.rest.consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.dam.exception.DamServiceException;
import com.dam.stock.JsonHelper;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Consumer {

	/**
	 * Send Request to any service
	 * 
	 * @param request
	 * @param url
	 * @param action
	 * @return
	 */
	public JsonNode retrieveResponse(String request, String url, String action) throws DamServiceException {
		com.dam.stock.JsonHelper jsonHelper = new JsonHelper();

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
