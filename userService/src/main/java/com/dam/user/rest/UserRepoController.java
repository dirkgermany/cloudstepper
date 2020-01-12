package com.dam.user.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.dam.exception.DamServiceException;
import com.dam.user.PermissionCheck;
import com.dam.user.UserStore;
import com.dam.user.model.entity.User;
import com.dam.user.model.entity.UserMessageContainer;
import com.dam.user.rest.message.WriteRequest;
import com.dam.user.rest.message.WriteResponse;
import com.dam.user.rest.message.DropRequest;
import com.dam.user.rest.message.DropResponse;
import com.dam.user.rest.message.ErrorResponse;
import com.dam.user.rest.message.RestResponse;
import com.dam.user.rest.message.UpdateRequest;
import com.dam.user.rest.message.UpdateResponse;
import com.dam.user.rest.message.UserRequest;
import com.dam.user.rest.message.UserResponse;

@CrossOrigin
@RestController
public class UserRepoController extends MasterController {
	@Autowired
	private UserStore userStore;

	/**
	 * Retrieves a user by userName and password for internal usage
	 * 
	 * @param userRequest
	 * @return
	 */
	@PostMapping("/checkUser")
	public ResponseEntity<RestResponse> checkUser(@RequestBody UserRequest userRequest) {
		if (null == userRequest.getUser()) {
			return null;
		}
		User user = userStore.getUser(userRequest.getUser());

		if (null != user && null == userRequest.getRequestorUserId()) {
			return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
		}

		if (null != user && new Long(userRequest.getRequestorUserId()).longValue() == user.getUserId().longValue()) {
			return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found",
				"User or combination of user+password is unknown"), HttpStatus.OK);
	}

	/**
	 * Retrieves a user by userName and userId Calling user must be logged in
	 * 
	 * @param userRequest
	 * @return
	 */
	@GetMapping("/getUser")
	public ResponseEntity<RestResponse> getUserByGet(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws DamServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		try {
			User user = userStore.getUserSafe(decodedParams, headers);
			if (null != user) {
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User could not be read", e.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(
				new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User could not be read", "User not found"),
				HttpStatus.OK);
	}

	@PostMapping("/createUser")
	public ResponseEntity<RestResponse> createUser(@RequestBody WriteRequest requestBody, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}
		
		try {
			User user = userStore.createUserSafe(requestBody, decodedParams, headers);
			if (null != user) {
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			}
		} catch (DamServiceException e) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()), "User could not be created", e.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_MODIFIED, "User not created",
				"User still exists, data invalid or not complete"), HttpStatus.NOT_MODIFIED);
	}
	
	@PutMapping("/updateUser")
	public ResponseEntity<RestResponse> updateUser(@RequestBody WriteRequest requestBody, @RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}
		
		try {
			User user = userStore.updateUserSafe(requestBody, decodedParams, headers);
			if (null != user) {
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			}
		} catch (DamServiceException e) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()), "User could not be created", e.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_MODIFIED, "User not created",
				"User still exists, data invalid or not complete"), HttpStatus.NOT_MODIFIED);
	}

	@DeleteMapping("/dropUser")
	public ResponseEntity<RestResponse> dropUser(@RequestBody DropRequest dropRequest) {
		Long userId = new Long(dropRequest.getRequestorUserId());
		HttpStatus result = userStore.dropUser(userId, dropRequest.getUser());

		if (result == HttpStatus.NO_CONTENT) {
			return new ResponseEntity<RestResponse>(new DropResponse(result), HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<RestResponse>(
				new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User not deleted", "User not found"),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}
}