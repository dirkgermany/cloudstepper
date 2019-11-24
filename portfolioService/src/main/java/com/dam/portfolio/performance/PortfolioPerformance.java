package com.dam.portfolio.performance;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.Precision;

import com.dam.portfolio.types.AssetClassType;

public class PortfolioPerformance extends Performance {
	
	private Long portfolioId;
	private String portfolioName;
	private Float callMoneyPct;
	private Float goldPct;
	private Float loanPct;
	private Float etfPct;

//	@Column(nullable=false)
//	@Max(100)
	private Float sharePct;
	
	private Float openWeighted;
	private Float closeWeighted;

	
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
	public Float getOpenWeighted() {
		return openWeighted;
	}
	public void setOpenWeighted(Float openWeighted) {
		this.openWeighted = openWeighted;
	}
	public Float getCloseWeighted() {
		return closeWeighted;
	}
	public void setCloseWeighted(Float closeWeighted) {
		this.closeWeighted = closeWeighted;
	}

	public void addToOpenWeighted(Float value) {
		if (null == this.openWeighted) {
			this.openWeighted = 0F;
		}
		this.openWeighted += value;
	}

	public void addToCloseWeighted(Float value) {
		if (null == this.closeWeighted) {
			this.closeWeighted = 0F;
		}
		this.closeWeighted += value;
	}

	public Float getPerformancePercent() {
		if (null == this.openWeighted || null == this.closeWeighted) {
			return 0F;
		}
		
		float calculated = ((this.closeWeighted/this.openWeighted - 1) * 100);
		return Precision.round(calculated, 6);
	}
	
	public String getPerformanceAsString() {
		DecimalFormat formatter = new DecimalFormat("#,##0.00'%'");
		formatter.setMultiplier(1);

		return formatter.format(getPerformancePercent());
	}
}
