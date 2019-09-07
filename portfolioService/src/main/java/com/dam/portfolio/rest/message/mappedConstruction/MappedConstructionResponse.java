package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;

public class MappedConstructionResponse extends RestResponse{
    private ConstructionMap constructionMap;
    	
	public MappedConstructionResponse (ConstructionMap constructionMap) {
		super(new Long(200), "OK", "MapEntry found");
		setMappedConstruction(constructionMap);
	}  

	private void setMappedConstruction(ConstructionMap constructionMap) {
		this.constructionMap = constructionMap;
	}
	
	public ConstructionMap getMappedConstruction() {
		return this.constructionMap;
	}
	
}
