package com.dam.provider.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.JsonHelper;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCall extends MasterController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCall.class);

	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	@Autowired
	ServiceProviderPing serviceProviderPing;

	@GetMapping("/*/*")
	public ResponseEntity<String> doubleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader(name = "tokenId", required = false) String tokenId) {

		return anyGet(params, servletRequest, tokenId);

	}

	@PostMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest,
			@RequestHeader(name = "tokenId", required = false) String tokenId) {

		return anyPost(requestBody, servletRequest, tokenId);

	}

	@GetMapping("/*/*/*")
	public ResponseEntity<String> tripleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader(name = "tokenId", required = false) String tokenId) {

		return anyGet(params, servletRequest, tokenId);

	}

	@PostMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest,
			@RequestHeader(name = "tokenId", required = false) String tokenId) {

		return anyPost(requestBody, servletRequest, tokenId);

	}

	@PostMapping("*")
	public ResponseEntity<JsonNode> singleSlashPost(@RequestBody String requestBody, HttpServletRequest servletRequest,
			@RequestHeader(name = "tokenId", required = false) String tokenId) {

		return anyPost(requestBody, servletRequest, tokenId);

	}

	@GetMapping("*")
	public ResponseEntity<String> singleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader(name = "tokenId", required = false) String tokenId) {
		return anyGet(params, servletRequest, tokenId);
	}

	private ResponseEntity<String> anyGet(@RequestParam Map<String, String> params, HttpServletRequest servletRequest,
			String tokenId) {

		String requestUri;
		String[] pathParts;
		ServiceDomain serviceDomain;
		String subPath;
		String apiMethod;
		Map<String, String> decodedParams = new HashMap<>();

		try {
			requestUri = servletRequest.getRequestURI();
			pathParts = getPathParts(servletRequest);
			serviceDomain = getServiceDomain(pathParts);
			subPath = getSubPath(pathParts);
			apiMethod = getApiMethod(pathParts, requestUri);

			Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				decodedParams.put(decode(entry.getKey()), decode(entry.getValue()));
			}

		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

		try {
			return consumer.retrieveWrappedAuthorizedGetResponse(decodedParams, getServiceUrl(serviceDomain),
					subPath + apiMethod, serviceDomain, tokenId);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

	public ResponseEntity<JsonNode> anyPost(@RequestBody String requestBody, HttpServletRequest servletRequest,
			String tokenId) {

		String requestUri;
		String[] pathParts;
		ServiceDomain serviceDomain;
		String subPath;
		String apiMethod;

		try {
			requestUri = servletRequest.getRequestURI();
			pathParts = getPathParts(servletRequest);
			serviceDomain = getServiceDomain(pathParts);
			subPath = getSubPath(pathParts);
			apiMethod = getApiMethod(pathParts, requestUri);

			if (null == tokenId || tokenId.isEmpty()) {
				tokenId = extractTokenFromRequest(requestBody);
			}

		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

		try {
			return consumer.retrieveWrappedAuthorizedPostResponse(requestBody, getServiceUrl(serviceDomain),
					subPath + apiMethod, serviceDomain, tokenId);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

	private String extractTokenFromRequest(String requestBody) throws DamServiceException {
		if (null != requestBody && !requestBody.isEmpty()) {
			return new JsonHelper().extractStringFromRequest(requestBody, "tokenId");
		}
		throw new DamServiceException(404L, "Missing tokenId", "Request not processed while tokenId was not received");
	}

	private String getApiMethod(String[] pathParts, String requestUri) throws DamServiceException {
		try {
			return decode(pathParts[pathParts.length - 1]);

		} catch (Exception e) {
			throw new DamServiceException(500L, "Ungültiger Pfad",
					"Der aufgerufene Pfad existiert nicht: " + requestUri);
		}
	}

	private String getSubPath(String[] pathParts) throws DamServiceException {
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

	private ServiceDomain getServiceDomain(String[] pathParts) throws DamServiceException {
		String domain = decode(pathParts[1]);

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
