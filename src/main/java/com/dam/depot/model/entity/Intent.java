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

import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import com.dam.depot.types.ActionType;
import com.dam.depot.types.Currency;

@Entity
@Component
@Table(name = "Intent")
public class Intent {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long intentId;
	
	@Column(nullable=false)
	private Long userId;
	
	@Column(nullable=true)
	private Long requestorUserId;

	@Column(nullable = false)
	private Float amount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ActionType action;
	
	@Column(nullable = false)
	private Date actionDate = new Date();
	
	@Column(nullable = true)
	@Type (type="yes_no")
	private Boolean booked = false;
	
	/**
	 * When booked the request must be accepted or declined at the same time
	 */
	@Column(nullable = true)
	@Type (type="yes_no")
	private Boolean accepted;
	
	/**
	 * The date when the record was booked
	 */
	@Column(nullable = true)
	private Date bookingDate;
	
	/**
	 * External API response. Especially when the request was declined here the reason should be explained
	 */
	@Column(nullable = true)
	private String finishResponse;
	
	/*
	 * Reference to the precedessor Intent
	 */
	@Column(nullable = true)
	private Long referenceId;
	
	@Column(nullable = true)
	private Long portfolioId;
	
	public void setIntent (Intent container) {
		this.action = container.getAction();
		this.actionDate = container.getActionDate();
		this.amount = container.getAmount();
		this.requestorUserId = container.getRequestorUserId();
		this.userId = container.getUserId();
		this.currency = container.getCurrency();
		this.accepted = container.getAccepted();
		this.booked = container.getBooked();
		this.bookingDate = container.getBookingDate();
		this.finishResponse = container.getFinishResponse();
		this.referenceId = container.getReferenceId();
		this.portfolioId = container.getPortfolioId();
	}
	
	public Intent updateEntity(Intent container) {
		this.action = container.getAction();
		this.actionDate = container.getActionDate();
		this.amount = container.getAmount();
		this.requestorUserId = container.getRequestorUserId();
		this.userId = container.getUserId();
		this.currency = container.getCurrency();
		this.accepted = container.getAccepted();
		this.booked = container.getBooked();
		this.bookingDate = container.getBookingDate();
		this.finishResponse = container.getFinishResponse();
		this.portfolioId = container.getPortfolioId();
		return this;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getFinishResponse() {
		return finishResponse;
	}

	public void setFinishResponse(String finishResponse) {
		this.finishResponse = finishResponse;
	}

	public Long getIntentId() {
		return intentId;
	}

	public void setIntentId(Long intentId) {
		this.intentId = intentId;
	}


	public Long getRequestorUserId() {
		return requestorUserId;
	}


	public void setRequestorUserId(Long requestorUserId) {
		this.requestorUserId = requestorUserId;
	}

	public Boolean getBooked() {
		return booked;
	}

	public void setBooked(Boolean booked) {
		this.booked = booked;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
}
