package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.rest.message.RestResponse;

public class AssetClassDropResponse extends RestResponse{
    	
	public AssetClassDropResponse (Long result) {
		super(result, "OK", "AssetClass dropped");
	}  
}
