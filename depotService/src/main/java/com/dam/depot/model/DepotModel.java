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

	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId")
	List<Depot> findDepotByUser(@Param("userId") Long userId);
	
	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate >= :dateFrom " + " AND depot.actionDate <= :dateUntil")
	List<Depot> findByUserActionDateFromDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom, @Param("dateUntil") Date dateUntil);
	
	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate >= :dateFrom")
	List<Depot> findByUserActionDateFrom(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom);

	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId " + " AND depot.action = :action "
			+ " AND depot.actionDate <= :dateUntil")
	List<Depot> findByUserActionDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateUntil") Date dateUntil);
	
	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId AND depot.action = :action ")
	List<Depot> findByUserAction(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId  AND depot.actionDate >= :dateFrom")
	List<Depot> findByUserDateFrom(@Param("userId") Long userId, @Param("dateFrom") Date dateFrom);

	@Query("SELECT depot from Depot depot WHERE depot.userId = :userId  AND depot.actionDate <= :dateUntil")
	List<Depot> findByUserDateUntil(@Param("userId") Long userId, @Param("dateUntil") Date dateUntil);

}
