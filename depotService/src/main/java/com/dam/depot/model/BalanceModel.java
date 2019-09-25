package com.dam.depot.model;


import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import com.dam.depot.model.entity.Balance;

@Transactional
public interface BalanceModel extends Repository<Balance, Long>, CrudRepository<Balance, Long> {

}
