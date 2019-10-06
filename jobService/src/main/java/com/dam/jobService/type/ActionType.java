package com.dam.jobService.type;

public enum ActionType {
	
	INVESTx,   						// Einzahlungsabsicht durch den Investor
	INVEST_INTENT,					// Investor will durch Geldeinzug das Depot erhöhen
	INVEST_INTENT_CONFIRMED,		// Bank hat Einzug bestätigt
	INVEST_INTENT_DECLINED,			// Bank hat Einzug abgelehnt
	SELL_INTENT,					// Investor will Anteile direkt verkaufen
	SELL_INTENT_CONFIRMED,			// Depotbank hat Verkauf bestätigt
	SELL_INTENT_DECLINED,			// Depotbank hat Verkauf abgelehnt
	DEPOSIT_INTENT,					// Investor will durch Geldeinzug sein Tagesgeld erhöhen
	DEPOSIT_INTENT_CONFIRMED,		// Bank hat Einzug bestätigt
	DEPOSIT_INTENT_DECLINED,		// Bank hat Geldeinzug abgelehnt
	DEBIT_INTENT,					// Investor will durch Einzahlung auf seine Hausbank sein Tagesgeld verringern
	DEBIT_INTENT_CONFIRMED,			// Bank hat Einzahlung bestätigt
	DEBIT_INTENT_DECLINED,			// Bank hat Einzahlung abgelehnt
	TRANSFER_TO_DEPOT_INTENT,		// Geld soll vom Tagesgeldkonto auf das Depotkonto transferiert werden
	TRANSFER_TO_DEPOT_CONFIRMED,	// Depotbank hat Aufstockung des Depots bestätigt
	TRANSFER_TO_DEPOT_DECLINED,		// Depotbank hat Aufstockung des Depots abgelehnt
	TRANSFER_TO_ACCOUNT_INTENT,		// Geld soll vom Depot auf das Tagesgeldkonto transferiert werden
	TRANSFER_TO_ACCOUNT_CONFIRMED,	// Depotbank hat Verkauf aus Depot bestätigt
	TRANSFER_TO_ACCOUNT_DECLINED,	// Depotbank hat Verkauf aus Depot abgelehnt
	DEBIT,   						// Abhebung von Bargeld
	DEPOSIT,						// Einzahlung durch Geldeinzug
	SELL,							// Verkauf von Assets
	CHARGING,  						// Belastung durch Dritte, z.B. DebitKarten-Einsatz
	BONUS,     						// Ausschüttung einer Dividende oder ähnliches
	DEPOT_TRANSFER,					// Bewegungen zwischen Konto und Depot
	DEBIT_REQUEST,					// Vorübergehender negativer Betrag, der später wieder gelöscht bzw. mit einer möglichen Buchung abgeglichen wird 
	SYSTEM;    						// Aktion durch das System, z.B. Kumulation von Werten
		
	ActionType() {		
	}
	
}
