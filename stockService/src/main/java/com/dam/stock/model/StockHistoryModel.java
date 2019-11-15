package com.dam.stock.model;

import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.dam.stock.model.entity.StockHistory;

@Transactional
public interface StockHistoryModel extends Repository<StockHistory, Long>, CrudRepository<StockHistory, Long> {
	
	@Query("SELECT stockHistory from StockHistory stockHistory WHERE stockHistory.symbol = :symbol ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findAllBySymbol(@Param("symbol") String symbol);

	@Query("SELECT stockHistory from StockHistory stockHistory WHERE stockHistory.symbol = :symbol " 
			+ " AND stockHistory.historyDate = :historyDate")
	StockHistory findBySymbolDate(@Param("symbol") String symbol, @Param("historyDate") LocalDate historyDate);

	@Query("SELECT stockHistory from StockHistory stockHistory "
			+ " WHERE stockHistory.symbol = :symbol "
			+ " AND stockHistory.historyDate <= :endDate "
			+ " AND stockHistory.historyDate >= :startDate "
			+ " ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findBySymbolStartEndDate(@Param("symbol") String symbol, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	@Query("SELECT stockHistory from StockHistory stockHistory "
			+ " WHERE stockHistory.historyDate <= :endDate "
			+ " AND stockHistory.historyDate >= :startDate "
			+ " ORDER BY stockHistory.historyDate") // DESC")
	List<StockHistory> findByStartEndDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	@Query("SELECT stockHistory from StockHistory stockHistory "
			+ " WHERE stockHistory.symbol = :symbol "
			+ " AND   stockHistory.historyDate = "
			+ " (SELECT MAX(history.historyDate) FROM StockHistory history "
			+ " where history.symbol = :symbol)")
	StockHistory findLastEntryForAsset(@Param("symbol") String symbol);
}
