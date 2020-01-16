package com.dam.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.exception.CsServiceException;
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

	public User getUserSafe(Map<String, String> requestParams, Map<String, String> headers) throws CsServiceException {
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

		if (null == user) {
			throw new CsServiceException(404L, "User not found", "Unknown userId or userName");
		}
		PermissionCheck.isReadPermissionSet(requestorUserId, user.getUserId(), rights);
		return user;
	}

	public List<User> listUsersSafe(Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		ArrayList<User> users = new ArrayList<>();
		userModel.findAll().forEach(user -> {
			user.setPassword("******");
			users.add(user);
		});
		return users;
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
	public User createUserSafe(WriteRequest requestBody, Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getUser(), User.class, "");

		// Diese Prüfung muss ähnlich in die Permissions rein
		if (requestBody.getUser().getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "User not created",
						"Creating user with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, requestBody.getUser().getUserId(), rights);

		return createUser(requestBody.getUser());
	}

	/*
	 * Only Super User may create users with admin rights
	 */
	public User updateUserSafe(WriteRequest requestBody, Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getUser(), User.class, "");

		User userUpdateData = requestBody.getUser();
		if (null == userUpdateData.getUserId() && null == userUpdateData.getUserName()) {
			throw new CsServiceException(401L, "Cannot update user", "User name and userId are empty");
		}

		// Diese Prüfung muss ähnlich in die Permissions rein
		// UND in die Verwaltung der Domänen -> User-, Permission-,
		// Authentication-Domänen dürfen nicht verändert werden (evtl. gar nicht gelesen
		// werden)
		if (userUpdateData.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "User not created",
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
	private User createUser(User userContainer) throws CsServiceException {

		if (null == userContainer.getUserName() || null == userContainer.getPassword() || null == userContainer.getGivenName() || null == userContainer.getLastName() || null == userContainer.getRole()) {
			return null;
		}

		// does the userName already exists?
		if (null != getUser(userContainer.getUserName())) {
			throw new CsServiceException(409L, "User not created", "User already exists, try update method");
		}

		try {
			User user = userModel.save(userContainer);
			if (null != user) {
				user.setPassword("******");
			}
			return user;
		} catch (Exception ex) {
			return null;
		}
	}

	private User updateUser(User userUpdateData) throws CsServiceException {
		User storedUser = null;
		if (null != userUpdateData.getUserId()) {
			storedUser = getUser(userUpdateData.getUserId());
		} else if (null != userUpdateData.getUserName()) {
			storedUser = getUser(userUpdateData.getUserName());
		}

		if (null == storedUser) {
			throw new CsServiceException(401L, "Cannot update user", "User not found by userId or userName");
		}

		storedUser.updateFrom(userUpdateData);
		storedUser = userModel.save(storedUser);
		if (null == storedUser) {
			throw new CsServiceException(401L, "User not updated", "Unknown reason but user was not saved");
		}
		storedUser.setPassword("******");
		return storedUser;
	}

	public void dropUserSafe(Map<String, String> requestParams, Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);

		User storedUser = null;
		if (null != requestParams.get("userId")) {
			Long userId = extractLong(requestParams.get("userId"));
			storedUser = getUser(userId);
		} else if (null != requestParams.get("userName")) {
			storedUser = getUser(requestParams.get("userName"));
		}

		if (null == storedUser) {
			throw new CsServiceException(500L, "Cannot drop user", "User not found by userId or userName");
		}

		if (storedUser.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "User not deleted",
						"Deleting user with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isDeletePermissionSet(requestorUserId, storedUser.getUserId(), rights);
		dropUser(storedUser.getUserId());
	}

	private void dropUser(long userId) throws CsServiceException {
		userModel.deleteById(userId);
		User deletedUser = getUser(userId);
		if (null != deletedUser) {
			throw new CsServiceException(404L, "User not deleted", "Unknown reason but user was not deleted");
		}
	}

	private Long extractLong(String longString) throws CsServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new CsServiceException(404L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}
}
