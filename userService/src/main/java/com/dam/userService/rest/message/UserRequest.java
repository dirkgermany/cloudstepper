package com.dam.userService.rest.message;

import com.dam.userService.model.entity.User;

public class UserRequest extends RestRequest {
    private User user = new User();

    public UserRequest(String userName, String password, String requestorUserId) {
		super("DAM 2.0");
		user.setUserName(userName);
		user.setPassword(password);
		setRequestorUserId(requestorUserId);
	}
    
    public User getUser() {
    	return user;
    }
    
}