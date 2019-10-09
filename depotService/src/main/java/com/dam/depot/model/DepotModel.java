package com.dam.depot.model;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.depot.model.entity.Depot;

@Transactional
public interface DepotModel extends Repository<Depot, Long>, CrudRepository<Depot, Long> {
	
	@Query("SELECT depot FROM Depot depot WHERE depot.userId = :userId ")
	List<Depot> findByUserId(@Param("userId") Long userId);
	
	@Query("SELECT depot FROM Depot depot WHERE depot.userId = :userId " +
			" AND depot.portfolioId = :portfolioId")
	Depot findByUserPortfolio(@Param("userId") Long userId, @Param("portfolioId") Long portfolioId);

}
