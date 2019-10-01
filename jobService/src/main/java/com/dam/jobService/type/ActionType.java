package com.dam.jobService.type;

public enum ActionType {
	
	INVEST,   					// Einzahlungsabsicht durch den Investor
	INVEST_INTENT,				// Investor will durch Geldeinzug das Depot erhöhen
	INVEST_INTENT_CONFIRMED,	// Bank hat Einzug bestätigt
	INVEST_INTENT_DECLINED,		// Bank hat Einzug abgelehnt
	TRANSFER_TO_DEPOT_INTENT,	// Geld soll vom Tagesgeldkonto auf das Depotkonto transferiert werden
	DEBIT,   					// Abhebung von Bargeld
	DEPOSIT,					// Einzahlung durch Geldeinzug
	CHARGING,  					// Belastung durch Dritte, z.B. DebitKarten-Einsatz
	BONUS,     					// Ausschüttung einer Dividende oder ähnliches
	DEPOT_TRANSFER,				// Bewegungen zwischen Konto und Depot
	DEBIT_REQUEST,				// Vorübergehender negativer Betrag, der später wieder gelöscht bzw. mit einer möglichen Buchung abgeglichen wird 
	SYSTEM;    					// Aktion durch das System, z.B. Kumulation von Werten
		
	ActionType() {		
	}
	
}
