package com.dam.depot.types;

public enum ActionType {
	
	DEPOSIT,   				// Einzahlung
	DRAWING,   				// Abhebung von Bargeld
	CHARGING,  				// Belastung durch Dritte, z.B. DebitKarten-Einsatz
	BONUS,     				// Ausschüttung einer Dividende oder ähnliches
	DEPOT_TRANSFER,			// Bewegungen zwischen Konto und Depot
	DEPOT_INVEST_INTENT,	// Markiert eine Einzahlung, die durch einen Prozess angefasst werden muss
	DEBIT_REQUEST,			// Vorübergehender negativer Betrag, der später wieder gelöscht bzw. mit einer möglichen Buchung abgeglichen wird 
	SYSTEM;    				// Aktion durch das System, z.B. Kumulation von Werten
		
	ActionType() {		
	}
	
}
