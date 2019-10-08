package com.dam.stock.type;

public enum Symbol {
	
	EXXT_DE ("EXXT.DE"),
	EUNL_DE ("EUNL.DE"),
	IHI ("IHI"),
	IQQE_DE ("IQQE.DE"), 
	SXRT_DE ("SXRT.DE"),
	BND ("BND"),
	IBCD_DE ("IBCD.DE"),
	IGLN_L ("IGLN.L"); 

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
