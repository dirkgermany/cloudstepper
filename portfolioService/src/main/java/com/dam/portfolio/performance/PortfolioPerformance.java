package com.dam.portfolio.performance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.validation.constraints.Max;

import com.dam.portfolio.types.AssetClassType;

public class PortfolioPerformance extends Performance {
	
	private Long portfolioId;
	private String portfolioName;
	private Float callMoneyPct;
	private Float goldPct;
	private Float loanPct;
	private Float etfPct;

	@Column(nullable=false)
	@Max(100)
	private Float sharePct;
	
	
	private Map<AssetClassType, ClassTypeValues> ClassTypeValuesMap = new HashMap<>();
	
	public Long getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	
	public Map<AssetClassType, ClassTypeValues> getClassTypeValuesMap() {
		return ClassTypeValuesMap;
	}
	public void setClassTypeValuesMap(Map<AssetClassType, ClassTypeValues> classTypeValuesMap) {
		ClassTypeValuesMap = classTypeValuesMap;
	}
	public Float getCallMoneyPct() {
		return callMoneyPct;
	}
	public void setCallMoneyPct(Float callMoneyPct) {
		this.callMoneyPct = callMoneyPct;
	}
	public Float getGoldPct() {
		return goldPct;
	}
	public void setGoldPct(Float goldPct) {
		this.goldPct = goldPct;
	}
	public Float getLoanPct() {
		return loanPct;
	}
	public void setLoanPct(Float loanPct) {
		this.loanPct = loanPct;
	}
	public Float getEtfPct() {
		return etfPct;
	}
	public void setEtfPct(Float etfPct) {
		this.etfPct = etfPct;
	}
	public Float getSharePct() {
		return sharePct;
	}
	public void setSharePct(Float sharePct) {
		this.sharePct = sharePct;
	}

}
