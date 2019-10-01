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
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.dam.depot.types.Currency;

@Entity
@Component
@Table(name = "Depot", uniqueConstraints= {@UniqueConstraint(columnNames = {"userId", "portfolioId"})}
)
public class Depot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long depotId;

	@Column(nullable = false)
	private Long userId;
	
	@Column(nullable = false)
	private Long portfolioId;

	@Column(nullable = false)
	private Float investValue;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Currency currency = Currency.EUR;
	
	@Column(nullable = true)
	private Date lastUpdate;

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

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getDepotId() {
		return depotId;
	}

	public void setDepotId(Long depotId) {
		this.depotId = depotId;
	}

	public Float getInvestValue() {
		return investValue;
	}

	public void setInvestValue(Float investValue) {
		this.investValue = investValue;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}
}
