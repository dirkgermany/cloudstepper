package com.dam.depot.model;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.depot.model.entity.AccountTransaction;
import com.dam.depot.types.ActionType;

@Transactional
public interface AccountTransactionModel extends Repository<AccountTransaction, Long>, CrudRepository<AccountTransaction, Long> {

	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId")
	List<AccountTransaction> findAccountByUser(@Param("userId") Long userId);
	
	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId " + " AND accountTransaction.action = :action "
			+ " AND accountTransaction.actionDate >= :dateFrom " + " AND accountTransaction.actionDate <= :dateUntil")
	List<AccountTransaction> findByUserActionDateFromDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom, @Param("dateUntil") Date dateUntil);
	
	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId " + " AND accountTransaction.action = :action "
			+ " AND accountTransaction.actionDate >= :dateFrom")
	List<AccountTransaction> findByUserActionDateFrom(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom);

	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId " + " AND accountTransaction.action = :action "
			+ " AND accountTransaction.actionDate <= :dateUntil")
	List<AccountTransaction> findByUserActionDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateUntil") Date dateUntil);
	
	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId AND accountTransaction.action = :action ")
	List<AccountTransaction> findByUserAction(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId  AND accountTransaction.actionDate >= :dateFrom")
	List<AccountTransaction> findByUserDateFrom(@Param("userId") Long userId, @Param("dateFrom") Date dateFrom);

	@Query("SELECT accountTransaction from AccountTransaction accountTransaction WHERE accountTransaction.userId = :userId  AND accountTransaction.actionDate <= :dateUntil")
	List<AccountTransaction> findByUserDateUntil(@Param("userId") Long userId, @Param("dateUntil") Date dateUntil);

}
