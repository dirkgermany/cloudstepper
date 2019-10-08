package com.dam.stock.task.jobs;

import org.springframework.stereotype.Component;
import com.dam.exception.DamServiceException;

/**
 * Charge money from house bank. Send Update Requests to ServiceProvider. Works
 * for Invest Intents and Deposit Intents (is the same step).
 * 
 * @author dirk
 *
 */
@Component
public class JobSellIntentFinalize extends DepotClient {

	private final static String PATH_INTENT_SELL_CONFIRMED = "sellConfirmed";
	private final static String PATH_INTENT_SELL_DECLINED = "sellDeclined";

	public JobSellIntentFinalize() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

	}
}
