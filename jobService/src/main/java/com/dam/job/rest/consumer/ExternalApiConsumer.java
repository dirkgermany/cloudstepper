package com.dam.job.rest.consumer;

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
		return true;
	}
	
	/**
	 * Investition in Depot.
	 * @param userId
	 * @param amount
	 * @param portfolioId used to find out the WKN or whatever the depot bank needs
	 * @return
	 */
	public static boolean stockBuyAssets (Long userId, float amount, Long portfolioId) {
		return true;
	}
	
	/**
	 * Verkauf von Werten aus Depot.
	 * @param userId
	 * @param amount
	 * @param wkn
	 * @return
	 */
	public static boolean stockSellAssets (Long userId, float amount, Long portfolioId) {
		return true;
	}

}
