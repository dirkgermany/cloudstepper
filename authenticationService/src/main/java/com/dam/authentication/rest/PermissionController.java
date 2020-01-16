package com.dam.authentication.rest;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.authentication.ConfigProperties;
import com.dam.authentication.PermissionManager;
import com.dam.authentication.TokenStore;
import com.dam.authentication.model.PermissionStore;
import com.dam.authentication.model.entity.Permission;
import com.dam.authentication.rest.message.DropResponse;
import com.dam.authentication.rest.message.ListPermissionResponse;
import com.dam.authentication.rest.message.WriteRequest;
import com.dam.authentication.rest.message.PermissionResponse;
import com.dam.authentication.rest.message.PermissionsByDomainResponse;
import com.dam.authentication.rest.message.RestResponse;
import com.dam.exception.CsServiceException;

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
	public ResponseEntity<RestResponse> getPermissionsByDomain(@RequestParam Map<String, String> requestParams,
			@RequestHeader Map<String, String> headers) throws CsServiceException {
		Map<String, String> decodedRequestParams = null;
		try {
			decodedRequestParams = decodeHttpMap(requestParams);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			List<Permission> permission = permissionStore.getPermissionsByDomainSafe(decodedRequestParams, headers);
			return new ResponseEntity<RestResponse>(new PermissionsByDomainResponse(permission), HttpStatus.OK);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()),
					"Permissions could not be read", dse.getMessage()), HttpStatus.OK);
		}
	}

	@GetMapping("/listPermissions")
	public ResponseEntity<RestResponse> listPermissions(@RequestHeader Map<String, String> headers)
			throws CsServiceException {

		try {
			List<Permission> permissions = permissionStore.listPermissionsSafe(headers);
			if (null != permissions) {
				return new ResponseEntity<RestResponse>(new ListPermissionResponse(permissions), HttpStatus.OK);
			}
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()),
					"User list could not be read", dse.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR,
				"User list could not be read", "No permissions found"), HttpStatus.OK);
	}

	@GetMapping("/getPermission")
	public ResponseEntity<RestResponse> getPermission(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws CsServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			Permission permission = permissionStore.getPermissionSafe(decodedParams, headers);
			return new ResponseEntity<RestResponse>(new PermissionResponse(permission), HttpStatus.OK);

		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()),
					"Permission could not be read", dse.getMessage()), HttpStatus.OK);
		}
	}
	
	@PostMapping("/createPermission")
	public ResponseEntity<RestResponse> createPermission(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			Permission permission = permissionStore.createPermissionSafe(requestBody, decodedParams, headers);
			if (null != permission) {
				return new ResponseEntity<RestResponse>(new PermissionResponse(permission), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"User could not be created", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Permission not created",
				"Permission still exists, data invalid or not complete"), HttpStatus.OK);
	}

	@PutMapping("/updatePermission")
	public ResponseEntity<RestResponse> updatePermission(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			Permission permission = permissionStore.updatePermissionSafe(requestBody, decodedParams, headers);
			if (null != permission) {
				return new ResponseEntity<RestResponse>(new PermissionResponse(permission), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Permission could not be updated", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Permission not updated",
				"Permission does not exist, data invalid or not complete"), HttpStatus.OK);
	}
	
	@DeleteMapping("/dropPermission")
	public ResponseEntity<RestResponse> dropPermission(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		String permissionIdAsString = decodedParams.get("permissionId");
		if (null == permissionIdAsString) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Permission not deleted", "Permission not found"),
					HttpStatus.OK);
		}

		try {
			permissionStore.dropPermissionSafe(decodedParams, headers);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "User not deleted", dse.getMessage()),
					HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new DropResponse(HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
	}
}