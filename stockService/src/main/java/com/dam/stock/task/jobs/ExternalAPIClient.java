package com.dam.stock.task.jobs;

import com.dam.stock.task.Client;

public abstract class ExternalAPIClient extends Client {
	
	abstract void waitTimeForNextRequest();

}
