package com.dam.job.task.jobs;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.job.messageClass.Intent;
import com.dam.job.rest.consumer.ExternalApiConsumer;
import com.dam.job.type.ActionType;

/**
 * Charge money from house bank. Send Update Requests to ServiceProvider. Works
 * for Invest Intents and Deposit Intents (is the same step).
 * 
 * @author dirk
 *
 */
@Component
public class JobInvestIntentFinalize extends DepotClient {

	private final static String PATH_INTENT_INVEST_CONFIRMED = "investConfirmed";
	private final static String PATH_INTENT_INVEST_DECLINED = "investDeclined";

	public JobInvestIntentFinalize() {
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
		Iterator<Intent> it = getIntentList(ActionType.INVEST_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT,
				NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			if (ExternalApiConsumer.debit(intent.getUserId(), intent.getAmount())) {
				// house bank accepted
				confirmIntent(intent, ActionType.INVEST_INTENT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_INVEST_CONFIRMED);
			} else {
				declineIntent(intent, ActionType.INVEST_INTENT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_INVEST_DECLINED);
			}
		}
	}
}
