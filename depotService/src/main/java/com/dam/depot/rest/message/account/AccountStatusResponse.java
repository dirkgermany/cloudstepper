package com.dam.depot.rest.message.account;

import java.util.List;

import com.dam.depot.model.entity.AccountStatus;
import com.dam.depot.rest.message.RestResponse;

public class AccountStatusResponse extends RestResponse {

	private AccountStatus accountStatus;
	private List<Long> portfolioIdList;

	public AccountStatusResponse(AccountStatus accountStatus, List<Long> portfolioIdList) {
		super(new Long(200), "OK", "Request performed");
		setAccountStatus(accountStatus);
		setPortfolioIdList(portfolioIdList);
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public List<Long> getPortfolioIdList() {
		return portfolioIdList;
	}

	public void setPortfolioIdList(List<Long> portfolioIdList) {
		this.portfolioIdList = portfolioIdList;
	}
}
