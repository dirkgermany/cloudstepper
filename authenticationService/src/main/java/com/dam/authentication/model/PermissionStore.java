package com.dam.authentication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.authentication.PermissionCheck;
import com.dam.authentication.model.entity.Permission;
import com.dam.authentication.rest.message.WriteRequest;
import com.dam.exception.CsServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class PermissionStore {

	@Autowired
	private PermissionModel permissionModel;

	public long count() {
		return permissionModel.count();
	}

	public Permission getPermissionSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		String permissionIdAsString = requestParams.get("permissionId");

		Permission permission = null;
		if (null != permissionIdAsString) {
			Long permissionId = extractLong(permissionIdAsString);
			permission = getPermission(permissionId);
		}
		if (null == permission) {
			throw new CsServiceException(404L, "Permission not found", "Unknown permissionId");
		}
		return permission;
	}

	public List<Permission> listPermissionsSafe(Map<String, String> headers) throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		ArrayList<Permission> permissions = new ArrayList<>();
		permissionModel.findAll().forEach(permission -> permissions.add(permission));
		return permissions;
	}

	public List<Permission> getPermissionsByDomainSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {

		String serviceDomain = requestParams.get("serviceDomain");
		if (null == serviceDomain || serviceDomain.isEmpty()) {
			throw new CsServiceException(400L, "Invalid Request", "serviceDomain is recommended but not set.");
		}

		String rights = headers.get("rights");
		String requestorUserIdAsString = headers.get("requestoruserid");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);

		List<Permission> permissions = permissionModel.findByServiceDomain(serviceDomain);

		if (null == permissions || permissions.isEmpty()) {
			throw new CsServiceException(404L, "Permissions not found by domain",
					"No permissions related to domain stored");
		}
		return permissions;
	}

	public Permission createPermissionSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getPermission(), Permission.class, "");

		// das muss noch in die Domänen-Verwaltung rein
		if (requestBody.getPermission().getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "Permission not created",
						"Creating permissions with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, null, rights);

		return createPermission(requestBody.getPermission());
	}

	public Permission updatePermissionSafe(WriteRequest requestBody, Map<String, String> requestParams,
			Map<String, String> headers) throws CsServiceException {

		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestBody, requestorUserIdAsString, rights);
		PermissionCheck.checkRequestedEntity(requestBody.getPermission(), Permission.class, "");

		Permission permissionContainer = requestBody.getPermission();
		if (null == permissionContainer.get_id()) {
			throw new CsServiceException(401L, "Cannot update permission", "Permission id is empty");
		}

		if (permissionContainer.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "Permission not updated",
						"Updating permission with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isWritePermissionSet(requestorUserId, null, rights);

		return updatePermission(permissionContainer);
	}

	public void dropPermissionSafe(Map<String, String> requestParams, Map<String, String> headers)
			throws CsServiceException {
		String requestorUserIdAsString = headers.get("requestoruserid");
		String rights = headers.get("rights");
		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);

		Permission storedPermission = null;
		String permissionIdAsString = requestParams.get("permissionId");
		if (null == permissionIdAsString) {
			throw new CsServiceException(500L, "Cannot drop permission", "Permission id is null");
		}

		Long permissionId = extractLong(requestParams.get("permissionId"));
		storedPermission = getPermission(permissionId);

		if (null == storedPermission) {
			throw new CsServiceException(500L, "Cannot drop permission", "Permission not found by id");
		}

		if (storedPermission.getRole().equalsIgnoreCase("ROOT_ADMIN")) {
			String rightOption = headers.get("rightoption");
			if (null == rightOption || !"Write_Root_Admin=true".equalsIgnoreCase(rightOption)) {
				throw new CsServiceException(401L, "Permission not deleted",
						"Deleting permission with ROOT rights is not allowed for the current user");
			}
		}

		Long requestorUserId = extractLong(requestorUserIdAsString);
		PermissionCheck.isDeletePermissionSet(requestorUserId, null, rights);
		dropPermission(storedPermission.get_id());
	}

	private Permission updatePermission(Permission permissionContainer) throws CsServiceException {

		if (null == permissionContainer.get_id()) {
			throw new CsServiceException(401L, "Cannot update permission", "Permission id is null");
		}
		Permission storedPermission = getPermission(permissionContainer.get_id());

		if (null == storedPermission) {
			throw new CsServiceException(401L, "Cannot update permission", "Permission not found by id");
		}

		storedPermission.updateFrom(permissionContainer);
		storedPermission = permissionModel.save(storedPermission);
		if (null == storedPermission) {
			throw new CsServiceException(401L, "Permission not updated", "Unknown reason but permission was not saved");
		}
		return storedPermission;
	}

	private List<Permission> getPermissionList() {
		List<Permission> rolePermissions = new ArrayList<Permission>();
		permissionModel.findAll().forEach((Permission permission) -> rolePermissions.add(permission));
		return rolePermissions;
	}

	public Permission getPermission(String role, String serviceDomain) {
		return permissionModel.findByRoleDomain(role, serviceDomain);
	}

	/**
	 * Standard read by id
	 * 
	 * @param _id
	 * @return
	 */
	public Permission getPermission(Long id) {
		if (null == id) {
			return null;
		}

		Optional<Permission> optionalPermission = permissionModel.findById(id);
		if (null != optionalPermission && optionalPermission.isPresent()) {
			return optionalPermission.get();
		}
		return null;
	}

	public void dropPermission(Long id) throws CsServiceException {
		permissionModel.deleteById(id);
		Permission permission = getPermission(id);
		if (null != permission) {
			throw new CsServiceException(404L, "Permission not deleted",
					"Unknown reason but permission could not be deleted");
		}
	}

	private Permission createPermission(Permission permissionContainer) throws CsServiceException {

		if (null == permissionContainer.getRights() || null == permissionContainer.getRole()
				|| null == permissionContainer.getServiceDomain()) {
			return null;
		}

		// does the _id already exists?
		if (null != getPermission(permissionContainer.get_id())) {
			throw new CsServiceException(409L, "Permission not created",
					"Permission already exists, try update method");
		}

		try {
			Permission permission = permissionModel.save(permissionContainer);
			return permission;
		} catch (Exception ex) {
			return null;
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
