package com.dam.depot.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.dam.depot.model.DepotModel;
import com.dam.depot.model.entity.Depot;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class DepotStore {

	@Autowired
	private DepotModel depotModel;

	public long count() {
		return depotModel.count();
	}

	public Depot getDepotByUserId(Long userId) {
		return depotModel.findByUserId(userId);
	}
	
	public Depot saveDepot(Depot depot) throws DamServiceException {
		return depotModel.save(depot);
	}
	
	public Depot getDepotByUserPortfolio(Long userId, Long portfolioId) {
		return depotModel.findByUserPortfolio(userId, portfolioId);
	}
}
