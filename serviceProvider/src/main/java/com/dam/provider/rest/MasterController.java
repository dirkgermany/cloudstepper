package com.dam.provider.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.JsonHelper;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

public class MasterController {
	
	@Autowired
	ConfigProperties config;

	protected String decode(String value) throws DamServiceException {
		if (null == value)
			return null;

		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString()).trim();
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be decode for URL", e.getMessage());
		}
	}

	protected Map<String, String> decodeHttpMap(Map<String, String> params) throws DamServiceException{
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

	protected String[] getPathParts(HttpServletRequest servletRequest) throws DamServiceException {
		String requestUri = servletRequest.getRequestURI();

		if (requestUri.contains("/")) {
			return requestUri.split("/");
		} else {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}

	protected String getServiceUrl(ServiceDomain domain) throws DamServiceException {
		if (null == domain) {
			throw new DamServiceException(400L, "Konfigurationsfehler", "domain ist null");
		}
		Integer index = config.getIndexPerDomain(domain.name());
		return config.getServiceUrl(index);
	}

	
	protected String extractTokenFromRequest(JsonNode requestBody) throws DamServiceException {
		if (null != requestBody) {
			return new JsonHelper().extractStringFromJsonNode(requestBody, "tokenId");
		}
		throw new DamServiceException(404L, "Missing tokenId", "Request not processed while tokenId was not received");
	}

	protected String getApiMethod(String[] pathParts, String requestUri) throws DamServiceException {
		try {
			return decode(pathParts[pathParts.length - 1]);

		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}

	protected String getSubPath(String[] pathParts) throws DamServiceException {
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

	protected ServiceDomain getServiceDomain(String[] pathParts) throws DamServiceException {
		String domain = decode(pathParts[1]);

		try {
			return ServiceDomain.valueOf(domain.toUpperCase());
		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad", "Domäne existiert nicht: " + domain.toUpperCase());
		}
	}
}
