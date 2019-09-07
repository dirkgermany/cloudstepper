package com.dam.portfolio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.exception.EntityNotFoundException;
import com.dam.exception.PermissionCheckException;
import com.dam.portfolio.model.AssetClassModel;
import com.dam.portfolio.model.AssetClassToPortfolioMapModel;
import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;
import com.dam.portfolio.rest.message.RestRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassCreateRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassDropRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassUpdateRequest;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class AssetClassToPortfolioMapStore {

	@Autowired
	private AssetClassToPortfolioMapModel mapModel;
	
	public void dropMapEntriesByConstructionId(Long constructionId) {
		mapModel.deleteAllMapEntriesByConstructionId(constructionId);
	}

	public void dropMapEntriesByAssetClassId(Long assetClassId) {
		mapModel.deleteAllMapEntriesByConstructionId(assetClassId);
	}
	
	public AssetClassToPortfolioMap findMapByAssetClassIdAndPortfolioId(Long assetClassId,Long constructionId) throws EntityNotFoundException  {
		AssetClassToPortfolioMap mapEntry = mapModel.findMapByAssetClassIdAndPortfolioId(assetClassId, constructionId);
			if (null == mapEntry) {
			throw new EntityNotFoundException(new Long(500), "Map Entry not found", "Unknown Map Entry");
		}
		return mapEntry;
	}


	/**
	 * 
	 * @return
	 */
	public AssetClassToPortfolioMap getMapById(Long mapId) throws EntityNotFoundException  {
		Optional<AssetClassToPortfolioMap> map = mapModel.findById(mapId);
		if (null != map && map.isPresent()) {
			return map.get();
		}
		throw new EntityNotFoundException(new Long(500), "Map Entry not found", "Unknown Map Entry");
	}
	
	public List<AssetClassToPortfolioMap> getMapsByAssetId(Long assetId) {
		return mapModel.findAllMapsByAssetId(assetId);
	}

	public List<AssetClassToPortfolioMap> getMapsByPortfolioId(Long portfolioId) {
		return mapModel.findAllMapsByPortfolioId(portfolioId);
	}

	/**
	 */
	public AssetClassToPortfolioMap createMapEntry (AssetClassToPortfolioMap mapEntry) throws DamServiceException {
		
		if (null != mapEntry && null != mapEntry.getAssetClassId() && null != mapEntry.getPortfolioId()) {
			try {
				return mapModel.save(mapEntry);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409),
						"MapEntry could not be saved.", e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "MapEntry could not be saved",
				"Check MapEntry data in request.");
	}

	public Long dropMapEntry(AssetClassToPortfolioMap mapEntry)  throws DamServiceException{
		if (null != mapEntry) {
			mapModel.deleteById(mapEntry.getPortfolioId());
			AssetClassToPortfolioMap deletedAssetClass = getMapById(mapEntry.getPortfolioId());
			if (null == deletedAssetClass) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

}
