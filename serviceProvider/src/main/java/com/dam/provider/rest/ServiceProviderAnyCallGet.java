package com.dam.provider.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dam.exception.AuthorizationServiceException;
import com.dam.exception.DamServiceException;
import com.dam.provider.rest.consumer.Consumer;
import com.dam.provider.types.ServiceDomain;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCallGet extends MasterController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallGet.class);

	@Autowired
	Consumer consumer;

	@Autowired
	ServiceProviderPing serviceProviderPing;

	@GetMapping("/*/*")
	public ResponseEntity<String> doubleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody String requestBody) {

		return anyGet(params, servletRequest, headers, requestBody);
	}

	@GetMapping("/*/*/*")
	public ResponseEntity<String> tripleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody String requestBody) {

		return anyGet(params, servletRequest, headers, requestBody);
	}

	@GetMapping("*")
	public ResponseEntity<String> singleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody String requestBody) {
		return anyGet(params, servletRequest, headers, requestBody);
	}

	private ResponseEntity<String> anyGet(@RequestParam Map<String, String> params, HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, String requestBody) {

		String requestUri;
		String[] pathParts;
		ServiceDomain serviceDomain;
		String subPath;
		String apiMethod;
		Map<String, String> decodedParams = null;

		try {
			decodedParams = decodeHttpMap(params);
			requestUri = servletRequest.getRequestURI();
			pathParts = getPathParts(servletRequest);
			serviceDomain = getServiceDomain(pathParts);
			subPath = getSubPath(pathParts);
			apiMethod = getApiMethod(pathParts, requestUri);

		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

		try {
			return consumer.retrieveWrappedAuthorizedGetResponse(decodedParams, headers, getServiceUrl(serviceDomain),
					subPath + apiMethod, serviceDomain);
		} catch (AuthorizationServiceException ase) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"User not logged in, invalid token or not enough rights for action.");
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getDescription() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

}
