package com.dam.portfolio.rest.message.mappedConstruction;

import com.dam.portfolio.rest.message.RestResponse;

public class MappedConstructionDropResponse extends RestResponse{
    	
	public MappedConstructionDropResponse (Long result) {
		super(result, "OK", "MapEntry dropped");
	}  
}
