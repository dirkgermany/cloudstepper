package com.dam.portfolio.rest.message.assetClass;

import com.dam.portfolio.model.entity.AssetClass;

public class AssetClassUpdateResponse extends AssetClassWriteResponse{
    	
	public AssetClassUpdateResponse (AssetClass assetClass) {
		super(new Long(200), "OK", "AssetClass Updated", assetClass);
	}  

}
