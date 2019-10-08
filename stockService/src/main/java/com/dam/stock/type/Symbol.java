package com.dam.stock.type;

public enum Symbol {
	
	ISHARES_NASDAQ_100_UCITS_ETF ("EXXT.DE");
	
	private String requestSymbol;
	
	Symbol() {
		
	}
	
	Symbol (String requestSymbol) {
		this.requestSymbol = requestSymbol;
	}
	
	public String getRequestSymbol() {
		return requestSymbol;
	}
}
