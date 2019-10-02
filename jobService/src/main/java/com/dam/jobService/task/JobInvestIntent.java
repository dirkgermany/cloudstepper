package com.dam.jobService.task;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.type.ActionType;

/**
 * Get list of open InvestIntents from Service Provider. Charge money from house
 * bank. Send Update Request per list entry to ServiceProvider.
 * 
 * @author dirk
 *
 */
@Component
public class JobInvestIntent extends DepotClient {

	private final static String PATH_INTENT_INVEST_CONFIRMED = "intentInvestConfirmed";
	private final static String PATH_INTENT_INVEST_DECLINED = "intentInvestDeclined";

	public JobInvestIntent() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		Iterator<Intent> it = getIntentList(ActionType.INVEST_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT, NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			if (ExternalApiConsumer.debit(intent.getUserId(), intent.getAmount())) {
				// house bank accepted
				confirmIntent(intent, ActionType.INVEST_INTENT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_INVEST_CONFIRMED);
			}
			else {
				declineIntent(intent, ActionType.INVEST_INTENT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_INVEST_DECLINED);
			}
		}
	}
}
