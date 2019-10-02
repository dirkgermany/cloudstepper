package com.dam.depot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dam.exception.DamServiceException;

public class RequestBlocker {
	
	private static Map<Long, Boolean> userLock = new ConcurrentHashMap<>();
	
	public static void lockUser(Long requestorUserId) throws DamServiceException {
		checkUserLock(requestorUserId);
		lock(requestorUserId);
	}
	
	public static void unlockUser(Long requestorUserId) {
		unlock(requestorUserId);
	}
	
	private static void checkUserLock(Long requestorUserId) throws DamServiceException {
		if (isUserLocked(requestorUserId)) {
			throw new DamServiceException(403L, "Depot locked for user", "Another request is in progress for user: " + requestorUserId + "; please try in some seconds again");
		}
	}
	
	private static Boolean isUserLocked(Long requestorUserId) {
		Boolean isLocked = userLock.get(requestorUserId);
		return isLocked == null ? Boolean.FALSE : isLocked;
	}
	
	private static void lock(Long requestorUserId) {
		userLock.put(requestorUserId, Boolean.TRUE);
	}

	private static void unlock(Long requestorUserId) {
		userLock.put(requestorUserId, Boolean.FALSE);
	}

}
