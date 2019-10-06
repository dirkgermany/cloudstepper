package com.dam.stock.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.stock.model.entity.StockHistory;

@Transactional
public interface StockHistoryModel extends Repository<StockHistory, Long>, CrudRepository<StockHistory, Long> {
	
	@Query("SELECT stockHistory from StockHistory stockHistory WHERE stockHistory.symbol = :symbol ORDER By stockHistory.historyDate DESCENDING")
	List<StockHistory> findAllBySymbol(@Param("symbol") String symbol);

}
