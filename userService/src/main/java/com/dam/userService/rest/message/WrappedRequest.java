package com.dam.userService.rest.message;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * LoginRequest is the typical request to authenticate the user against the system.
 * 
 * Response: TokenResponse
 * 
 * Scope: External Calls
 * 
 * @author dirk
 *
 */

public class WrappedRequest {

    private JsonNode wrappedMsg;
//    private String password;

    public WrappedRequest(JsonNode wrappedMsg, String dummy) {
//		super("DAM 2.0");
 //       this.ipAddress = ipAddress;
    	this.wrappedMsg=wrappedMsg;
 //   	this.password = password;
    }
    

	public JsonNode getWrappedMsg() {
		return wrappedMsg;
	}

	public void setWrappedMsg(JsonNode wrappedMsg) {
		this.wrappedMsg = wrappedMsg;
	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

}