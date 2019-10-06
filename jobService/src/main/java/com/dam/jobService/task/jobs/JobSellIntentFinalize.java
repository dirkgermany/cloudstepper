package com.dam.jobService.task.jobs;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.type.ActionType;

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

		// Was aus Sicht des Investors auf dem Smartphone ein DEPOSIT (also Guthaben
		// erhöhen) ist,
		// ist bei der Hausbank ein DEBIT (also Belastung).

		// Investment
		Iterator<Intent> it = getIntentList(ActionType.SELL_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT,
				NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			if (ExternalApiConsumer.stockSellAssets(intent.getUserId(), intent.getAmount(), intent.getPortfolioId())) {
				// house bank accepted
				confirmIntent(intent, ActionType.SELL_INTENT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_SELL_CONFIRMED);
			} else {
				declineIntent(intent, ActionType.SELL_INTENT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_SELL_DECLINED);
			}
		}
	}
}
