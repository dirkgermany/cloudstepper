package com.dam.person;

import com.dam.exception.PermissionCheckException;

public class PermissionCheck {

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
			throw new PermissionCheckException(403L, "No Write Permission",
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
			throw new PermissionCheckException(403L, "No Write Permission in general",
					"User has no permissions to write or update a dataset in general");
		}
	}

	public static void isReadPermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (!isMarkerSet(requestorUserId, userId, rights, "R")) {
			throw new PermissionCheckException(403L, "No Read Permission",
					"User has no permissions to read the requested dataset");
		}
	}

	public static void isReadPermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (! rights.toUpperCase().contains("R")) {
			throw new PermissionCheckException(403L, "No Read Permission in general",
					"User has no permissions to read a dataset in general");
		}
	}

	public static void isDeletePermissionSet(Long requestorUserId, Long userId, String rights)
			throws PermissionCheckException {
		if (! isMarkerSet(requestorUserId, userId, rights, "D")) {
			throw new PermissionCheckException(403L, "No Delete Permission",
					"User has no permissions to delete the requested dataset");
		}
	}

	public static void isDeletePermissionSetInGeneral(String rights) throws PermissionCheckException {
		if (!rights.toUpperCase().contains("W")) {
			throw new PermissionCheckException(403L, "No Delete Permission in general",
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

		if (requestorUserId.equals(userId)) {
			return ownerRights;
		}
		return otherRights;

	}

}
