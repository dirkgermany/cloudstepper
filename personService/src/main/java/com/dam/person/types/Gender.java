package com.dam.person.types;

public enum Gender {
	
	MALE ("male"),
	FEMALE ("female"),
	INTER ("inter");
	
	private String term;
	
	Gender() {
		
	}
	
	Gender (String term) {
		this.term = term;
	}
	
	public String getTerm() {
		return term;
	}

}
