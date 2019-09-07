package com.dam.person.rest.message;

public class RestRequest {
	
	private String clientSource;
    private Long requestorUserId;
    private String rights;

	
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

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

}
