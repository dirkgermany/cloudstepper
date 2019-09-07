package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.rest.message.RestRequest;

public class MappedConstructionRequest extends RestRequest {
    private Long mapId;

    public MappedConstructionRequest( Long requestorUserId, Long mapId) {
		super("DAM 2.0");
		setRequestorUserId(requestorUserId);
	}

	public Long getMapId() {
		return mapId;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}
    
}