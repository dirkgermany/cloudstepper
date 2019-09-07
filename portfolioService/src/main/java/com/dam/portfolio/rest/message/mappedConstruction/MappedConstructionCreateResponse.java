package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;

public class MappedConstructionCreateResponse extends MappedConstructionWriteResponse{

	public MappedConstructionCreateResponse(ConstructionMap constructionMap) {
		super(new Long(200), "OK", "MappedConstruction Saved", constructionMap);
	}
    	
}
