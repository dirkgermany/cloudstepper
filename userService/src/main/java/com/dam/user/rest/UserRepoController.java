package com.dam.user.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	 * Retrieves a user by userName and password For internal usage
	 * 
	 * @param userRequest
	 * @return
	 */
	@PostMapping("/checkUser")
	public RestResponse checkUserResponse(@RequestBody UserRequest userRequest) {
		if (null == userRequest.getUser()) {
			return null;
		}
		User user = userStore.getUser(userRequest.getUser());

		if (null != user && null == userRequest.getRequestorUserId()) {
			return new UserResponse(user);
		}

		if (null != user && new Long(userRequest.getRequestorUserId()).longValue() == user.getUserId().longValue()) {
			return new UserResponse(user);
		}

		return new RestResponse(new Long(500), "User Unknown", "User not found or invalid request");
	}

	/**
	 * Retrieves a user by userName and userId Calling user must be logged in
	 * 
	 * @param userRequest
	 * @return
	 */
	@GetMapping("/getUser")
	public RestResponse getUserByGet(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws DamServiceException {
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
			return new UserResponse(user);
		}

		return new RestResponse(new Long(500), "User Unknown", "User not found or invalid request");
	}

	@PostMapping("/createUser")
	public RestResponse createUser(@RequestBody CreateRequest createRequest) {
		User user = userStore.createUser(createRequest.getUser());

		if (null != user) {
			return new CreateResponse(user);
		}

		return new RestResponse(new Long(500), "User NOT created", "User still exists, data invalid or not complete");

	}

	@PostMapping("/dropUser")
	public RestResponse dropUser(@RequestBody DropRequest dropRequest) {
		Long userId = new Long(dropRequest.getRequestorUserId());
		Long result = userStore.dropUser(userId, dropRequest.getUser());

		if (result.longValue() == new Long(200).longValue()) {
			return new DropResponse(result);
		}

		return new RestResponse(result, "User NOT dropped", "User not found, or entity not ready for deletion");

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

		return new RestResponse(new Long(500), "User NOT updated",
				"User not found, no valid data or entity not ready for update");

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