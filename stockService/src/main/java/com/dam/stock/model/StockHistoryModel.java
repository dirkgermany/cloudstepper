package com.dam.stock.model;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.type.Symbol;

@Transactional
public interface StockHistoryModel extends Repository<StockHistory, Long>, CrudRepository<StockHistory, Long> {
	
	@Query("SELECT stockHistory from StockHistory stockHistory WHERE stockHistory.symbol = :symbol ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findAllBySymbol(@Param("symbol") String symbol);

	@Query("SELECT stockHistory from StockHistory stockHistory WHERE stockHistory.symbol = :symbol " 
			+ " AND stockHistory.historyDate = :historyDate")
	StockHistory findBySymbolDate(@Param("symbol") Symbol symbol, @Param("historyDate") Date historyDate);

	@Query("SELECT stockHistory from StockHistory stockHistory "
			+ " WHERE stockHistory.symbol = :symbol "
			+ " AND stockHistory.historyDate <= :endDate "
			+ " AND stockHistory.historyDate >= :startDate "
			+ " ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findBySymbolStartEndDate(@Param("symbol") Symbol symbol, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("SELECT stockHistory from StockHistory stockHistory "
			+ " WHERE stockHistory.historyDate <= :endDate "
			+ " AND stockHistory.historyDate >= :startDate "
			+ " ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findByStartEndDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
