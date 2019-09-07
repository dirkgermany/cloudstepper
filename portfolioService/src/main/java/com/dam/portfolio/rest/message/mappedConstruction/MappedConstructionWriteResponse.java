package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;
import com.dam.portfolio.rest.message.RestResponse;

public abstract class MappedConstructionWriteResponse extends RestResponse{
    private ConstructionMap constructionMap;
	
	public MappedConstructionWriteResponse(Long result, String shortStatus, String longStatus, ConstructionMap constructionMap) {
		super(result, shortStatus, longStatus);
		setMappedConstruction(constructionMap);
	}

	private void setMappedConstruction(ConstructionMap constructionMap) {
		this.constructionMap = constructionMap;
	}
	
	public ConstructionMap getMappedConstruction() {
		return constructionMap;
	}
}
