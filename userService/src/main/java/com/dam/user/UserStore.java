package com.dam.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.user.model.UserModel;
import com.dam.user.model.entity.User;
import com.dam.user.types.Role;

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

	/* Only for internal usage! Nothing is checked */
	private User getUser(String userName) {
		return userModel.findByUserName(userName);
	}

	public User createUser(User user) {
		return createUser(user.getUserName(), user.getPassword(), user.getGivenName(), user.getLastName(), user.getRole());
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
	public User createUser(String userName, String password, String givenName, String lastName, Role role) {

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

	private Long dropUser(String userName, String password) {
		User user = getUser(userName, password);
		return dropUser(user);
	}

	public Long dropUser(Long requestorUserId, User userToDrop) {
		if (null == requestorUserId || null == userToDrop) {
			return new Long(500);
		}

		User user = null;
		if (null != userToDrop.getUserId()) {
			user = getUser(userToDrop.getUserId());
		} else if (null != userToDrop.getUserName()) {
			user = getUser(userToDrop.getUserName());
		}

		if (null != user && user.getUserId().longValue() == requestorUserId.longValue()) {
			return dropUser(user);
		}

		return new Long(500);
	}

	private Long dropUser(Long userId) {
		User user = getUser(userId);
		return dropUser(user);
	}

	private Long dropUser(User user) {
		if (null != user) {
			userModel.deleteById(user.getUserId());
			User deletedUser = getUser(user.getUserId());
			if (null == deletedUser) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

	public static Long userIdToLong(String formattedUserId) {
		return Long.valueOf(formattedUserId);
	}

}
