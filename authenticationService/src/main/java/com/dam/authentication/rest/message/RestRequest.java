package com.dam.authentication.rest.message;

public class RestRequest {
	
	private String clientSource;
    private Long requestorUserId;

	
	public RestRequest (String clientSource) {
		setClientSource(clientSource);
	}

	public String getClientSource() {
		return clientSource;
	}

	private void setClientSource(String clientSource) {
		this.clientSource = clientSource;
	}
	
    public Long getRequestorUserId() {
    	return requestorUserId;
    }
    
    public void setRequestorUserId(Long requestorUserId) {
    	this.requestorUserId = requestorUserId;
    }

}
