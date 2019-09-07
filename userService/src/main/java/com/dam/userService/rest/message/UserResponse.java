package com.dam.userService.rest.message;

import com.dam.userService.model.entity.User;

public class UserResponse extends RestResponse{
    private User user;
    	
	public UserResponse (User user) {
		super(new Long(0), "OK", "User exists");
		
		setUser(user);
	}  

	private void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return this.user;
	}
	
}
