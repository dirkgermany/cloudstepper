package com.dam.depot.model.entity;

import java.time.LocalDateTime;
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
	private Float amountAccount = 0F;

	@Column(nullable = true)
	private Float amountInvest = 0F;
	
	// whole money in depot
//	@Transient
//	private Float amountDepot = 0F;
	
	// free call money
//	@Transient
//	private Float amountDisposable = 0F;
	
	@Column(nullable = true)
	private Float amountAccountIntent = 0F;
	
	@Column(nullable = true)
	private Float amountDepotIntent = 0F;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;
	
	@Column(nullable = true)
	private LocalDateTime lastUpdate;
	
	@Transient
	private Float amountInvestIntent = 0F;
	
	@Transient
	private Float amountSellIntent = 0F;
	
	@Transient
	private Float amountDepositIntent = 0F;
	
	@Transient
	private Float amountDebitIntent = 0F;
	
	@Transient
	private Float amountTransferToAccountIntent = 0F;
	
	@Transient
	private Float amountTransferToDepotIntent = 0F;

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

	public Float getAmountInvest() {
		return amountInvest;
	}

	public void setAmountInvest(Float amountInvest) {
		this.amountInvest = amountInvest;
	}

	public Float getAmountDepot() {
		return amountInvest + amountAccount;
	}

	public Float getAmountDisposable() {
		Float dispo = getAmountAccount() - getAmountInvest() - Math.abs(getAmountDepotIntent());
		return dispo < 0? 0f : dispo;
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
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
