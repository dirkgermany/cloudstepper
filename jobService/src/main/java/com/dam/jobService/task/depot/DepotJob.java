package com.dam.jobService.task.depot;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.task.Client;

@Component
public class DepotJob extends Client {
	
	public DepotJob() {
		
	}
	
	public void login () throws DamServiceException {
		super.login();
	}

}
