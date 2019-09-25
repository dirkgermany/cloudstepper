package com.dam.depot;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.dam.depot.model.BalanceModel;
import com.dam.depot.model.entity.Balance;
import com.dam.exception.DamServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
public class BalanceStore {

	@Autowired
	private BalanceModel balanceModel;

	public long count() {
		return balanceModel.count();
	}

	public Balance getBalanceByUserId(Long userId) {
		Optional<Balance> optionalBalance = balanceModel.findById(userId);
		if (null != optionalBalance && optionalBalance.isPresent()) {
			return optionalBalance.get();
		}
		return null;
	}
	
	public Balance saveBalance(Balance balance) throws DamServiceException {
		return balanceModel.save(balance);
	}
}
