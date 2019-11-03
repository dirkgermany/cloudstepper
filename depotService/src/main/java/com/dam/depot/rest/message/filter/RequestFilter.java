package com.dam.depot.rest.message.filter;

import java.time.LocalDateTime;

public class RequestFilter {
	
	private Float amountFrom;
	private Float amountUntil;
	private LocalDateTime dateFrom;
	private LocalDateTime dateUntil;
	private String eventText;
	
	public Boolean isFiltered() {
		return (null != amountFrom && null != amountUntil && null != dateFrom && null != dateUntil && null != eventText);
	}
	
	public Float getAmountFrom() {
		return amountFrom;
	}
	public void setAmountFrom(Float amountFrom) {
		this.amountFrom = amountFrom;
	}
	public Float getAmountUntil() {
		return amountUntil;
	}
	public void setAmountUntil(Float amountUntil) {
		this.amountUntil = amountUntil;
	}
	public LocalDateTime getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(LocalDateTime dateFrom) {
		this.dateFrom = dateFrom;
	}
	public LocalDateTime getDateUntil() {
		return dateUntil;
	}
	public void setDateUntil(LocalDateTime dateUntil) {
		this.dateUntil = dateUntil;
	}
	public String getEventText() {
		return eventText;
	}
	public void setEventText(String eventText) {
		this.eventText = eventText;
	}

}
