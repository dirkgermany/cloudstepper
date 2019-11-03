package com.dam.depot.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.depot.model.entity.DepotTransaction;
import com.dam.depot.types.ActionType;

@Transactional
public interface DepotTransactionModel extends Repository<DepotTransaction, Long>, CrudRepository<DepotTransaction, Long> {

	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId")
	List<DepotTransaction> findDepotTransactionByUser(@Param("userId") Long userId);
	
	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId " + " AND depotTransaction.action = :action "
			+ " AND depotTransaction.actionDate >= :dateFrom " + " AND depotTransaction.actionDate <= :dateUntil")
	List<DepotTransaction> findByUserActionDateFromDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") LocalDateTime dateFrom, @Param("dateUntil") LocalDateTime dateUntil);
	
	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId " + " AND depotTransaction.action = :action "
			+ " AND depotTransaction.actionDate >= :dateFrom")
	List<DepotTransaction> findByUserActionDateFrom(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") LocalDateTime dateFrom);

	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId " + " AND depotTransaction.action = :action "
			+ " AND depotTransaction.actionDate <= :dateUntil")
	List<DepotTransaction> findByUserActionDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateUntil") LocalDateTime dateUntil);
	
	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId AND depotTransaction.action = :action ")
	List<DepotTransaction> findByUserAction(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId  AND depotTransaction.actionDate >= :dateFrom")
	List<DepotTransaction> findByUserDateFrom(@Param("userId") Long userId, @Param("dateFrom") LocalDateTime dateFrom);

	@Query("SELECT depotTransaction from DepotTransaction depotTransaction WHERE depotTransaction.userId = :userId  AND depotTransaction.actionDate <= :dateUntil")
	List<DepotTransaction> findByUserDateUntil(@Param("userId") Long userId, @Param("dateUntil") LocalDateTime dateUntil);

}
