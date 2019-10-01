package com.dam.depot.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.depot.model.entity.Intent;
import com.dam.depot.types.ActionType;

@Transactional
public interface IntentModel extends Repository<Intent, Long>, CrudRepository<Intent, Long> {
	
	@Query("SELECT intent from Intent intent WHERE intent.userId = :userId " + " AND intent.action = :action "
			+ " AND intent.bookingDate = :bookingDate")
	List<Intent> findByUserActionBookingDate(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("bookingDate") Boolean bookingDate);
	
	@Query("SELECT intent from Intent intent WHERE intent.userId = :userId " + " AND intent.action = :action "
			+ " AND UPPER(intent.booked) = 'Y' ")
	List<Intent> findByUserActionBooked(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT intent from Intent intent WHERE intent.userId = :userId " + " AND intent.action = :action "
			+ " AND (UPPER(intent.booked) = 'N' or intent.booked is null) ")
	List<Intent> findByUserActionNotBooked(@Param("userId") Long userId, @Param("action") ActionType action);
	
	@Query("SELECT intent from Intent intent WHERE intent.action = :action AND (UPPER(intent.booked) = 'N' or intent.booked is null) ")
	List<Intent> findByActionNotBooked(@Param("action") ActionType action);

	@Query("SELECT intent from Intent intent WHERE intent.action = :action AND UPPER(intent.booked) = 'Y' ")
	List<Intent> findByActionBooked(@Param("action") ActionType action);
}
