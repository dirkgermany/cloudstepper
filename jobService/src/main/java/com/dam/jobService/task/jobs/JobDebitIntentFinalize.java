package com.dam.jobService.task.jobs;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.type.ActionType;

/**
 * Pay money to house bank. Send Update Requests to ServiceProvider.
 * 
 * @author dirk
 *
 */
@Component
public class JobDebitIntentFinalize extends DepotClient {
	private final static String PATH_INTENT_DEBIT_CONFIRMED = "debitConfirmed";
	private final static String PATH_INTENT_DEBIT_DECLINED = "debitDeclined";

	public JobDebitIntentFinalize() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren
		
		// Was aus Sicht des Investors auf dem Smartphone ein DEPOSIT (also Guthaben erhöhen) ist,
		// ist bei der Hausbank ein DEBIT (also Belastung).

		// Call Money
		Iterator<Intent> it = getIntentList(ActionType.DEBIT_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT, NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			if (ExternalApiConsumer.debit(intent.getUserId(), intent.getAmount())) {
				// house bank accepted
				confirmIntent(intent, ActionType.DEBIT_INTENT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_DEBIT_CONFIRMED);
			}
			else {
				declineIntent(intent, ActionType.DEBIT_INTENT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_DEBIT_DECLINED);
			}
		}
}
}
