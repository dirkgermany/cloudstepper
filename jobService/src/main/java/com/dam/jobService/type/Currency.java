package com.dam.jobService.type;

public enum Currency {
	
	EUR ("Euro"),
	USD ("US Dollar"),
	GBP ("British Pound");
	
	private String linguistic;
	
	Currency (String linguistic) {
		this.linguistic = linguistic;
	}
	
	public String getLinguistic() {
		return linguistic;
	}

}