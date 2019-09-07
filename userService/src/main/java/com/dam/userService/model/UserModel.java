package com.dam.userService.model;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.userService.model.entity.User;

@Transactional
public interface UserModel extends Repository<User, Long>, CrudRepository<User, Long> {

//	List<User> findByUserName(String userName);
	User findByUserName(String userName);
	Optional<User> findByUserId(Long userId);
//	List<User> findByClientId(int clientId);

	@Query("SELECT user FROM User user where user.userName = :userName "
			+ "AND user.password = :password")
	User getUser(@Param("userName") String userName, @Param("password") String password);
//	
//	@Query("SELECT pers FROM PersonEntity pers where pers.clientId = :clientId "
//			+ "AND UPPER(pers.foreName) = UPPER(:foreName) "
//			+ "AND UPPER(pers.surName) = UPPER(:surName)")
//	PersonEntity findPersonByNames(@Param("clientId") int clientId, @Param("foreName") String foreName, @Param("surName") String surName);
	
	@Modifying
	@Transactional
	@Query("Update User user set "
	        + "user.userName = :#{#user.userName}, "
	        + "user.givenName = :#{#user.givenName}, "
			+ "user.lastName = :#{#user.lastName}, "
			+ "user.password = :#{#user.password} "
			+ "Where user.userId = :#{#user.userId} ")
	Integer updateUser(@Param("user") User user);
}
