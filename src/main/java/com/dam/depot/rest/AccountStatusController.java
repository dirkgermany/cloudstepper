package com.dam.depot.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.depot.RequestBlocker;
import com.dam.depot.rest.message.RestResponse;
import com.dam.depot.rest.message.account.AccountStatusRequest;
import com.dam.depot.rest.message.account.AccountStatusResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionListResponse;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionRequest;
import com.dam.depot.rest.message.depotTransaction.DepotTransactionResponse;
import com.dam.depot.store.AccountStatusStore;
import com.dam.depot.store.DepotTransactionStore;
import com.dam.exception.DamServiceException;

@RestController
public class AccountStatusController {
	@Autowired
	private AccountStatusStore accountStatusStore;

	@PostMapping("/getAccountStatus")
	public RestResponse getAccountStatus (@RequestBody AccountStatusRequest accountStatusRequest) throws DamServiceException {
		try {
			return new AccountStatusResponse(accountStatusStore.getAccountStatusSafe(accountStatusRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}