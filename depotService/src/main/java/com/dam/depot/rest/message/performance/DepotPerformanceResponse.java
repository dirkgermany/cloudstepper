package com.dam.depot.rest.message.performance;

import java.util.List;

import com.dam.depot.performance.DepotPerformanceDetail;
import com.dam.depot.rest.message.RestResponse;

public class DepotPerformanceResponse extends RestResponse{

	private List<DepotPerformanceDetail> depotPerformanceDetails;
	
	public DepotPerformanceResponse(Long result, String shortStatus, String longStatus, List<DepotPerformanceDetail> depotPerformanceDetails) {
		super(result, shortStatus, longStatus);
		setDepotPerformanceDetails(depotPerformanceDetails);
	}

	public List<DepotPerformanceDetail> getDepotPerformanceDetails() {
		return depotPerformanceDetails;
	}

	public void setDepotPerformanceDetails(List<DepotPerformanceDetail> depotPerformanceDetails) {
		this.depotPerformanceDetails = depotPerformanceDetails;
	}

}
