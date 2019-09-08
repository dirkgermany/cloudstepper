package com.dam.portfolio.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.AssetClassStore;
import com.dam.portfolio.AssetClassToPortfolioMapStore;
import com.dam.portfolio.PortfolioStore;
import com.dam.portfolio.model.entity.AssetClass;
import com.dam.portfolio.model.entity.AssetClassToPortfolioMap;
import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.mapAssetClassToPortfolio.AddAssetClassesToPortfolioMapRequest;

/**
 * Wird für die Gesamtkonstruktion der Portfolios benötigt. Stellt die
 * Konstrukte zusammen und liefert sie zurück. Greift auf die DB zu.
 * 
 * @author dirk
 *
 */
public class MappedConstructionManager {

	@Autowired
	PortfolioStore portfolioStore;

	@Autowired
	AssetClassStore assetClassStore;

	@Autowired
	AssetClassToPortfolioMapStore mapStore;
	
	public MappedConstructionManager () {
		
	}

	public ConstructionMap getMappedConstruction(Long portfolioId) throws DamServiceException {
		Portfolio construction = portfolioStore.getPortfolioById(portfolioId);
		if (null == construction) {
			throw new DamServiceException(new Long(500), "Fehler bei Ermittlung MappedConstruction",
					"MappedConstruction zur ID nicht gefunden.");
		}

		List<AssetClassToPortfolioMap> mapEntries = mapStore.getMapsByPortfolioId(portfolioId);
		if (null == mapEntries || mapEntries.isEmpty()) {
			throw new DamServiceException(new Long(500), "Fehler bei Ermittlung MappedConstruction",
					"Keine Mapping Einträge zur ID gefunden.");
		}

		List<AssetClass> assetClassList = new ArrayList<>();
		Iterator<AssetClassToPortfolioMap> it = mapEntries.iterator();
		while (it.hasNext()) {
			AssetClassToPortfolioMap map = it.next();
			assetClassList.add(assetClassStore.getAssetClassById(map.getAssetClassId()));
		}

		return new ConstructionMap(construction, assetClassList);
	}

	public ConstructionMap storeMappedConstruction(ConstructionMap constructionMap) throws DamServiceException {
		if (null == constructionMap || null == constructionMap.getPortfolio()
				|| null == constructionMap.getPortfolio().getPortfolioId()) {
			throw new DamServiceException(new Long(500), "Fehler bei Speichern einer MappedConstruction",
					"MappedConstruction ist null oder eines der Elemente ist null oder leer.");
		}

		if (null == constructionMap.getAssetClasses() || constructionMap.getAssetClasses().isEmpty()) {
			throw new DamServiceException(new Long(500), "Fehler bei Speichern einer MappedConstruction",
					"AssetClass Liste ist null oder leer.");
		}

		// entries still exist
		if (null != mapStore.getMapsByPortfolioId(constructionMap.getPortfolio().getPortfolioId())) {
			mapStore.dropMapEntriesByConstructionId(constructionMap.getPortfolio().getPortfolioId());
		}

		ConstructionMap storedMappedConstruction = new ConstructionMap();
		storedMappedConstruction.setPortfolio(constructionMap.getPortfolio());

		Integer counter = new Integer(0);
		Iterator<AssetClass> it = constructionMap.getAssetClasses().iterator();
		while (it.hasNext()) {
			AssetClassToPortfolioMap mapEntry = new AssetClassToPortfolioMap();
			AssetClass assetClass = it.next();
			Long assetClassId = assetClass.getAssetClassId();
			mapEntry.setAssetClassId(assetClassId);
			mapEntry.setPortfolioId(constructionMap.getPortfolio().getPortfolioId());

			storedMappedConstruction.addAssetClass(assetClass);
			mapStore.createMapEntry(mapEntry);
			counter++;
		}

		return storedMappedConstruction;
	}

}