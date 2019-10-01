package com.dam.jobService.rest.consumer;

public class ExternalApiConsumer {
	
	/**
	 * Einzahlung auf Bankkonto.
	 * @return
	 */
	public static boolean deposit (Long userId, float amount) {
		return true;
	}
	
	/**
	 * Abhebung von Bankkonto.
	 * @param userId
	 * @param amount
	 * @return
	 */
	public static boolean debit (Long userId, float amount) {
		return false;
	}
	
	/**
	 * Investition in Depot.
	 * @param userId
	 * @param amount
	 * @param wkn		WertKennNummer
	 * @return
	 */
	public static boolean stockIncrease (Long userId, float amount, String wkn) {
		return true;
	}
	
	/**
	 * Verkauf von Werten aus Depot.
	 * @param userId
	 * @param amount
	 * @param wkn
	 * @return
	 */
	public static boolean stockDecrease (Long userId, float amount, String wkn) {
		return true;
	}

}
