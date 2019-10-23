package com.dam.depot.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.PermissionCheck;
import com.dam.depot.model.AccountStatusModel;
import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.model.entity.Intent;
import com.dam.depot.rest.message.account.AccountStatusRequest;
import com.dam.depot.rest.message.account.AccountStatusResponse;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class AccountStatusStore {

	@Autowired
	private AccountStatusModel accountStatusModel;

	@Autowired
	private DepotStore depotStore;

	@Autowired
	private IntentStore intentStore;

	public long count() {
		return accountStatusModel.count();
	}

	public AccountStatusResponse getAccountStatusSafe(AccountStatusRequest statusRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(statusRequest, statusRequest.getRequestorUserId(),
				statusRequest.getRights());
		PermissionCheck.isReadPermissionSet(statusRequest.getRequestorUserId(), null, statusRequest.getRights());

		AccountStatus accountStatus = getAccountStatusByUserId(statusRequest.getAccountStatus().getUserId());
		if (null != accountStatus) {
			AccountStatusResponse response = new AccountStatusResponse(accountStatus, null);
			// Depots
			List<Depot> depotList = depotStore.getDepotByUserId(statusRequest.getAccountStatus().getUserId());
			if (null != accountStatus && null != depotList && !depotList.isEmpty()) {
				response.setPortfolioIdList(new ArrayList<Long>());
				Iterator<Depot> it = depotList.iterator();
				while (it.hasNext()) {
					response.getPortfolioIdList().add(it.next().getPortfolioId());
				}
			}

			// Open Transactions
			List<Intent> intentList = intentStore.getIntentOpenListByUser(statusRequest.getAccountStatus().getUserId());
			Iterator<Intent> itIntent = intentList.iterator();
			while (itIntent.hasNext()) {
				Intent intent = itIntent.next();
				switch (intent.getAction()) {
				case INVEST_INTENT:
					accountStatus.setAmountInvestIntent(accountStatus.getAmountInvestIntent() + intent.getAmount());
					break;

				case SELL_INTENT:
					accountStatus.setAmountSellIntent(accountStatus.getAmountSellIntent() + intent.getAmount());
					break;

				case DEPOSIT_INTENT:
					accountStatus.setAmountDepositIntent(accountStatus.getAmountDepositIntent() + intent.getAmount());
					break;

				case DEBIT_INTENT:
					accountStatus.setAmountDebitIntent(accountStatus.getAmountDebitIntent() + intent.getAmount());
					break;

				case TRANSFER_TO_ACCOUNT_INTENT:
					accountStatus.setAmountTransferToAccountIntent(
							accountStatus.getAmountTransferToAccountIntent() + intent.getAmount());
					break;

				case TRANSFER_TO_DEPOT_INTENT:
					accountStatus.setAmountTransferToDepotIntent(
							accountStatus.getAmountTransferToDepotIntent() + intent.getAmount());
					break;

				default:
					break;
				}
			}

			return response;
		}
		return null;
	}

	public AccountStatus getAccountStatusByUserId(Long userId) {
		return accountStatusModel.findByUserId(userId);
	}

	public AccountStatus saveAccountStatus(AccountStatus accountStatus) throws DamServiceException {
		return accountStatusModel.save(accountStatus);
	}
}
