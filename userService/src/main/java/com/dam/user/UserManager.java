package com.dam.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.user.model.entity.User;

@Component
public class UserManager {

	@Autowired
	Configuration config;

	@Autowired
	UserStore userStore;

	private static Map<String, User> userMap;
	private static Long lastUpdate = new Long(0);

	public synchronized User getUser(String userName, String password) throws DamServiceException {
		if (isTimeForUpdate()) {
			userMap = new HashMap<>();
		}

		User user = userMap.get(userName + password);
		if (null == user) {
			user = userStore.getUser(userName, password);
		}
		if (null != user) {
			userMap.put(user.getUserName() + user.getPassword(), user);
		}
		return user;
	}

	public synchronized User getUser(User requestedUser) throws DamServiceException {
		return getUser(requestedUser.getUserName(), requestedUser.getPassword());
	}

//	private void updateUserStore() throws DamServiceException {
//		if (!isTimeForUpdate()) {
//			return;
//		}
//		userMap = new HashMap<>();
//		List<User> users = userStore.getUserList();
//		users.forEach(user -> userMap.put(user.getUserName() + user.getPassword(), user));
//	}

	private Boolean isTimeForUpdate() {
		if (System.currentTimeMillis() - lastUpdate > config.getUserStoreUpdateInterval()) {
			lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
