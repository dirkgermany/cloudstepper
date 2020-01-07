package com.dam.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.dam.user.model.UserModel;
import com.dam.user.model.entity.User;

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
		return userModel.getUser(userName, password);
	}
	
	public List<User> getUserList() {
		List<User> users = new ArrayList<User>();
		userModel.findAll().forEach((User user) -> users.add(user));
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
			return optionalUser.get();
		}
		return null;
	}

	public User getUser(String userName) {
		return userModel.findByUserName(userName);
	}

	public User createUser(User user) {
		return createUser(user.getUserName(), user.getPassword(), user.getGivenName(), user.getLastName(),
				user.getRole());
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
	public User createUser(String userName, String password, String givenName, String lastName, String role) {

		if (null == userName || null == password || null == givenName || null == lastName || null == role) {
			return null;
		}

		// does the userName already exists?
		if (null != getUser(userName)) {
			return null;
		}

		try {
			return userModel.save(new User(userName, password, givenName, lastName, role));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public User updateUser(Long userId, User userStored, User userUpdate) {
		if (null == userId || null == userStored || null == userUpdate) {
			return null;
		}

		User user = null;
		if (null != userStored.getUserId()) {
			user = getUser(userStored.getUserId());
		} else {
			user = getUser(userStored.getUserName()); // , userStored.getPassword());
		}

		if (null != user && user.getUserId().longValue() == userId.longValue()) {
			user.updateEntity(userUpdate);
			return userModel.save(user);
		}

		return null;
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

}
