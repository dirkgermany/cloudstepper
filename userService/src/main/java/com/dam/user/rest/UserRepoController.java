package com.dam.user.rest;

import java.util.List;
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

import com.dam.exception.CsServiceException;
import com.dam.user.UserStore;
import com.dam.user.model.entity.User;
import com.dam.user.rest.message.WriteRequest;
import com.dam.user.rest.message.DropResponse;
import com.dam.user.rest.message.ErrorResponse;
import com.dam.user.rest.message.ListUserResponse;
import com.dam.user.rest.message.RestResponse;
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

		if (null != user && Long.parseLong(userRequest.getRequestorUserId()) == user.getUserId().longValue()) {
			return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found",
				"User or combination of user+password is unknown"), HttpStatus.OK);
	}
	
	@GetMapping("/listUsers")
	public ResponseEntity<RestResponse> listUsers(@RequestHeader Map<String, String> headers) throws CsServiceException {

		try {
			List<User> users = userStore.listUsersSafe(headers);
			if (null != users) {
				return new ResponseEntity<RestResponse>(new ListUserResponse(users), HttpStatus.OK);
			}
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "User list could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(
				new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User list could not be read", "No user found"),
				HttpStatus.OK);
	}



	/**
	 * Retrieves a user by userName and userId Calling user must be logged in
	 * 
	 * @param userRequest
	 * @return
	 */
	@GetMapping("/getUser")
	public ResponseEntity<RestResponse> getUser(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws CsServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			User user = userStore.getUserSafe(decodedParams, headers);
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "User could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
	}

	@PostMapping("/createUser")
	public ResponseEntity<RestResponse> createUser(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			User user = userStore.createUserSafe(requestBody, decodedParams, headers);
			if (null != user) {
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"User could not be created", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "User not created",
				"User still exists, data invalid or not complete"), HttpStatus.OK);
	}

	@PutMapping("/updateUser")
	public ResponseEntity<RestResponse> updateUser(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			User user = userStore.updateUserSafe(requestBody, decodedParams, headers);
			if (null != user) {
				return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"User could not be updated", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "User not updated",
				"User does not exist, data invalid or not complete"), HttpStatus.OK);
	}

	@DeleteMapping("/dropUser")
	public ResponseEntity<RestResponse> dropUser(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		String userIdAsString = decodedParams.get("userId");
		if (null == userIdAsString) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User not deleted", "User id is empty"),
					HttpStatus.OK);
		}

		try {
			userStore.dropUserSafe(decodedParams, headers);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "User not deleted", dse.getMessage()),
					HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new DropResponse(HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
	}
}