package com.dam.portfolio.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import org.springframework.stereotype.Component;


@Entity
@Component
@Table(name = "MapAssetClassToPortfolio", uniqueConstraints= {@UniqueConstraint(columnNames = {"assetClassId", "portfolioId"})})
public class AssetClassToPortfolioMap {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long mapId;
	
	@Column(nullable=false)
	private Long assetClassId;
	
	@Column(nullable=false)
	private Long portfolioId;
	
	@Column(nullable= false)
	@Max(100)
	private Float assetWeighting;
	

	public AssetClassToPortfolioMap () {	
	}
	
	public AssetClassToPortfolioMap updateEntity (AssetClassToPortfolioMap container) {
		setAssetClassId(container.getAssetClassId());
		setPortfolioId(container.getPortfolioId());
		return this;
	}


	public Long getAssetClassId() {
		return assetClassId;
	}

	public void setAssetClassId(Long assetClassId) {
		this.assetClassId = assetClassId;
	}

	public Long getMapId() {
		return mapId;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}

	public Long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(Long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public Float getAssetWeighting() {
		return assetWeighting;
	}

	public void setAssetWeighting(Float assetWeighting) {
		this.assetWeighting = assetWeighting;
	}

}
