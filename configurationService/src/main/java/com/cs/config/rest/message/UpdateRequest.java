package com.cs.config.rest.message;

import com.cs.config.model.entity.ConfigEntity;

public class UpdateRequest extends RestRequest {

	private ConfigEntity configStored = new ConfigEntity();
	private ConfigEntity configContainer = new ConfigEntity();

    public UpdateRequest(ConfigEntity userStored, ConfigEntity userUpdate) {
		super("CS 0.0.1");
		this.configStored = userStored;
		this.configContainer = userUpdate;
    }
    
    public ConfigEntity getConfigStored() {
    	return configStored;
    }
    
    public ConfigEntity getConfigContainer() {
    	return configContainer;
    }
    
}