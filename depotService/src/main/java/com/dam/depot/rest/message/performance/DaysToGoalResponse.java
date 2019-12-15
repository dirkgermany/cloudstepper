package com.dam.depot.rest.message.performance;

import java.time.LocalDate;
import com.dam.depot.rest.message.RestResponse;

public class DaysToGoalResponse extends RestResponse{

	private Integer daysToGoal;
	private LocalDate dateOfGoal;
	private Float investment;
	private Float returnOfInvest;
	private Float totalSaved;
	
	public DaysToGoalResponse(Long result, String shortStatus, String longStatus, Integer daysToGoal, LocalDate dateOfGoal, Float investment, Float returnOfInvest, Float totalSaved) {
		super(result, shortStatus, longStatus);
		setDaysToGoal(daysToGoal);
		setDateOfGoal(dateOfGoal);
		setInvestment(investment);
		setReturnOfInvest(returnOfInvest);
		setTotalSaved(totalSaved);
	}

	public Integer getDaysToGoal() {
		return daysToGoal;
	}

	public void setDaysToGoal(Integer daysToGoal) {
		this.daysToGoal = daysToGoal;
	}

	public LocalDate getDateOfGoal() {
		return dateOfGoal;
	}

	public void setDateOfGoal(LocalDate dateOfGoal) {
		this.dateOfGoal = dateOfGoal;
	}

	public Float getInvestment() {
		return investment;
	}

	public void setInvestment(Float investment) {
		this.investment = investment;
	}

	public Float getReturnOfInvest() {
		return returnOfInvest;
	}

	public void setReturnOfInvest(Float returnOfInvest) {
		this.returnOfInvest = returnOfInvest;
	}

	public Float getTotalSaved() {
		return totalSaved;
	}

	public void setTotalSaved(Float totalSaved) {
		this.totalSaved = totalSaved;
	}
}
