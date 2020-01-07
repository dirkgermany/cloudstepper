package com.dam.authentication.model;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.authentication.model.entity.Permission;


@Transactional
public interface PermissionModel extends Repository<Permission, Long>, CrudRepository<Permission, Long> {

	Optional<Permission> findBy_id(Long id);
	
	Permission findByRole(String role);
	List<Permission> findByServiceDomain(String domain);
	
	@Query("SELECT permission FROM Permission permission where permission.role = :role "
			+ "AND permission.serviceDomain = :serviceDomain")
	Permission findByRoleDomain(@Param("role") String role, @Param("serviceDomain") String serviceDomain);

	
	@Modifying
	@Transactional
	@Query("Update Permission permission set "
	        + "permission.role = :#{#permission.role}, "
	        + "permission.serviceDomain = :#{#permission.serviceDomain}, "
			+ "permission.rights = :#{#permission.rights} "
			+ "Where permission._id = :#{#permission._id} ")
	Integer update(@Param("permission") Permission permission);

}
