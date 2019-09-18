package com.dam.portfolio.model;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.types.AssetClassType;

@Transactional
public interface AssetClassModel extends Repository<AssetClass, Long>, CrudRepository<AssetClass, Long> {
	
	AssetClass findByAssetClassName(String assetClassName);
	
	@Query("SELECT assetClass FROM AssetClass assetClass where assetClass.assetClassType = :assetClassType")
	List<AssetClass> listAssetClassByType(@Param("assetClassType") AssetClassType assetClassType);

	
}
