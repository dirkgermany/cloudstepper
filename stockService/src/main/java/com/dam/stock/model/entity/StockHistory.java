package com.dam.stock.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "StockHistory", uniqueConstraints= {@UniqueConstraint(columnNames = {"historyDate"}), @UniqueConstraint(columnNames = {"historyDate", "symbol"}), @UniqueConstraint(columnNames = {"symbol"}), @UniqueConstraint(columnNames = {"wkn"})}
)
public class StockHistory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long stockHistoryId;

	@Column(nullable = false)
	private String symbol;

	@Column(nullable = false)
	private String wkn;
	
	@Column(nullable = false)
	private Float open;
	
	@Column(nullable = false)
	private Float close;
	
	@Column(nullable = false)
	private Date historyDate;

	public Long getStockHistoryId() {
		return stockHistoryId;
	}

	public void setStockHistoryId(Long stockHistoryId) {
		this.stockHistoryId = stockHistoryId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
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

	public Date getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(Date historyDate) {
		this.historyDate = historyDate;
	}

}
