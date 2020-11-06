package com.dam.provider.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.provider.rest.consumer.Client;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins = "*")
@RestController
public class ServiceProviderAnyCallGet extends MasterController {
//	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderAnyCallGet.class);

	@Autowired
	Client client;

	@Autowired
	ServiceProviderPing serviceProviderPing;

	@GetMapping("/*/*")
	public ResponseEntity<JsonNode> doubleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody (required = false) JsonNode requestBody) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.GET);
	}

	@GetMapping("/*/*/*")
	public ResponseEntity<JsonNode> tripleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody (required = false) JsonNode requestBody) {
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.GET);
	}

	@GetMapping("*")
	public ResponseEntity<JsonNode> singleSlashGet(@RequestParam Map<String, String> params,
			HttpServletRequest servletRequest, @RequestHeader Map<String, String> headers, @RequestBody (required = false) JsonNode requestBody) {
		System.out.println("@GetMapping: " + requestBody + "\n - " + servletRequest + "\n - " + params + "\n - " + headers);
		return anyHttpRequest(requestBody, servletRequest, params, headers, HttpMethod.GET);
	}

}
