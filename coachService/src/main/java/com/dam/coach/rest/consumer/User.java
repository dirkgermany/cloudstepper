package com.dam.coach.rest.consumer;

import com.dam.coach.types.Role;

public class User {

	private Long userId;
	
	private String userName;
	private String givenName;
	private String lastName;
	private String password;
	private Role role;
	
	public User () {
		
	}
	
	public User (Long userId, String userName, String password, String givenName, String lastName, Role role) {
		setUserName(userName);
		setPassword(password);
		setGivenName(givenName);
		setLastName(lastName);
		setRole(role);
	}
	
	public User (String userName, String password, String givenName, String lastName, Role role) {
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
