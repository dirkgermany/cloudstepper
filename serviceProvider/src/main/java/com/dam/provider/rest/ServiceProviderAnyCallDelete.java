package com.dam.provider.rest;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Client;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCallDelete extends MasterController {
//	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallDelete.class);

	@Autowired
	ConfigProperties config;

	@Autowired
	Client client;

	@DeleteMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashDelete(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.DELETE);
	}

	@DeleteMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashDelete(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.DELETE);
	}

	@DeleteMapping("*")
	public ResponseEntity<JsonNode> singleSlashDelete(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.DELETE);
	}
}
