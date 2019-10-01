package com.dam.jobService.messageClass;

import java.util.Date;

import com.dam.jobService.type.ActionType;
import com.dam.jobService.type.Currency;


public class Intent {
	private Long intentId;
	private Long userId;
	private Long requestorUserId;
	private Float amount;
	private Currency currency;
	private ActionType action;
	private Date actionDate;
	private Boolean booked = false;
	private Boolean accepted;
	private Date bookingDate;
	private String finishResponse;
	private Long referenceId;
	
	public Long getIntentId() {
		return intentId;
	}
	public void setIntentId(Long intentId) {
		this.intentId = intentId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRequestorUserId() {
		return requestorUserId;
	}
	public void setRequestorUserId(Long requestorUserId) {
		this.requestorUserId = requestorUserId;
	}
	public Float getAmount() {
		return amount;
	}
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
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
	public Boolean getBooked() {
		return booked;
	}
	public void setBooked(Boolean booked) {
		this.booked = booked;
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
	public Long getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

}
