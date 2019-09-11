package com.dam.portfolio;

import java.util.Iterator;
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
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassCreateRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassDropRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassRequest;
import com.dam.portfolio.rest.message.assetClass.AssetClassUpdateRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.GetAssetClassesToPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.GetAssetClassesToPortfolioMapResponse;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.RemoveAssetClassesFromPortfolioMapRequest;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.RemoveAssetClassesFromPortfolioMapResponse;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class AssetClassToPortfolioMapStore {

	@Autowired
	private AssetClassToPortfolioMapModel mapModel;

	@Autowired
	private PortfolioStore portfolioStore;

	@Autowired
	private AssetClassStore assetClassStore;
	
	public long count() {
		return mapModel.count();
	}

	public void dropMapEntriesByConstructionId(Long constructionId) {
		mapModel.deleteAllMapEntriesByConstructionId(constructionId);
	}

	public void dropMapEntriesByAssetClassId(Long assetClassId) {
		mapModel.deleteAllMapEntriesByConstructionId(assetClassId);
	}

	/**
	 * Returns complete Map with Portfolio and assigned AssetClasses
	 * @param getRequest
	 * @return
	 * @throws DamServiceException
	 */
	public GetAssetClassesToPortfolioMapResponse getMapPortfolioSafe(GetAssetClassesToPortfolioMapRequest getRequest)
			throws DamServiceException {
		PermissionCheck.checkRequestedParams(getRequest, getRequest.getRequestorUserId(), getRequest.getRights());
		PermissionCheck.checkRequestedEntity(getRequest.getPortfolioId(), Long.class, "Portfolio Id");
		PermissionCheck.isReadPermissionSetInGeneral(getRequest.getRights());
		
		return new GetAssetClassesToPortfolioMapResponse(getConstructionMap(getRequest.getPortfolioId()));
	}

	public AddAssetClassesToPortfolioMapResponse addAssetClassesToPortfolioSafe(AddAssetClassesToPortfolioMapRequest addRequest)
			throws DamServiceException {

		PermissionCheck.checkRequestedParams(addRequest, addRequest.getRequestorUserId(), addRequest.getRights());
		PermissionCheck.checkRequestedEntity(addRequest.getAssetClassIds(), List.class, "Asset Class Ids");
		PermissionCheck.isWritePermissionSetInGeneral(addRequest.getRights());

		if (addRequest.getAssetClassIds().isEmpty() || null == addRequest.getPortfolioId()) {
			throw new DamServiceException(500L, "Keine Zuordnung Asset Klassen zu Portfolio möglich.",
					"Request Parameter nicht vollständig.");
		}

		Portfolio portfolio = portfolioStore.getPortfolioById(addRequest.getPortfolioId());
		if (null == portfolio) {
			throw new DamServiceException(400L, "Fehler bei Hinzufügen von Assetklassen an Portfolio",
					"Portfolio mit angegebener Id existiert nicht");
		}

		ConstructionMap constructionMap = new ConstructionMap();
		constructionMap.setPortfolio(portfolio);

		Iterator<Long> it = addRequest.getAssetClassIds().iterator();
		while (it.hasNext()) {
			Long assetClassId = it.next();
			AssetClass assetClass = assetClassStore.getAssetClassById(assetClassId);
			if (null == assetClass) {
				throw new DamServiceException(400L, "Fehler bei Hinzufügen von Assetklasse an Portfolio",
						"Asset Klasse mit Id " + assetClassId + " existiert nicht");
			}

			AssetClassToPortfolioMap mapEntry = new AssetClassToPortfolioMap();
			mapEntry.setAssetClassId(assetClassId);
			mapEntry.setPortfolioId(addRequest.getPortfolioId());
			createMapEntry(mapEntry);
			constructionMap.addAssetClass(assetClass);
		}
		return new AddAssetClassesToPortfolioMapResponse(constructionMap);
	}

	public RemoveAssetClassesFromPortfolioMapResponse removeAssetClassesFromPortfolioSafe(RemoveAssetClassesFromPortfolioMapRequest removeRequest)
			throws DamServiceException {

		PermissionCheck.checkRequestedParams(removeRequest, removeRequest.getRequestorUserId(), removeRequest.getRights());
		PermissionCheck.checkRequestedEntity(removeRequest.getAssetClassIds(), List.class, "Asset Class Ids");
		PermissionCheck.isWritePermissionSetInGeneral(removeRequest.getRights());

		if (removeRequest.getAssetClassIds().isEmpty() || null == removeRequest.getPortfolioId()) {
			throw new DamServiceException(500L, "Keine Zuordnung Asset Klassen zu Portfolio möglich.",
					"Request Parameter nicht vollständig.");
		}

		Portfolio portfolio = portfolioStore.getPortfolioById(removeRequest.getPortfolioId());
		if (null == portfolio) {
			throw new DamServiceException(400L, "Fehler bei Hinzufügen von Assetklassen an Portfolio",
					"Portfolio mit angegebener Id existiert nicht");
		}

		AssetClassToPortfolioMap map = null;
		
		Iterator<Long> it = removeRequest.getAssetClassIds().iterator();
		while (it.hasNext()) {
			Long assetClassId = it.next();
			map = new AssetClassToPortfolioMap();
			map.setAssetClassId(assetClassId);
			map.setPortfolioId(removeRequest.getPortfolioId());
			dropMapEntry(map);
		}
		return new RemoveAssetClassesFromPortfolioMapResponse(getConstructionMap(removeRequest.getPortfolioId()));
	}

	/**
	 * 
	 * @return
	 */
	public AssetClassToPortfolioMap getMapById(Long mapId) throws EntityNotFoundException {
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
	public AssetClassToPortfolioMap createMapEntry(AssetClassToPortfolioMap mapEntry) throws DamServiceException {

		if (null != mapEntry && null != mapEntry.getAssetClassId() && null != mapEntry.getPortfolioId()) {
			try {
				return mapModel.save(mapEntry);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "MapEntry could not be saved.", e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "MapEntry could not be saved", "Check MapEntry data in request.");
	}

	public Long dropMapEntry(AssetClassToPortfolioMap mapEntry) throws DamServiceException {
		if (null != mapEntry) {
			mapModel.deleteById(mapEntry.getPortfolioId());
			AssetClassToPortfolioMap deletedAssetClass = getMapById(mapEntry.getPortfolioId());
			if (null == deletedAssetClass) {
				return new Long(200);
			}
		}

		return new Long(10);
	}
	
	private ConstructionMap getConstructionMap(Long portfolioId)  throws DamServiceException {
		List<AssetClassToPortfolioMap> mapEntries = mapModel.findAllMapsByPortfolioId(portfolioId);
		if (null == mapEntries || mapEntries.isEmpty()) {
			throw new DamServiceException(new Long(500), "No Map Entry found for Portfolio", "List seems to be empty");
		}
		
		Portfolio portfolio = (portfolioStore.getPortfolioById(portfolioId));
		if (null == portfolio) {
			throw new DamServiceException(400L, "Couldn't find Portfolio with id",
					"Perhaps another call deleted the Portfolio or data is not consistent");
		}
		
		ConstructionMap portfolioMap = new ConstructionMap();
		portfolioMap.setPortfolio(portfolioStore.getPortfolioById(portfolioId));

		Iterator<AssetClassToPortfolioMap> it = mapEntries.iterator();
		while (it.hasNext()) {
			Long id = it.next().getAssetClassId();
			AssetClass assetClass = assetClassStore.getAssetClassById(id);
			portfolioMap.getAssetClasses().add(assetClass);
		}
		
		return portfolioMap;
	}

}
