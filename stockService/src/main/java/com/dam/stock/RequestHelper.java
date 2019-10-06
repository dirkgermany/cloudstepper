package com.dam.stock;

import com.dam.exception.DamServiceException;

public class RequestHelper {
	
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
	
	public static void checkPortfolio(Long portfolioId) throws DamServiceException {
		if (null == portfolioId) {
			throw new DamServiceException(400L,  "Invalid portfolioId", "portfolioId is null");
		}
	}

}
