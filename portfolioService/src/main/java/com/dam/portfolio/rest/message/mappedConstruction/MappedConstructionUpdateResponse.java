package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;

public class MappedConstructionUpdateResponse extends MappedConstructionWriteResponse{
    	
	public MappedConstructionUpdateResponse (ConstructionMap constructionMap) {
		super(new Long(200), "OK", "Mapped Construction Updated", constructionMap);
	}  

}
