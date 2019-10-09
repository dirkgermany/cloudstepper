package com.dam.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Does two missions:
 * 
 * - Holding a list with active running tasks
 * - Holding a dependency chain for the tasks
 * @author dirk
 *
 */
@ConfigurationProperties
public class Configuration {
	
	
	@Value("${server.port}")
	String serverPort;

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

}
