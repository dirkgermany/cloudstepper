package com.dam.portfolio.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.PortfolioStore;
import com.dam.portfolio.model.entity.Portfolio;
import com.dam.portfolio.rest.message.RestResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioUpdateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioUpdateResponse;

@RestController
public class PortfolioController {
	@Autowired
	private PortfolioStore portfolioStore;

	/**
	 * Retrieves a person
	 * 
	 * @param personRequest
	 * @return
	 */
	@PostMapping("/getPortfolio")
	public RestResponse getPortfolio(@RequestBody PortfolioRequest portfolioRequest) throws DamServiceException {
		try {
			return new PortfolioResponse(portfolioStore.getPortfolioSafe(portfolioRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createPortfolio")
	public RestResponse createPortfolio(@RequestBody PortfolioCreateRequest portfolioCreateRequest) throws DamServiceException {
		try {
			return new PortfolioCreateResponse(portfolioStore.createPortfolioSafe(portfolioCreateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropPortfolio")
	public RestResponse dropPortfolio(@RequestBody PortfolioDropRequest portfolioDropRequest) throws DamServiceException {
		try {
			return new PortfolioDropResponse(portfolioStore.dropPortfolioSafe(portfolioDropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updatePortfolio")
	public RestResponse updatePortfolio(@RequestBody PortfolioUpdateRequest portfolioUpdateRequest) throws DamServiceException {
		try {
			return new PortfolioUpdateResponse(portfolioStore.updatePortfolioSafe(portfolioUpdateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}