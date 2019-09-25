package com.dam.depot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dam.exception.DamServiceException;

public class RequestBlocker {
	
	private static Map<Long, Boolean> userLock = new ConcurrentHashMap<>();
	
	public static void lockUser(Long userId) throws DamServiceException {
		checkUserLock(userId);
		
		userLock.put(userId, Boolean.TRUE);
	}
	
	public static void unlockUser(Long userId) {
		userLock.put(userId, Boolean.FALSE);
	}
	
	public static void checkUserLock(Long userId) throws DamServiceException {
		if (isUserLocked(userId)) {
			throw new DamServiceException(403L, "Depot locked for user", "Another request is in progress for user: " + userId + "; please try in some seconds again");
		}
	}
	
	public static Boolean isUserLocked(Long userId) {
		Boolean isLocked = userLock.get(userId);
		return isLocked == null ? Boolean.FALSE : isLocked;
	}

}
