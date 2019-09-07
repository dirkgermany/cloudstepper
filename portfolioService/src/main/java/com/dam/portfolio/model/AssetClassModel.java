package com.dam.portfolio.model;


import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import com.dam.portfolio.model.entity.AssetClass;

@Transactional
public interface AssetClassModel extends Repository<AssetClass, Long>, CrudRepository<AssetClass, Long> {
	
	AssetClass findByAssetClassName(String assetClassName);
	
}
