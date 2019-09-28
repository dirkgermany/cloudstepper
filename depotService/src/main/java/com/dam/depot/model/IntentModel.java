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
	
	@Query("SELECT from Intent intent WHERE intent.userId = :userId " + " AND intent.action = :action "
			+ " AND intent.booked = :booked")
	List<Intent> findByUserActionBooked(@Param("userId") Long userId, @Param("action") ActionType action,
			@Param("booked") Boolean booked);
	


}
