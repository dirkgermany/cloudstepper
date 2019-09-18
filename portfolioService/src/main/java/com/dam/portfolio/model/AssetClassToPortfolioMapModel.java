package com.dam.portfolio.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;

@Transactional
public interface AssetClassToPortfolioMapModel
		extends Repository<AssetClassToPortfolioMap, Long>,
		CrudRepository<AssetClassToPortfolioMap, Long> {

	@Query("SELECT mapEntry FROM AssetClassToPortfolioMap mapEntry where mapEntry.assetClassId = :assetClassId "
			+ "AND mapEntry.portfolioId = :portfolioId")
	AssetClassToPortfolioMap findMapByAssetClassIdAndPortfolioId(@Param("assetClassId") Long assetClassId,
			@Param("portfolioId") Long portfolioId);

	@Query("SELECT mapEntry FROM AssetClassToPortfolioMap mapEntry where mapEntry.assetClassId = :assetClassId ")
	List<AssetClassToPortfolioMap> findAllMapsByAssetId(@Param("assetClassId") Long assetClassId);

	@Query("SELECT mapEntry FROM AssetClassToPortfolioMap mapEntry where mapEntry.portfolioId = :portfolioId ")
	List<AssetClassToPortfolioMap> findAllMapsByPortfolioId(@Param("portfolioId") Long portfolioId);

	@Modifying
	@Query("DELETE from AssetClassToPortfolioMap WHERE assetClassId = :assetClassId")
	void deleteAllMapEntriesByAssetClassId(@Param("assetClassId") Long assetClassId);
	
	@Modifying
	@Query("DELETE from AssetClassToPortfolioMap WHERE portfolioId = :portfolioId")
	void deleteAllMapEntriesByPortfolioId(@Param("portfolioId") Long portfolioId);
	
	@Modifying
	@Query ("DELETE from AssetClassToPortfolioMap WHERE assetClassId = :assetClassId " +
	        " AND portfolioId = :portfolioId")
	void deleteByAssetClassIdAndPortfolioId(@Param("assetClassId") Long assetClassId, @Param("portfolioId") Long portfolioId);

}
