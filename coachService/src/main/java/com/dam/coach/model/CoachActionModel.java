package com.dam.coach.model;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.dam.coach.model.entity.CoachAction;

@Transactional
public interface CoachActionModel extends Repository<CoachAction, Long>, CrudRepository<CoachAction, Long> {
	
	@Query("SELECT action FROM CoachAction action WHERE action.actionReference = :actionReference ")
	CoachAction findByActionReference(@Param("actionReference") String actionReference);
}
