package com.dam.jobService.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.rest.message.RestRequestIntent;
import com.dam.jobService.type.ActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Get list of open InvestIntents from Service Provider. Charge money from house
 * bank. Send Update Request per list entry to ServiceProvider.
 * 
 * @author dirk
 *
 */
@Component
public class JobInvestIntent extends Client {

	private final static String DOMAIN_DEPOT = "depot";
	private final static String PATH_LIST_INTENT_INVEST = "getListIntentInvest";
	private final static String PATH_INTENT_INVEST_CONFIRMED = "intentInvestConfirmed";
	private final static String PATH_INTENT_INVEST_DECLINED = "intentInvestDeclined";
	private final static String NODE_RESPONSE_INTENT_LIST = "intentList";

	public JobInvestIntent() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		Iterator<Intent> it = getIntentList(ActionType.INVEST_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT_INVEST, NODE_RESPONSE_INTENT_LIST).iterator();
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
