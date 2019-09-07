package com.dam.portfolio.types;

public enum Risc {
	
	CONSERVATIVE ("konservativ"),
	MODERATE ("moderat"),
	BALANCED ("ausgewogen"),
	DEDICATED ("chancenorientiert"),
	RISKY ("risikobereit");
	
	private String meaning;
	
	Risc() {
		
	}
	
	Risc (String meaning) {
		this.meaning = meaning;
	}
	
	public String getMeaning() {
		return meaning;
	}

}
