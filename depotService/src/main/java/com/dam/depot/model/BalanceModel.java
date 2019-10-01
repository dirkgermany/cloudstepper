package com.dam.depot.model;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.depot.model.entity.Balance;

@Transactional
public interface BalanceModel extends Repository<Balance, Long>, CrudRepository<Balance, Long> {
	
	@Query("SELECT balance from Balance balance WHERE balance.userId = :userId ")
	Balance findByUserId(@Param("userId") Long userId);

}
