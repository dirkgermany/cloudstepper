package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;

public class MappedConstructionCreateRequest extends MappedConstructionWriteRequest {

    public MappedConstructionCreateRequest(Long requestorUserId, ConstructionMap constructionMap) {
		super(requestorUserId, constructionMap);
    }
    
}