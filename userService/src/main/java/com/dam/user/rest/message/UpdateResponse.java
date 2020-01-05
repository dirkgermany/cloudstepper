package com.dam.user.rest.message;

import org.springframework.http.HttpStatus;

import com.dam.user.model.entity.UserMessageContainer;

public class UpdateResponse extends RestResponse{
    private UserMessageContainer user;
    	
	public UpdateResponse (UserMessageContainer user) {
		super(HttpStatus.OK, "OK", "User updated");
		
		setUser(user);
	}  

	private void setUser(UserMessageContainer user) {
		this.user = user;
	}
	
	public UserMessageContainer getUser() {
		return user;
	}
	
}
