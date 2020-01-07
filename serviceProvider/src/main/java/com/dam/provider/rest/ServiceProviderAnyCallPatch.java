package com.dam.provider.rest;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.provider.ConfigProperties;
import com.dam.provider.rest.consumer.Consumer;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCallPatch extends MasterController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallPatch.class);

	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	@PatchMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.PATCH);
	}

	@PatchMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.PATCH);
	}

	@PatchMapping("*")
	public ResponseEntity<JsonNode> singleSlashPatch(@RequestBody (required = false) JsonNode requestBody, HttpServletRequest servletRequest, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.PATCH);
	}
}
