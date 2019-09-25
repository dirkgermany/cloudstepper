package com.dam.depot;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dam.depot.rest.message.depot.DepotRequest;
import com.dam.depot.types.ActionType;
import com.dam.depot.types.Currency;
import com.dam.exception.DamServiceException;

public class RequestHelper {
	
	private final static Set<String> values = new HashSet<String>(Currency.values().length);

	static {
		for (Currency c : Currency.values())
			values.add(c.name());
	}

	public static void checkActions(ActionType action, List<ActionType> allowedActions) throws DamServiceException {
		if (!allowedActions.contains(action)) {
			throw new DamServiceException(500L, "Invalid action",
					"Action used: " + action.name() + ", actions allowed: " + allowedActions);
		}
	}

	public static void checkAmountTransfer(Float amount) throws DamServiceException {
		if (amount < 0) {
			throw new DamServiceException(500L, "Invalid amount",
					"Amount in money transfer actions must always be > (greater than) 0");
		}
	}

	public static float setAmountNegative(float amount) {
		return amount < 0 ? amount : amount * -1;
	}

	public static float setAmountPositive(float amount) {
		return amount >= 0 ? amount : amount * -1;
	}

	public static void checkCurrency(Currency currency) throws DamServiceException {
		if (! values.contains(currency.name())) {
			throw new DamServiceException(400L, "Unknown or invalid currency", "Currency in Request not allowed: " + currency.name());
		}
	}

}
