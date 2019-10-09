package com.dam.provider.rest.service.message;

public class RestRequest {
	
	private String clientSource;
	
	public RestRequest (String clientSource) {
		setClientSource(clientSource);
	}

	public String getClientSource() {
		return clientSource;
	}

	private void setClientSource(String clientSource) {
		this.clientSource = clientSource;
	}
	

}
