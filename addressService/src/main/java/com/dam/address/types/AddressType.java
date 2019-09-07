package com.dam.address.types;

public enum AddressType {
	
	PRIMARY ("primary"),
	SECONDARY ("secondary"),
	POSTBOX ("postbox");
	
	private String term;
	
	AddressType() {
		
	}
	
	AddressType (String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}

}
