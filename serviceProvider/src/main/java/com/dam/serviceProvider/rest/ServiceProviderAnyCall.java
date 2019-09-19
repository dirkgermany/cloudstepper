package com.dam.serviceProvider.rest;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.JsonHelper;
import com.dam.serviceProvider.rest.consumer.Consumer;
import com.dam.serviceProvider.rest.service.message.PingResponse;
import com.dam.serviceProvider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class ServiceProviderAnyCall {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;
	
	@Autowired
	ServiceProviderPing serviceProviderPing;
	
	@PostMapping("/*/*")
	public JsonNode doubleSlash(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}

	@PostMapping("/*/*/*")
	public JsonNode tripleSlash(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}


	@PostMapping("/*")
	public JsonNode anyPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		String requestUri = servletRequest.getRequestURI();
		String[] pathParts = null;

		if (requestUri.contains("/")) {
			pathParts = requestUri.split("/");
		} else {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}

		String domain = pathParts[1];

		// Ausnahmefälle - direkte interne Bearbeitung
		if (domain.equalsIgnoreCase("login")) {
			int index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
			JsonNode response = consumer.retrieveResponse(requestBody, config.getServiceUrl(index), "login");

			return response;			
		}
		
		if (domain.equalsIgnoreCase("logout")) {
			int index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
			JsonNode response = consumer.retrieveResponse(requestBody, config.getServiceUrl(index), "logout");

			return response;			
		}
				
		
		ServiceDomain serviceDomain = null;
		try {
			serviceDomain = ServiceDomain.valueOf(domain.toUpperCase());
		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad", "Domäne existiert nicht: " + requestUri);
		}
		
		String subPath = "/";
		try {
			int index = 2;
			while (pathParts.length > index+1) {
				subPath+= pathParts[index] + "/";
			}
		}catch (Exception e) {
			// nothing to do
		}

		String apiMethod = null;
		try {
			apiMethod = pathParts[pathParts.length -1];

		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}

		JsonNode response = consumer.retrieveAuthorizedResponse(requestBody, getServiceUrl(serviceDomain), subPath + apiMethod,
				serviceDomain);
		return response;
	}

	protected String getServiceUrl(ServiceDomain domain) throws DamServiceException {
		if (null == domain) {
			throw new DamServiceException(400L, "Konfigurationsfehler", "domain ist null");
		}
		Integer index = config.getIndexPerDomain(domain.name());
		return config.getServiceUrl(index);
	}

}