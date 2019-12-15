package com.dam.depot.rest.message.performance;

import java.util.List;

import com.dam.depot.performance.DepotPerformanceDetail;
import com.dam.depot.rest.message.RestResponse;

public class DepotPerformanceResponse extends RestResponse{

	private List<DepotPerformanceDetail> depotPerformanceDetails;
	private Float periodPerformancePercentage;
	private Float periodPerformanceValue;
	private String periodPerformancePercentageAsString;
	private Float periodAmountAtEnd;
	
	public DepotPerformanceResponse(Long result, String shortStatus, String longStatus, Float periodPerformancePercentage, String periodPerformancePercentageAsString, Float periodPerformanceValue, Float periodAmountAtEnd, List<DepotPerformanceDetail> depotPerformanceDetails) {
		super(result, shortStatus, longStatus);
		setDepotPerformanceDetails(depotPerformanceDetails);
		setPeriodPerformanceValue(periodPerformanceValue);
		setPeriodPerformancePercentage(periodPerformancePercentage);
		setPeriodPerformancePercentageAsString(periodPerformancePercentageAsString);
		setPeriodAmountAtEnd(periodAmountAtEnd);
	}

	public List<DepotPerformanceDetail> getDepotPerformanceDetails() {
		return depotPerformanceDetails;
	}

	public void setDepotPerformanceDetails(List<DepotPerformanceDetail> depotPerformanceDetails) {
		this.depotPerformanceDetails = depotPerformanceDetails;
	}

	public Float getPeriodPerformancePercentage() {
		return periodPerformancePercentage;
	}

	public void setPeriodPerformancePercentage(Float periodPerformancePercentage) {
		this.periodPerformancePercentage = periodPerformancePercentage;
	}

	public Float getPeriodPerformanceValue() {
		return periodPerformanceValue;
	}

	public void setPeriodPerformanceValue(Float periodPerformanceValue) {
		this.periodPerformanceValue = periodPerformanceValue;
	}

	public String getPeriodPerformancePercentageAsString() {
		return periodPerformancePercentageAsString;
	}

	public void setPeriodPerformancePercentageAsString(String periodPerformancePercentageAsString) {
		this.periodPerformancePercentageAsString = periodPerformancePercentageAsString;
	}

	public Float getPeriodAmountAtEnd() {
		return periodAmountAtEnd;
	}

	public void setPeriodAmountAtEnd(Float periodAmountAtEnd) {
		this.periodAmountAtEnd = periodAmountAtEnd;
	}

}
