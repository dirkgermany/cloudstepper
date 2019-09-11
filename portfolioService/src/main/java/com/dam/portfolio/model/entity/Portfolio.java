package com.dam.portfolio.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.dam.portfolio.types.Risc;

@Entity
@Component
@Table(name = "Portfolio")
public class Portfolio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long portfolioId;
	
	@Column(nullable=false, unique=true)
	private String portfolioName;
	
	@Column
	@Lob
	private String description;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Risc risc;
	
	@Column(nullable=false)
	private Float callMoneyPct;

	@Column(nullable=false)
	private Float goldPct;
	
	@Column(nullable=false)
	private Float loanPct;
	
	@Column(nullable=false)
	private Float etfPct;

	@Column(nullable=false)
	private Float sharePct;

	@Column(nullable=true)
	private Date validFrom;

	@Column(nullable=true)
	private Date validUntil;

	public Portfolio () {	
	}
	
	public Portfolio (Risc risc, Float callMoneyPct, Float goldPct, Float loanPct, Float etfPct, Float sharePct, Date validFrom, Date validUntil) {
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public Risc getRisc() {
		return risc;
	}

	public void setRisc(Risc risc) {
		this.risc = risc;
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

	public Float getSharePct() {
		return sharePct;
	}

	public void setSharePct(Float sharePct) {
		this.sharePct = sharePct;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}
	
	public Portfolio updateEntity (Portfolio container) {
		setPortfolioName(container.getPortfolioName());
		setDescription(container.getDescription());
		setCallMoneyPct(container.getCallMoneyPct());
		setGoldPct(container.getGoldPct());
		setLoanPct(container.getLoanPct());
		setEtfPct(container.getEtfPct());
		setRisc(container.getRisc());
		setSharePct(container.getSharePct());
		setValidFrom(container.getValidFrom());
		setValidUntil(container.getValidUntil());
		
		return this;
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getEtfPct() {
		return etfPct;
	}

	public void setEtfPct(Float etfPct) {
		this.etfPct = etfPct;
	}
	
}
