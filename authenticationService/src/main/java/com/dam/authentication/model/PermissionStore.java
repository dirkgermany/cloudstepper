package com.dam.authentication.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.authentication.model.entity.Permission;


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

	public List<Permission> getPermissionList() {
		List<Permission> rolePermissions = new ArrayList<Permission>();
		permissionModel.findAll().forEach((Permission permission) -> rolePermissions.add(permission));
		return rolePermissions;
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

}
