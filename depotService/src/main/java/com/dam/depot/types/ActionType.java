package com.dam.depot.types;

public enum ActionType {
	
	DEPOSIT,   // Einzahlung
	DRAWING,   // Abhebung von Bargeld
	CHARGING,  // Belastung durch Dritte, z.B. DebitKarten-Einsatz
	BONUS,     // Ausschüttung einer Dividende oder ähnliches
	SYSTEM;    // Aktion durch das System, z.B. Kumulation von Werten
		
	ActionType() {		
	}
	
}
