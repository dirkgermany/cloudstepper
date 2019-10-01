package com.dam.depot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dam.exception.DamServiceException;

public class RequestBlocker {
	
	private static Map<Long, Boolean> userLock = new ConcurrentHashMap<>();
	
	public static void lockUser(Long requestorUserId) throws DamServiceException {
		checkUserLock(requestorUserId);
		
		userLock.put(requestorUserId, Boolean.TRUE);
	}
	
	public static void unlockUser(Long requestorUserId) {
		userLock.put(requestorUserId, Boolean.FALSE);
	}
	
	public static void checkUserLock(Long requestorUserId) throws DamServiceException {
		if (isUserLocked(requestorUserId)) {
			throw new DamServiceException(403L, "Depot locked for user", "Another request is in progress for user: " + requestorUserId + "; please try in some seconds again");
		}
	}
	
	public static Boolean isUserLocked(Long requestorUserId) {
		Boolean isLocked = userLock.get(requestorUserId);
		return isLocked == null ? Boolean.FALSE : isLocked;
	}

}
