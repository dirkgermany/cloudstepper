package com.dam.portfolio.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dam.exception.DamServiceException;
import com.dam.portfolio.PortfolioStore;
import com.dam.portfolio.rest.message.RestResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioCreateResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioDropResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioPerformanceRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioResponse;
import com.dam.portfolio.rest.message.portfolio.PortfolioUpdateRequest;
import com.dam.portfolio.rest.message.portfolio.PortfolioUpdateResponse;

@RestController
public class PortfolioController extends MasterController {
	@Autowired
	private PortfolioStore portfolioStore;

	@PostMapping("/getPortfolioPerformance")
	public RestResponse getPortfolioPerformanceByPost(@RequestBody PortfolioPerformanceRequest portfolioPerformanceRequest, @RequestHeader(name = "tokenId", required = false) String tokenId)
			throws DamServiceException {
		try {
			return portfolioStore.getPortfolioPerformanceSafe(portfolioPerformanceRequest, tokenId);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@GetMapping("/getPortfolioPerformance")
	public RestResponse getPortfolioPerformance(@RequestParam Map<String, String> params, @RequestHeader(name = "tokenId", required = false) String tokenId)
			throws DamServiceException {
		
		Map<String, String> decodedMap = decodeUrlMap(params);
		try {
			return portfolioStore.getPortfolioPerformanceSafe(decodedMap, tokenId);
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/getPortfolio")
	public RestResponse getPortfolio(@RequestBody PortfolioRequest portfolioRequest) throws DamServiceException {
		try {
			return new PortfolioResponse(portfolioStore.getPortfolioSafe(portfolioRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createPortfolio")
	public RestResponse createPortfolio(@RequestBody PortfolioCreateRequest portfolioCreateRequest)
			throws DamServiceException {
		try {
			return new PortfolioCreateResponse(portfolioStore.createPortfolioSafe(portfolioCreateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropPortfolio")
	public RestResponse dropPortfolio(@RequestBody PortfolioDropRequest portfolioDropRequest)
			throws DamServiceException {
		try {
			return new PortfolioDropResponse(portfolioStore.dropPortfolioSafe(portfolioDropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updatePortfolio")
	public RestResponse updatePortfolio(@RequestBody PortfolioUpdateRequest portfolioUpdateRequest)
			throws DamServiceException {
		try {
			return new PortfolioUpdateResponse(portfolioStore.updatePortfolioSafe(portfolioUpdateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}