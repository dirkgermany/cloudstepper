package com.dam.authentication.rest;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.authentication.ConfigProperties;
import com.dam.authentication.PermissionManager;
import com.dam.authentication.TokenStore;
import com.dam.authentication.model.PermissionStore;
import com.dam.authentication.rest.message.PermissionCreateRequest;
import com.dam.authentication.rest.message.RestResponse;
import com.dam.exception.DamServiceException;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin
@RestController
@ComponentScan
public class PermissionController extends MasterController {

	@Autowired
	TokenStore tokenStore;

	@Autowired
	PermissionStore permissionStore;

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	UserServiceConsumer userServiceConsumer;

	@Autowired
	ConfigProperties config;

	@GetMapping("/getPermissionsByDomain")
	public ResponseEntity<RestResponse> getPermissionsByDomain(@RequestParam Map<String, String> requestParams, @RequestHeader Map<String, String> headers) throws DamServiceException {
		Map<String, String> decodedRequestParams = decodeHttpMap(requestParams);
		
		return permissionStore.getPermissionsByDomainSafe(decodedRequestParams, headers);
	}
	
//	@PostMapping("/createPermission")
//	public ResponseEntity<RestResponse> createPermission(@RequestBody PermissionCreateRequest requestBody, @RequestParam Map<String, String> requestParams, @RequestHeader Map<String, String> headers) throws DamServiceException {
//		Map<String, String> decodedRequestParams = decodeHttpMap(requestParams);
//
//		return permissionStore.createPermissionSafe(requestBody, decodedRequestParams, headers);
//	}
}