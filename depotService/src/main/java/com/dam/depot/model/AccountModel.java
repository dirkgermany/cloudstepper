package com.dam.depot.model;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.depot.model.entity.Account;
import com.dam.depot.types.ActionType;

@Transactional
public interface AccountModel extends Repository<Account, Long>, CrudRepository<Account, Long> {

	@Query("SELECT account from Account account WHERE account.userId = :userId")
	List<Account> findAccountByUser(@Param("userId") Long userId);
	
	@Query("SELECT account from Account account WHERE account.userId = :userId " + " AND account.action = :action "
			+ " AND account.actionDate >= :dateFrom " + " AND account.actionDate <= :dateUntil")
	List<Account> findByUserActionDateFromDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom, @Param("dateUntil") Date dateUntil);
	
	@Query("SELECT account from Account account WHERE account.userId = :userId " + " AND account.action = :action "
			+ " AND account.actionDate >= :dateFrom")
	List<Account> findByUserActionDateFrom(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateFrom") Date dateFrom);

	@Query("SELECT account from Account account WHERE account.userId = :userId " + " AND account.action = :action "
			+ " AND account.actionDate <= :dateUntil")
	List<Account> findByUserActionDateUntil(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("dateUntil") Date dateUntil);
	
	@Query("SELECT account from Account account WHERE account.userId = :userId AND account.action = :action ")
	List<Account> findByUserAction(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT account from Account account WHERE account.userId = :userId  AND account.actionDate >= :dateFrom")
	List<Account> findByUserDateFrom(@Param("userId") Long userId, @Param("dateFrom") Date dateFrom);

	@Query("SELECT account from Account account WHERE account.userId = :userId  AND account.actionDate <= :dateUntil")
	List<Account> findByUserDateUntil(@Param("userId") Long userId, @Param("dateUntil") Date dateUntil);

}
