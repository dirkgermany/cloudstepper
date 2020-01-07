package com.dam.authentication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.dam.authentication.PermissionCheck;
import com.dam.authentication.model.entity.Permission;
import com.dam.authentication.rest.message.ErrorResponse;
import com.dam.authentication.rest.message.PermissionsByServiceResponse;
import com.dam.authentication.rest.message.RestResponse;
import com.dam.exception.DamServiceException;

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
	
//	public createPermissionSafe(PermissionCreateRequest createRequest, Map<String, String> requestParams,
//			Map<String, String> headers) throws DamServiceException {
//		
//		if ()
//	}

	public ResponseEntity<RestResponse> getPermissionsByDomainSafe(Map<String, String> requestParams,
			Map<String, String> headers) throws DamServiceException {

		String serviceDomain = requestParams.get("serviceDomain");

		if (null == serviceDomain || serviceDomain.isEmpty()) {
			return new ResponseEntity<RestResponse>(
					new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid requestParam ServiceDomain", "ServiceDomain is empty or null"),
					HttpStatus.OK);
		}

		String rights = requestParams.get("rights");
		if (null == rights) {
			rights = headers.get("rights");
		}
		String requestorUserIdAsString = requestParams.get("requestorUserId");
		if (null == requestorUserIdAsString) {
			requestorUserIdAsString = headers.get("requestoruserid");
		}

		PermissionCheck.checkRequestedParams(requestorUserIdAsString, rights);
		Long requestorUserId = extractLong(requestorUserIdAsString);

		List<Permission> permissions = permissionModel.findByServiceDomain(serviceDomain);

		if (null != permissions && !permissions.isEmpty()) {
			try {
			PermissionCheck.isReadPermissionSet(requestorUserId, null, rights);
			return new ResponseEntity<RestResponse>(new PermissionsByServiceResponse(permissions), HttpStatus.OK);
			}
			catch (DamServiceException dse) {
				return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.FORBIDDEN, dse.getShortMsg(),
						dse.getDescription()), HttpStatus.OK);
			}
		}

		return new ResponseEntity<RestResponse>(new ErrorResponse(HttpStatus.NOT_FOUND, "Permissions not found",
				"No Permission for ServiceDomain found"), HttpStatus.OK);

	}

	public List<Permission> getPermissionList() {
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

	/**
	 * Drop by ID
	 * 
	 * @param id
	 * @return
	 */
	public Long dropPermission(Long id) {
		if (null == id) {
			return new Long(10);
		}

		permissionModel.deleteById(id);
		if (null == getPermission(id)) {
			return new Long(0);
		}

		return new Long(10);
	}

	private Long extractLong(String longString) throws DamServiceException {
		try {
			return Long.valueOf(longString);
		} catch (Exception e) {
			throw new DamServiceException(404L, "Extraction Long from String failed",
					"Parameter is required but null or does not represent a Long value");
		}
	}

}
