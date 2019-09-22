package com.dam.depot.model;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.depot.model.entity.Depot;
import com.dam.depot.types.ActionType;

@Transactional
public interface DepotModel extends Repository<Depot, Long>, CrudRepository<Depot, Long> {

	@Query("SELECT from Depot depot WHERE depot.userId = :userId")
	List<Depot> findDepotByUser(@Param("userId") Long userId);
	
	
	
	@Query("SELECT from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate >= :dateFrom " + " AND depot.actionDate <= :dateUntil")
	List<Depot> findByUserActionDateFromDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom, @Param("dateUntil") Date dateUntil);
	
	@Query("SELECT from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate >= :dateFrom")
	List<Depot> findByUserActionDateFrom(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom);

	@Query("SELECT from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate >= :dateFrom")
	List<Depot> findByUserActionDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateUntil);

}
