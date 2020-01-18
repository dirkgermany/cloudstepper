package com.cs.config.rest.message;

import org.springframework.http.HttpStatus;

import com.cs.config.model.entity.ConfigEntity;

public class ConfigResponse extends RestResponse{
    private ConfigEntity configEntity;
    	
	public ConfigResponse (ConfigEntity configEntity) {
		super(HttpStatus.OK, "OK", "Configuration found");
		
		setConfigEntity(configEntity);
	}  

	private void setConfigEntity(ConfigEntity configEntity) {
		this.configEntity = configEntity;
	}
	
	public ConfigEntity getConfigEntity() {
		return this.configEntity;
	}
	
}
