package com.dam.provider.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.CsServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.JsonHelper;
import com.dam.provider.rest.consumer.Client;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class MasterController {

	@Autowired
	ConfigProperties config;

	@Autowired
	Client client;

	protected String requestUri;
	protected String[] pathParts;
	protected String serviceDomain;
	protected String subPath;
	protected String apiMethod;
	protected Map<String, String> decodedParams = null;

	protected ResponseEntity<JsonNode> anyHttpRequest(@RequestBody(required = false) JsonNode requestBody,
			HttpServletRequest servletRequest, Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers, HttpMethod httpMethod) {

		// if first call in lifetime initialize configuration of provider
		try {
			config.init();
		} catch (CsServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		prepareHttpMessageParts(requestParams, servletRequest, requestBody);
		try {
			return client.retrieveWrappedAuthorizedResponse(requestBody, decodedParams, headers,
					config.getServiceUrl(serviceDomain), subPath + apiMethod, serviceDomain, httpMethod);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (CsServiceException dse) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ErrorId: " + dse.getErrorId() + "; "
					+ dse.getDescription() + "; " + dse.getShortMsg() + "; Service:" + dse.getServiceName());
		}
	}

	protected void prepareHttpMessageParts(@RequestParam Map<String, String> requestParams,
			HttpServletRequest servletRequest, JsonNode requestBody) throws ResponseStatusException {

		try {
			decodedParams = decodeHttpMap(requestParams);
			requestUri = servletRequest.getRequestURI();
			pathParts = getPathParts(servletRequest);
			serviceDomain = getServiceDomain(pathParts);
			subPath = getSubPath(pathParts);
			apiMethod = getApiMethod(pathParts, requestUri);

		} catch (CsServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

	protected String decode(String value) throws CsServiceException {
		if (null == value)
			return null;

		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString()).trim();
		} catch (UnsupportedEncodingException e) {
			throw new CsServiceException(404L, "Value could not be decode for URL", e.getMessage());
		}
	}

	protected Map<String, String> decodeHttpMap(Map<String, String> params) throws CsServiceException {
		Map<String, String> decodedMap = new HashMap<>();
		if (null != params && !params.isEmpty()) {
			Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				decodedMap.put(decode(entry.getKey()), decode(entry.getValue()));
			}
		}
		return decodedMap;
	}

	protected String[] getPathParts(HttpServletRequest servletRequest) throws CsServiceException {
		String requestUri = servletRequest.getRequestURI();

		if (requestUri.contains("/")) {
			return requestUri.split("/");
		} else {
			throw new CsServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}

	protected String getServiceUrl(String domain) throws CsServiceException {
		if (null == domain) {
			throw new CsServiceException(400L, "Konfigurationsfehler", "domain ist null");
		}
		return config.getServiceUrl(domain);
//		Integer index = config.getIndexPerDomain(domain);
//		return config.getServiceUrl(index);
	}

	protected String extractTokenFromRequest(JsonNode requestBody) throws CsServiceException {
		if (null != requestBody) {
			return new JsonHelper().extractStringFromJsonNode(requestBody, "tokenId");
		}
		throw new CsServiceException(404L, "Missing tokenId", "Request not processed while tokenId was not received");
	}

	protected String getApiMethod(String[] pathParts, String requestUri) throws CsServiceException {
		try {
			return decode(pathParts[pathParts.length - 1]);

		} catch (Exception e) {
			throw new CsServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}

	protected String getSubPath(String[] pathParts) throws CsServiceException {
		String subPath = "/";
		try {
			int index = 2;
			while (pathParts.length > index + 1) {
				subPath += decode(pathParts[index]) + "/";
			}
		} catch (Exception e) {
			// nothing to do
		}
		return subPath;
	}

	protected String getServiceDomain(String[] pathParts) throws CsServiceException {
		if (null == pathParts || pathParts.length == 0) {
			System.out.println("MasterCrontroller::getServiceDomain pathParts sind NULL");
			throw new CsServiceException(500L, "Ungültiger Pfad", "Domäne existiert nicht: PathParts=" + pathParts);
		}
		String domain = decode(pathParts[1]);

		try {
			return domain.toUpperCase();
		} catch (Exception e) {
			throw new CsServiceException(500L, "Ungültiger Pfad", "Domäne existiert nicht: " + domain.toUpperCase());
		}
	}
}
