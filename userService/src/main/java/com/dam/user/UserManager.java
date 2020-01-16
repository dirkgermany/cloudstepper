package com.dam.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.CsServiceException;
import com.dam.user.model.entity.User;

@Component
public class UserManager {

	@Autowired
	Configuration config;

	@Autowired
	UserStore userStore;

	private static Map<String, User> userMap;
	private static Long lastUpdate = new Long(0);

	public synchronized User getUser(String userName, String password) throws CsServiceException {
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

	public synchronized User getUser(User requestedUser) throws CsServiceException {
		return getUser(requestedUser.getUserName(), requestedUser.getPassword());
	}

	private Boolean isTimeForUpdate() {
		if (System.currentTimeMillis() - lastUpdate > config.getUserStoreUpdateInterval()) {
			lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
