package com.dam.user.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "User",
indexes = {@Index(name = "idx_user_username", columnList = "userName")})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;
	
	@Column(nullable=false)
	private String userName;
	@Column(nullable=false)
	private String givenName;
	@Column(nullable=false)
	private String lastName;
	@Column(nullable=false)
	private String password;
	@Column(nullable=false)
	private String role;
	
	public User () {
		
	}
	
	public User (Long userId, String userName, String password, String givenName, String lastName, String role) {
		setUserName(userName);
		setPassword(password);
		setGivenName(givenName);
		setLastName(lastName);
		setRole(role);
	}
	
	public User (String userName, String password, String givenName, String lastName, String role) {
		this(null, userName, password, givenName, lastName, role);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Updates Entity values.
	 * BUT doesn't overwrites userName or password with NULL values.
	 * userId is not changeable
	 * @param updateUser
	 */
	public void updateEntity (User updateUser) {
		if (null == updateUser) {
			return;
		}
		
		if (null != updateUser.getUserName()) {
			setUserName(updateUser.getUserName());
		}
		
		if (null != updateUser.getPassword())  {
			setPassword(updateUser.getPassword());
		}
		
		setGivenName(updateUser.getGivenName());
		setLastName(updateUser.getLastName());
		setRole(updateUser.getRole());
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
