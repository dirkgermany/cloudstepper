package com.dam.stock;

import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;
import com.dam.stock.rest.message.RestRequest;

public class PermissionCheck {
	
	/**
	 * Checks preconditions
	 * @param request
	 * @param requestorUserId
	 * @param rights
	 * @throws DamServiceException
	 */
	public static void checkRequestedParams(RestRequest request, Long requestorUserId, String rights)
			throws DamServiceException {
		if (null == request) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Request is null.");
		}
		if (null == requestorUserId) {
			throw new DamServiceException(new Long(400), "Invalid Request",
					"requestorUserId is recommended but not set.");
		}
		if (null == rights || rights.isEmpty()) {
			throw new DamServiceException(new Long(400), "Invalid Request",
					"User rights are recommended but are null or empty.");
		}
	}
	
	/**
	 * Checks if a required object is existing
	 * @param entity
	 * @param theClass
	 * @throws DamServiceException
	 */
	@SuppressWarnings("rawtypes")
	public static void checkRequestedEntity(Object entity, Class theClass, String additionalInformation) 
			throws DamServiceException {
		if (null == entity) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Required object " + theClass.getSimpleName() + " is null." + "; " + additionalInformation);
		}
	}



	/**
	 * Checks if the WRITE permission is set. TRUE, when requestor and owner are the
	 * same and Owner Right for Writing is set. For Creation also the Owner Right
	 * should be prooved.
	 * 
	 * @param requestorUserId
	 * @param userId
	 * @param rights
	 * @return
	 */
	public static void isWritePermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (!isMarkerSet(requestorUserId, userId, rights, "W")) {
			throw new PermissionCheckException(new Long(403), "No Write Permission",
					"User has no permissions to write or update the dataset");
		}
	}

	/**
	 * Checks if any right to write is set
	 * 
	 * @param rights
	 * @return
	 */
	public static void isWritePermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (!rights.toUpperCase().contains("W")) {
			throw new PermissionCheckException(new Long(403), "No Write Permission in general",
					"User has no permissions to write or update a dataset in general");
		}
	}

	public static void isReadPermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (!isMarkerSet(requestorUserId, userId, rights, "R")) {
			throw new PermissionCheckException(new Long(403), "No Read Permission",
					"User has no permissions to read the requested dataset");
		}
	}

	public static void isReadPermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (! rights.toUpperCase().contains("R")) {
			throw new PermissionCheckException(new Long(403), "No Read Permission in general",
					"User has no permissions to read a dataset in general");
		}
	}

	public static void isDeletePermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (! isMarkerSet(requestorUserId, userId, rights, "D")) {
			throw new PermissionCheckException(new Long(403), "No Delete Permission",
					"User has no permissions to delete the requested dataset");
		}
	}

	public static void isDeletePermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (!rights.toUpperCase().contains("W")) {
			throw new PermissionCheckException(new Long(403), "No Delete Permission in general",
					"User has no permissions to delete a dataset in general");
		}
	}

	private static Boolean rightCheck(String rights) {
		return rights.contains("+");
	}

	private static Boolean isMarkerSet(Long requestorUserId, Long userId, String rights, String marker) {
		if (!rightCheck(rights)) {
			return false;
		}

		if (null == marker || marker.isEmpty()) {
			return false;
		}

		if (!retrieveRightGroup(requestorUserId, userId, rights).toUpperCase().contains(marker)) {
			return false;
		}
		return true;
	}

	private static String retrieveRightGroup(Long requestorUserId, Long userId, String rights) {

		String[] groups = rights.split("[+]");
		String ownerRights = groups[0];
		String otherRights = groups[1];

		if (null != userId && requestorUserId.equals(userId)) {
			return ownerRights;
		}
		return otherRights;

	}

}
