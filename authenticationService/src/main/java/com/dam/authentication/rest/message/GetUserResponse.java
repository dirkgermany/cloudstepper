package com.dam.authentication.rest.message;

import org.springframework.http.HttpStatus;

import com.dam.authentication.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetUserResponse extends RestResponse{
    private User user = new User();
    
    public GetUserResponse () {
		super(HttpStatus.OK, "OK", "User validated");
    }
    	
	public GetUserResponse (User user) {
		super(HttpStatus.OK, "OK", "User validated");
		
		this.user = user;
	}  
	
	public GetUserResponse(Long userId, String userName, String password, String givenName, String lastName) {
		super(HttpStatus.OK, "OK", "User validated");
		user = new User(userId, userName, password, givenName, lastName);
		
	}
	
	public void setUser (User user) {
		this.user = user;
	}

	public User getUser() {
		return this.user;
	}
		
}
