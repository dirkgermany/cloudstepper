package com.dam.user.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.user.PermissionCheck;
import com.dam.user.UserStore;
import com.dam.user.model.entity.User;
import com.dam.user.model.entity.UserMessageContainer;
import com.dam.user.rest.message.CreateRequest;
import com.dam.user.rest.message.CreateResponse;
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
		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found", "User or combination of user+password is unknown"), HttpStatus.OK);
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
		Map<String, String> decodedMap = decodeHttpMap(params);

		String requestorUserIdAsString = decodedMap.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}
		String rights = decodedMap.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String userIdAsString = decodedMap.get("userId");
		String userName = decodedMap.get("userName");

		User user = null;
		if (null != userIdAsString) {
			Long userId = extractLong(userIdAsString);
			user = userStore.getUser(userId);
		} else if (null != userName) {
			user = userStore.getUser(userName);
		}

		if (null != user) {
			PermissionCheck.isReadPermissionSet(requestorUserId, user.getUserId(), rights);
			return new ResponseEntity<RestResponse>(new UserResponse(user), HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found", "Check userId: " + userIdAsString + ", userName: " + userName), HttpStatus.NOT_FOUND);
	}

	@PostMapping("/createUser")
	public ResponseEntity<RestResponse> createUser(@RequestBody CreateRequest createRequest) {
		User user = userStore.createUser(createRequest.getUser());

		if (null != user) {
			return new ResponseEntity<RestResponse>(new CreateResponse(user), HttpStatus.CREATED);
		}

		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_MODIFIED, "User not created", "User still exists, data invalid or not complete"), HttpStatus.NOT_MODIFIED);
	}

	@DeleteMapping("/dropUser")
	public ResponseEntity<RestResponse> dropUser(@RequestBody DropRequest dropRequest) {
		Long userId = new Long(dropRequest.getRequestorUserId());
		HttpStatus result = userStore.dropUser(userId, dropRequest.getUser());

		if (result == HttpStatus.NO_CONTENT) {
			return new ResponseEntity<RestResponse>(new DropResponse(result), HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User not deleted", "User not found"), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@PostMapping("/updateUser")
	public RestResponse updateUser(@RequestBody UpdateRequest updateRequest) {
		if (null == updateRequest.getRequestorUserId()) {
			return null;
		}
		Long userId = new Long(updateRequest.getRequestorUserId());
		User user = userStore.updateUser(userId, updateRequest.getUserStored(), updateRequest.getUserUpdate());
		if (null != user) {
			UserMessageContainer userMessageContainer = new UserMessageContainer(user.getUserName(),
					user.getGivenName(), user.getLastName());
			return new UpdateResponse(userMessageContainer);
		}

		return null;
//		return new RestResponse(new Long(500), "User NOT updated",
//				"User not found, no valid data or entity not ready for update");

	}

	private Long extractLong(String longString) throws DamServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}

}