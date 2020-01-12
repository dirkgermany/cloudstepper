package com.dam.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.user.model.UserModel;
import com.dam.user.model.entity.User;
import com.dam.user.rest.message.WriteRequest;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class UserStore {

	@Autowired
	private UserModel userModel;

	public long count() {
		return userModel.count();
	}

	public User getUser(String userName, String password) {
		User user = userModel.getUser(userName, password);
		if (null != user) {
			user.setPassword("******");
		}
		return user;
	}

	public List<User> getUserList() {
		List<User> users = new ArrayList<User>();
		userModel.findAll().forEach((User user) -> {
			user.setPassword("******");
			users.add(user);
		});
		return users;
	}

	public User getUserSafe(Map<String, String> requestParams, Map<String, String> headers) throws DamServiceException {
		String requestorUserIdAsString = requestParams.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}
		String rights = requestParams.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		String userIdAsString = requestParams.get("userId");
		String userName = requestParams.get("userName");

		User user = null;
		if (null != userIdAsString) {
			Long userId = extractLong(userIdAsString);
			user = getUser(userId);
		} else if (null != userName) {
			user = getUser(userName);
		}

		if (null != user) {
			PermissionCheck.isReadPermissionSet(requestorUserId, user.getUserId(), rights);
		}
		return user;
	}

	/**
	 * For secure user reading.
	 * 
	 * @param userId
	 * @param userName
	 * @return
	 */
	public User getUser(Long userId, String userName) {
		User user = getUser(userId);
		if (null != user && user.getUserName().equals(userName)) {
			return user;
		}
		return null;
	}

	public User getUser(User user) {
		return getUser(user.getUserName(), user.getPassword());
	}

	public User getUser(Long userId) {
		Optional<User> optionalUser = userModel.findById(userId);
		if (null != optionalUser && optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setPassword("******");
			return user;
		}
		return null;
	}

	public User getUser(String userName) {
		User user = userModel.findByUserName(userName);
		if (null != user) {
			user.setPassword("******");
		}
		return user;
	}

	/*
	 * Only Super User may create users with admin rights
	 */
	public User createUserSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws DamServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getUser(), User.class, "");

		User user = requestBody.getUser();

		// Diese Prüfung muss ähnlich in die Permissions rein
		if (user.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new DamServiceException(401L, "User not created",
						"Creating user with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, user.getUserId(), rights);

		return createUser(user.getUserName(), user.getPassword(), user.getGivenName(), user.getLastName(),
				user.getRole());
	}

	/*
	 * Only Super User may create users with admin rights
	 */
	public User updateUserSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws DamServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getUser(), User.class, "");

		User userUpdateData = requestBody.getUser();
		if (null == userUpdateData.getUserId() && null == userUpdateData.getUserName()) {
			throw new DamServiceException(401L, "Cannot update user", "User name and userId are empty");
		}

		// Diese Prüfung muss ähnlich in die Permissions rein
		if (userUpdateData.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new DamServiceException(401L, "User not created",
						"Creating user with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, userUpdateData.getUserId(), rights);

		return updateUser(userUpdateData);
	}

	/**
	 * Creates a user. Checks if the userName still exists (must be distinct)
	 * 
	 * @param userName
	 * @param password
	 * @param givenName
	 * @param lastName
	 * @return
	 */
	public User createUser(String userName, String password, String givenName, String lastName, String role)
			throws DamServiceException {

		if (null == userName || null == password || null == givenName || null == lastName || null == role) {
			return null;
		}

		// does the userName already exists?
		if (null != getUser(userName)) {
			throw new DamServiceException(409L, "User not created", "User already exists, try update method");
		}

		try {
			User user = userModel.save(new User(userName, password, givenName, lastName, role));
			if (null != user) {
				user.setPassword("******");
			}
			return user;
		} catch (Exception ex) {
			return null;
		}
	}

	private User updateUser(User userUpdateData) throws DamServiceException {
		User storedUser = null;
		if (null != userUpdateData.getUserId()) {
			storedUser = getUser(userUpdateData.getUserId());
		} else if (null != userUpdateData.getUserName()) {
			storedUser = getUser(userUpdateData.getUserName());
		}

		if (null == storedUser) {
			throw new DamServiceException(401L, "Cannot update user", "User not found by userId and userName");
		}

		storedUser.updateFrom(userUpdateData);
		storedUser = userModel.save(storedUser);
		if (null == storedUser) {
			throw new DamServiceException(401L, "User not updated", "Unknown reason but user was not saved");
		}
		storedUser.setPassword("******");
		return storedUser;
	}

	public HttpStatus dropUser(Long requestorUserId, User userToDrop) {
		if (null == requestorUserId || null == userToDrop) {
			return HttpStatus.BAD_REQUEST;
		}

		User user = null;
		if (null != userToDrop.getUserId()) {
			user = getUser(userToDrop.getUserId());
		} else if (null != userToDrop.getUserName()) {
			user = getUser(userToDrop.getUserName());
		}

		if (null == user) {
			return HttpStatus.NOT_FOUND;
		}
		if (user.getUserId().longValue() != requestorUserId.longValue()) {
			return HttpStatus.METHOD_NOT_ALLOWED;
		}

		return dropUser(user);
	}

	private HttpStatus dropUser(User user) {
		userModel.deleteById(user.getUserId());
		User deletedUser = getUser(user.getUserId());
		if (null == deletedUser) {
			return HttpStatus.NO_CONTENT;
		}

		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public static Long userIdToLong(String formattedUserId) {
		return Long.valueOf(formattedUserId);
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
