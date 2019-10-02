package com.dam.jobService.task;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.type.ActionType;

/**
 * Get list of open InvestIntents from Service Provider.
 * Charge money from house bank.
 * Send Update Request per list entry to ServiceProvider.
 * @author dirk
 *
 */
@Component
public class JobTransferToDepotIntent extends DepotClient {
	
	private final static String PATH_INTENT_TRANSFER_TO_DEPOT_CONFIRMED = "intentTransferToDepotConfirmed";
	private final static String PATH_INTENT_TRANSFER_TO_DEPOT_DECLINED = "intentTransferToDepotDeclined";
	
	public JobTransferToDepotIntent() {		
	}


	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		Iterator<Intent> it = getIntentList(ActionType.TRANSFER_TO_DEPOT_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT, NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			
			if (ExternalApiConsumer.stockIncrease(intent.getUserId(), intent.getAmount(), intent.getPortfolioId())) {
				// house bank accepted
				confirmIntent(intent, ActionType.TRANSFER_TO_DEPOT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_TRANSFER_TO_DEPOT_CONFIRMED);
			}
			else {
				declineIntent(intent, ActionType.TRANSFER_TO_DEPOT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_TRANSFER_TO_DEPOT_DECLINED);
			}
		}
	}
	

	

}
