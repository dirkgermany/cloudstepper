package com.dam.depot.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.dam.depot.types.Currency;

@Entity
@Component
@Table(name = "AccountStatus", uniqueConstraints= {@UniqueConstraint(columnNames = {"userId"})}
)
public class AccountStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long accountStatusId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = true)
	private Float amountAccount;

	@Column(nullable = true)
	private Float amountDepot;
	
	@Column(nullable = true)
	private Float amountAccountIntent;
	
	@Column(nullable = true)
	private Float amountDepotIntent;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;
	
	@Column(nullable = true)
	private Date lastUpdate;
	
	@Transient
	private Float amountDisposable;
	
	@Transient
	private Float amountInvestIntent;
	
	@Transient
	private Float amountSellIntent;
	
	@Transient
	private Float amountDepositIntent;
	
	@Transient
	private Float amountDebitIntent;
	
	@Transient
	private Float amountTransferToAccountIntent;
	
	@Transient
	private Float amountTransferToDepotIntent;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Float getAmountAccount() {
		return amountAccount;
	}

	public void setAmountAccount(Float amountAccount) {
			this.amountAccount = amountAccount;
	}

	public Float getAmountDepot() {
		return amountDepot;
	}

	public void setAmountDepot(Float amountDepot) {
		this.amountDepot = amountDepot;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Float getAmountAccountIntent() {
		return amountAccountIntent;
	}

	public void setAmountAccountIntent(Float amountAccountIntent) {
		this.amountAccountIntent = amountAccountIntent;
	}

	public Float getAmountDepotIntent() {
		return amountDepotIntent;
	}

	public void setAmountDepotIntent(Float amountDepotIntent) {
		this.amountDepotIntent = amountDepotIntent;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Transient
	public Float getAmountDisposable() {
		return getAmountAccount() - getAmountDepot() - Math.abs(getAmountDepotIntent());
	}

	public Float getAmountInvestIntent() {
		return amountInvestIntent;
	}

	public void setAmountInvestIntent(Float amountInvestIntent) {
		this.amountInvestIntent = amountInvestIntent;
	}

	public Float getAmountDepositIntent() {
		return amountDepositIntent;
	}

	public void setAmountDepositIntent(Float amountDepositIntent) {
		this.amountDepositIntent = amountDepositIntent;
	}

	public Float getAmountDebitIntent() {
		return amountDebitIntent;
	}

	public void setAmountDebitIntent(Float amountDebitIntent) {
		this.amountDebitIntent = amountDebitIntent;
	}

	public Float getAmountSellIntent() {
		return amountSellIntent;
	}

	public void setAmountSellIntent(Float amountSellIntent) {
		this.amountSellIntent = amountSellIntent;
	}

	public void setAmountDisposable(Float amountDisposable) {
		this.amountDisposable = amountDisposable;
	}

	public Float getAmountTransferToAccountIntent() {
		return amountTransferToAccountIntent;
	}

	public void setAmountTransferToAccountIntent(Float amountTransferToAccountIntent) {
		this.amountTransferToAccountIntent = amountTransferToAccountIntent;
	}

	public Float getAmountTransferToDepotIntent() {
		return amountTransferToDepotIntent;
	}

	public void setAmountTransferToDepotIntent(Float amountTransferToDepotIntent) {
		this.amountTransferToDepotIntent = amountTransferToDepotIntent;
	}
}
