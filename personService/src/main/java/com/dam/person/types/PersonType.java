package com.dam.person.types;

public enum PersonType {
	
	PRIVATE ("private"),
	COMPANY ("company"),
	AGENCY ("agency");
	
	private String term;
	
	PersonType() {
		
	}
	
	PersonType (String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}


}
