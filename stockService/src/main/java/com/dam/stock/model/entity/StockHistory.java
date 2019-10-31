package com.dam.stock.model.entity;

import java.time.LocalDate;
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

import com.dam.stock.type.Symbol;

@Entity
@Component
@Table(name = "StockHistory", uniqueConstraints= {@UniqueConstraint(columnNames = {"historyDate", "symbol"})}
)
public class StockHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long stockHistoryId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Symbol symbol;

	@Column(nullable = false)
	private String wkn;
	
	@Column(nullable = false)
	private Float open;
	
	@Column(nullable = false)
	private Float close;
	
	@Column(nullable = false)
	private LocalDate historyDate;
	
	public StockHistory updateEntity (StockHistory container) {
		setSymbol(container.getSymbol());
		setWkn(container.getWkn());
		setOpen(container.getOpen());
		setClose(container.getClose());
		setHistoryDate(container.getHistoryDate());
		return this;
	}

	public Long getStockHistoryId() {
		return stockHistoryId;
	}

	public void setStockHistoryId(Long stockHistoryId) {
		this.stockHistoryId = stockHistoryId;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public Float getOpen() {
		return open;
	}

	public void setOpen(Float open) {
		this.open = open;
	}

	public Float getClose() {
		return close;
	}

	public void setClose(Float close) {
		this.close = close;
	}

	public LocalDate getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(LocalDate historyDate) {
		this.historyDate = historyDate;
	}

}
