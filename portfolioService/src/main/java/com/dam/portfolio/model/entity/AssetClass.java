package com.dam.portfolio.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dam.portfolio.types.AssetClassType;

import org.springframework.stereotype.Component;


@Entity
@Component
@Table(name = "AssetClass")
public class AssetClass {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long assetClassId;
	
	@Column(nullable=false)
	private String assetClassName;
	
	@Column
	private Long providerId;
	
	@Column
	private String providerName;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private AssetClassType assetClassType;
	
	@Column
	@Lob
	private String description;
	
	@Column (nullable = true)
	private String wkn;
	
	@Column (nullable = true)
	private String symbol;
	
	@Column (nullable = true)
	private String link;
	
	@Transient
	private Float portfolioWeighting;

	public AssetClass () {	
	}
	
	public AssetClass updateEntity (AssetClass container) {
		setDescription(container.getDescription());
		setAssetClassName(container.getAssetClassName());
		setAssetClassType(container.getAssetClassType());
		setProviderId(container.getProviderId());
		setProviderName(container.getProviderName());
		setWkn(container.getWkn());
		setLink(container.getLink());
		
		return this;
	}

	public String getAssetClassName() {
		return assetClassName;
	}

	public void setAssetClassName(String assetClassName) {
		this.assetClassName = assetClassName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public AssetClassType getAssetClassType() {
		return assetClassType;
	}

	public void setAssetClassType(AssetClassType assetClassType) {
		this.assetClassType = assetClassType;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Float getPortfolioWeighting() {
		return portfolioWeighting;
	}

	public void setPortfolioWeighting(Float portfolioWeighting) {
		this.portfolioWeighting = portfolioWeighting;
	}
	
}
