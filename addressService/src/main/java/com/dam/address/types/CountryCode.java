package com.dam.address.types;

public enum CountryCode {
	
	DE ("Germany", "de", 49),
	GB ("Great Britain", "gb", 44),
	NL ("Netherlands", "nl", 31),
	US ("United States Of America", "us", 1);
	
	private String countryName;
	private String shortCode;
	private Integer phoneCode;
	
	
	CountryCode (String countryName, String shortCode, Integer phoneCode) {
		this.countryName = countryName;
		this.shortCode = shortCode;
		this.phoneCode = phoneCode;
	}


	public String getCountryName() {
		return countryName;
	}


	public String getShortCode() {
		return shortCode;
	}


	public Integer getPhoneCode() {
		return phoneCode;
	}
	

}
