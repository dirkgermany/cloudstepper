package com.dam.depot.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.stereotype.Component;

import com.dam.depot.types.ActionType;
import com.dam.depot.types.Currency;
import com.dam.depot.types.ReferenceType;

/**
 * This entity stores the call money. Regulary payments with the debit card
 * should be booked from this account. Transfers to the depot at first are
 * stored to this account and later from here to the depot.
 * 
 */

@Entity
@Component
@Table(name = "Account", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "userId", "action", "amount", "actionDate" }) }, indexes = {
				@Index(name = "idx_account_user_action", columnList = "userId, action"),
				@Index(name = "idx_account_user_date", columnList = "userId, actionDate"),
				@Index(name = "idx_account_user", columnList = "userId"),
				@Index(name = "idx_account_user_amount", columnList = "userId, amount") })
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long accountId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = true)
	private Long requestorUserId;

	@Column(nullable = false)
	private Date actionDate;

	@Column
	@Enumerated(EnumType.STRING)
	private ActionType action;

	@Column
	private String eventText;

	@Column(nullable = false)
	private Float amount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;
	
	@Column(nullable = true)
	private Long referenceId;
	
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private ReferenceType referenceType;
	

	public Account updateEntity(Account container) {
		this.action = container.getAction();
		this.actionDate = container.getActionDate();
		this.amount = container.getAmount();
		this.eventText = container.getEventText();
		this.requestorUserId = container.getRequestorUserId();
		this.userId = container.getUserId();
		this.currency = container.getCurrency();
		this.referenceId = container.getReferenceId();
		this.referenceType = container.getReferenceType();
		return this;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Long getRequestorUserId() {
		return requestorUserId;
	}

	public void setRequestorUserId(Long requestorUserId) {
		this.requestorUserId = requestorUserId;
	}

	public String getEventText() {
		return eventText;
	}

	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}
}
