package com.dam.portfolio.model;


import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import com.dam.portfolio.model.entity.Portfolio;

@Transactional
public interface PortfolioModel extends Repository<Portfolio, Long>, CrudRepository<Portfolio, Long> {
	
	Portfolio findByPortfolioName(String portfolioName);

}
