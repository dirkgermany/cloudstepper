package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;

public class MappedConstructionUpdateRequest extends MappedConstructionWriteRequest {

    public MappedConstructionUpdateRequest(Long requestorUserId, ConstructionMap constructionMap) {
    	super (requestorUserId, constructionMap);
    }
}