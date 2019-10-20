package com.dam.provider.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins="*")
@RestController
public class ServiceProviderAnyCall {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;
	
	@Autowired
	ServiceProviderPing serviceProviderPing;
	
	@GetMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashGet(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
				
		return anyPost(requestBody, servletRequest);
		
	}

	@PostMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
				
		return anyPost(requestBody, servletRequest);
		
	}

	@GetMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashGet(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}

	@PostMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}

	@GetMapping("*")
	public ResponseEntity<JsonNode> singleSlashGet(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}

	@PostMapping("*")
	public ResponseEntity<JsonNode> singleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}


	public ResponseEntity<JsonNode> anyPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
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
//		if (domain.equalsIgnoreCase("login")) {
//			int index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
//			JsonNode response = consumer.retrieveResponse(requestBody, config.getServiceUrl(index), "login");
//
//			return response;			
//		}
//		
//		if (domain.equalsIgnoreCase("logout")) {
//			int index = config.getIndexPerDomain(ServiceDomain.AUTHENTICATION.name());
//			JsonNode response = consumer.retrieveResponse(requestBody, config.getServiceUrl(index), "logout");
//
//			return response;			
//		}
				
		
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

		return consumer.retrieveWrappedAuthorizedResponse(requestBody, getServiceUrl(serviceDomain), subPath + apiMethod,
				serviceDomain);
	}

	protected String getServiceUrl(ServiceDomain domain) throws DamServiceException {
		if (null == domain) {
			throw new DamServiceException(400L, "Konfigurationsfehler", "domain ist null");
		}
		Integer index = config.getIndexPerDomain(domain.name());
		return config.getServiceUrl(index);
	}

}
