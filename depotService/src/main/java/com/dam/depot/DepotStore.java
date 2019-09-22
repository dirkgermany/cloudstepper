package com.dam.depot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.depot.model.DepotModel;
import com.dam.depot.model.entity.Depot;
import com.dam.depot.rest.message.RestRequest;
import com.dam.depot.rest.message.depot.DepotCreateRequest;
import com.dam.depot.rest.message.depot.DepotDropRequest;
import com.dam.depot.rest.message.depot.DepotListRequest;
import com.dam.depot.rest.message.depot.DepotRequest;
import com.dam.depot.rest.message.depot.DepotUpdateRequest;
import com.dam.depot.types.ActionType;
import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;

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

	@Autowired

	public long count() {
		return depotModel.count();
	}

	/**
	 * Save getter for Depot. request
	 * 
	 * @param depot
	 * @return
	 */
	public Depot getDepotSafe(DepotRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepot().getDepotId()) {
			throw new DamServiceException(new Long(404), "Invalid request", "Depot ID not set in request.");
		}

		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		// Read attemptions
		Depot depot = getDepotById(depotRequest.getDepot().getDepotId());
		if (null == depot) {
			throw new DamServiceException(new Long(404), "Depot Unknown", "Depot not found or invalid request");
		}

		return depot;
	}

	/**
	 * Delivers a list of depot entries by filtering the params.
	 * 
	 * @param depotRequest
	 * @return
	 * @throws DamServiceException
	 */
	public List<Depot> getDepotListSafe(DepotListRequest depotRequest) throws DamServiceException {
		if (null == depotRequest.getDepot()) {
			throw new DamServiceException(500L, "Invalid request", "Depot is not set in request.");
		}
		PermissionCheck.checkRequestedParams(depotRequest, depotRequest.getRequestorUserId(), depotRequest.getRights());
		PermissionCheck.isReadPermissionSet(depotRequest.getRequestorUserId(), null, depotRequest.getRights());

		List<Depot> depotList = null;
		// search by sql and indexed fields
		if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateFrom() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateFromDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateFrom(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserActionDateFrom(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()
				&& null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserActionDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction(), depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getFilter().getDateFrom()) {
			depotList = getDepotListByUserDateFrom(depotRequest.getDepot().getUserId(),
					depotRequest.getFilter().getDateFrom());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getFilter().getDateUntil()) {
			depotList = getDepotListByUserDateUntil(depotRequest.getDepot().getUserId(),
					depotRequest.getFilter().getDateUntil());
		} else if (null != depotRequest.getDepot().getUserId() && null != depotRequest.getDepot().getAction()) {
			depotList = getDepotListByUserAction(depotRequest.getDepot().getUserId(),
					depotRequest.getDepot().getAction());
		} else if (null != depotRequest.getDepot().getUserId()) {
			depotList = getDepotListByUser(depotRequest.getDepot().getUserId());
		}

		if (null == depotList) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		// Filter
		if (null == depotRequest.getFilter() || !depotRequest.getFilter().isFiltered()) {
			return depotList;
		}

		List<Depot> filteredDepotList = new ArrayList<>();
		Iterator<Depot> it = depotList.iterator();
		while (it.hasNext()) {
			Depot depot = it.next();
			if (null != depotRequest.getFilter().getAmountFrom()
					&& depot.getAmount() < depotRequest.getFilter().getAmountFrom()) {
				continue;
			}
			if (null != depotRequest.getFilter().getAmountUntil()
					&& depot.getAmount() > depotRequest.getFilter().getAmountUntil()) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateFrom()
					&& depot.getActionDate().before(depotRequest.getFilter().getDateFrom())) {
				continue;
			}
			if (null != depotRequest.getFilter().getDateUntil()
					&& depot.getActionDate().after(depotRequest.getFilter().getDateUntil())) {
				continue;
			}
			if (null != depotRequest.getFilter().getEventText() && !depot.getEventText().toUpperCase()
					.contains(depotRequest.getFilter().getEventText().toUpperCase())) {
				continue;
			}

			// all conditions fullfilled
			filteredDepotList.add(depot);
		}

		if (filteredDepotList.isEmpty()) {
			throw new DamServiceException(500L, "No Depot Entry found by request", "Request: " + depotRequest);
		}

		return filteredDepotList;
	}

	/**
	 * Creation of depot requests existing userId, givenName and lastName. userId in
	 * Entity Container mustn't be null
	 * 
	 * @param depotContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public Depot createDepotSafe(DepotCreateRequest depotCreateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(depotCreateRequest, depotCreateRequest.getRequestorUserId(),
				depotCreateRequest.getRights());

		PermissionCheck.checkRequestedEntity(depotCreateRequest.getDepot(), Depot.class, "Depot Class");

		// Save database requests
		PermissionCheck.isWritePermissionSet(depotCreateRequest.getRequestorUserId(), null,
				depotCreateRequest.getRights());

		if (null == depotCreateRequest.getDepot().getAction() || null == depotCreateRequest.getDepot().getActionDate()
				|| null == depotCreateRequest.getDepot().getAmount()
				|| null == depotCreateRequest.getDepot().getRequestorUserId()
				|| null == depotCreateRequest.getDepot().getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Depot data not complete");
		}

		Depot depot;
		try {
			depot = depotModel.save(depotCreateRequest.getDepot());
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Depot could not be stored", e.getMessage());
		}

		if (null == depot) {
			throw new DamServiceException(new Long(422), "Depot not created",
					"Depot still exists, data invalid or not complete");
		}
		return depot;

	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public Depot updateDepotSafe(DepotUpdateRequest depotUpdateRequest) throws DamServiceException {
		PermissionCheck.checkRequestedParams(depotUpdateRequest, depotUpdateRequest.getRequestorUserId(),
				depotUpdateRequest.getRights());

		PermissionCheck.checkRequestedEntity(depotUpdateRequest.getDepot(), Depot.class, "Depot Class");

		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(depotUpdateRequest.getRequestorUserId(), null,
				depotUpdateRequest.getRights());

		// check if still exists
		Depot existingDepot = getDepotById(depotUpdateRequest.getDepot().getDepotId());

		// Depot must exist and userId ist not permutable
		if (null == existingDepot) {
			throw new DamServiceException(new Long(404), "Depot for update not found",
					"Depot with depotId doesn't exist.");
		}

		return updateDepot(existingDepot, depotUpdateRequest.getDepot());
	}

	/**
	 * Secure drop, user must be owner
	 * 
	 * @param userId
	 * @param depot
	 * @return
	 */
//	public Long dropDepotSafe(DepotDropRequest depotDropRequest) throws DamServiceException {
//		PermissionCheck.checkRequestedParams(depotDropRequest, depotDropRequest.getRequestorUserId(),
//				depotDropRequest.getRights());
//		PermissionCheck.checkRequestedEntity(depotDropRequest.getDepot(), Depot.class, "Depot Class");
//
//		// Save database requests
//		PermissionCheck.isDeletePermissionSet(depotDropRequest.getRequestorUserId(), null,
//				depotDropRequest.getRights());
//
//		Depot existingDepot = getDepotById(depotDropRequest.getDepot().getDepotId());
//
//		if (null == existingDepot) {
//			throw new DamServiceException(new Long(404), "Depot could not be dropped",
//					"Depot does not exist or could not be found in database.");
//		}
//
//		if (200 == dropDepot(existingDepot)) {
//			mapStore.dropMapEntriesByDepotId(depotDropRequest.getDepot().getDepotId());
//		}
//
//		return 200L;
//
//	}


	/**
	 * Lists all Depots.
	 * 
	 * @return
	 */
	public List<Depot> getDepotList() {
		List<Depot> depots = new ArrayList<>();
		Iterator<Depot> it = depotModel.findAll().iterator();
		while (it.hasNext()) {
			depots.add(it.next());
		}
		return depots;
	}
	
	private List<Depot> getDepotListByUserAction(Long userId, ActionType action) {
		return depotModel.findByUserAction(userId, action.name());
	}

	private List<Depot> getDepotListByUserDateFrom(Long userId, Date dateFrom) {
		return depotModel.findByUserDateFrom(userId, dateFrom);
	}

	private List<Depot> getDepotListByUserDateUntil(Long userId, Date dateUntil) {
		return depotModel.findByUserDateUntil(userId, dateUntil);
	}

	private List<Depot> getDepotListByUserActionDateFrom(Long userId, ActionType action, Date dateFrom) {
		List<Depot> depotList = new ArrayList<>();
		return prepareResultList(depotModel.findByUserActionDateFrom(userId, action, dateFrom));
	}

	private List<Depot> getDepotListByUserActionDateUntil(Long userId, ActionType action, Date dateUntil) {
		List<Depot> depotList = new ArrayList<>();
		return depotModel.findByUserActionDateUntil(userId, action, dateUntil);

		return depotList;
	}

	private List<Depot> getDepotListByUserActionDateFromDateUntil(Long userId, ActionType action, Date dateFrom,
			Date dateUntil) {
		return depotModel.findByUserActionDateFromDateUntil(userId, action, dateFrom, dateUntil);
	}

	private List<Depot> getDepotListByUser(Long userId) {
		return depotModel.findDepotByUser(userId);
	}

	public Depot getDepotById(Long depotId) {
		if (null == depotId) {
			return null;
		}

		Optional<Depot> optionalDepot = depotModel.findById(depotId);
		if (null != optionalDepot && optionalDepot.isPresent()) {
			return optionalDepot.get();
		}
		return null;
	}

	/**
	 * Update depot with changed values
	 * 
	 * @param depotForUpdate
	 * @param depotContainer
	 * @return
	 */
	private Depot updateDepot(Depot depotForUpdate, Depot depotContainer) throws DamServiceException {

		if (null != depotForUpdate && null != depotContainer) {
			depotForUpdate.updateEntity(depotContainer);
			try {
				return depotModel.save(depotForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), "Depot could not be saved. Perhaps duplicate keys.",
						e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Depot could not be saved", "Check Depot data in request.");
	}

//	private Long dropDepot(Depot depot) {
//		if (null != depot) {
//			depotModel.deleteById(depot.getDepotId());
//			Depot deletedDepot = getDepotById(depot.getDepotId());
//			if (null == deletedDepot) {
//				return new Long(200);
//			}
//		}
//
//		return new Long(10);
//	}

}
