package com.dam.portfolio.rest.message.mappedConstruction;

import java.util.List;

import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;

public class MappedConstructionListResponse extends RestResponse {
	
	List<ConstructionMap> constructionMapList;

	public MappedConstructionListResponse(List<ConstructionMap> constructionMapList) {
		super (new Long(200), "OK", "Construction Maps found");
		this.constructionMapList = constructionMapList;
	}

	public List<ConstructionMap> getConstructionMapList() {
		return constructionMapList;
	}

	public void setConstructionMapList(List<ConstructionMap> constructionMapList) {
		this.constructionMapList = constructionMapList;
	}

}
