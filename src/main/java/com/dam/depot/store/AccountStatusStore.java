package com.dam.depot.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dam.depot.PermissionCheck;
import com.dam.depot.model.AccountStatusModel;
import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.rest.message.account.AccountStatusRequest;
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

	public long count() {
		return accountStatusModel.count();
	}
	
	public AccountStatus getAccountStatusSafe(AccountStatusRequest statusRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(statusRequest, statusRequest.getRequestorUserId(),
				statusRequest.getRights());
		PermissionCheck.isReadPermissionSet(statusRequest.getRequestorUserId(), null, statusRequest.getRights());

		return getAccountStatusByUserId(statusRequest.getAccountStatus().getUserId());
	}

	public AccountStatus getAccountStatusByUserId(Long userId) {
		return accountStatusModel.findByUserId(userId);
	}
	
	public AccountStatus saveAccountStatus(AccountStatus accountStatus) throws DamServiceException {
		return accountStatusModel.save(accountStatus);
	}
}
