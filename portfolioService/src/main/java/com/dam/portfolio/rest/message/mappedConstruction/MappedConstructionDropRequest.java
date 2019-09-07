package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.model.entity.ConstructionMap;

public class MappedConstructionDropRequest extends MappedConstructionWriteRequest {

	public MappedConstructionDropRequest(Long requestorUserId, ConstructionMap constructionMap) {
		super(requestorUserId, constructionMap);
    }
}