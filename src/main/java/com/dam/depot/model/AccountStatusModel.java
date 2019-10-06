package com.dam.depot.model;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.depot.model.entity.AccountStatus;

@Transactional
public interface AccountStatusModel extends Repository<AccountStatus, Long>, CrudRepository<AccountStatus, Long> {
	
	@Query("SELECT accountStatus from AccountStatus accountStatus WHERE accountStatus.userId = :userId ")
	AccountStatus findByUserId(@Param("userId") Long userId);

}
