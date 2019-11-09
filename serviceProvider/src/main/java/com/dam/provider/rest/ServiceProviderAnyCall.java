package com.dam.provider.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins="*")
@RestController
public class ServiceProviderAnyCall {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCall.class);
	
	
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;
	
	@Autowired
	ServiceProviderPing serviceProviderPing;
	
	@GetMapping("/*/*")
	public ResponseEntity<String> doubleSlashGet(@RequestParam Map<String,String> params, HttpServletRequest servletRequest)
			throws DamServiceException {
				
		return anyGet(params, servletRequest);
		
	}

	@PostMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
				
		return anyPost(requestBody, servletRequest);
		
	}

	@GetMapping("/*/*/*")
	public ResponseEntity<String> tripleSlashGet(@RequestParam Map<String,String> params, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyGet(params, servletRequest);
		
	}

	@PostMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}

	@PostMapping("*")
	public ResponseEntity<JsonNode> singleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		return anyPost(requestBody, servletRequest);
		
	}
	
	@GetMapping("*")
	public ResponseEntity<String> singleSlashGet(@RequestParam Map<String,String> params, HttpServletRequest servletRequest) throws DamServiceException {
		return anyGet(params, servletRequest);
	}

	private ResponseEntity<String> anyGet(@RequestParam Map<String,String> params, HttpServletRequest servletRequest) throws DamServiceException {
		String requestUri = servletRequest.getRequestURI();
		String[] pathParts = getPathParts(servletRequest);
		ServiceDomain serviceDomain = getServiceDomain(pathParts);
		String subPath = getSubPath(pathParts);
		String apiMethod = getApiMethod(pathParts, requestUri);

		return consumer.retrieveWrappedAuthorizedGetResponse(params, getServiceUrl(serviceDomain), subPath + apiMethod,	serviceDomain);
	}


	public ResponseEntity<JsonNode> anyPost(@RequestBody String requestBody, HttpServletRequest servletRequest)
			throws DamServiceException {
		
		String requestUri = servletRequest.getRequestURI();
		String[] pathParts = getPathParts(servletRequest);
		ServiceDomain serviceDomain = getServiceDomain(pathParts);
		String subPath = getSubPath(pathParts);
		String apiMethod = getApiMethod(pathParts, requestUri);

		return consumer.retrieveWrappedAuthorizedPostResponse(requestBody, getServiceUrl(serviceDomain), subPath + apiMethod,
				serviceDomain);
	}
	
	private String getApiMethod(String[] pathParts, String requestUri) throws DamServiceException {
		try {
			return pathParts[pathParts.length -1];

		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}
	
	private String getSubPath(String[] pathParts) throws DamServiceException {
		String subPath = "/";
		try {
			int index = 2;
			while (pathParts.length > index+1) {
				subPath+= pathParts[index] + "/";
			}
		}catch (Exception e) {
			// nothing to do
		}
		return subPath;
	}
	
	private ServiceDomain getServiceDomain(String[]pathParts) throws DamServiceException {
		String domain = pathParts[1];				
		
		try {
			return ServiceDomain.valueOf(domain.toUpperCase());
		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad", "Domäne existiert nicht: " + domain.toUpperCase());
		}
	}
	
	private String[] getPathParts(HttpServletRequest servletRequest) throws DamServiceException {
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

}
