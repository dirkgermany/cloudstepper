package com.dam.user.rest.message;

import com.dam.user.model.entity.User;

public class UserResponse extends RestResponse{
    private User user;
    	
	public UserResponse (User user) {
		super(200L, "OK", "User exists");
		
		setUser(user);
	}  

	private void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return this.user;
	}
	
}
