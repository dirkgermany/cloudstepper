package com.dam.provider.rest;

public class PingInfo {
	
	private String serviceName;
	private String status;
	private String uptime;
	private String systime;
	private String hostname;
	private String serverIp;
	private String database;
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUptime() {
		return uptime;
	}
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
	public String getSystime() {
		return systime;
	}
	public void setSystime(String systime) {
		this.systime = systime;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getServerName() {
		return hostname;
	}
	public void setServerName(String serverName) {
		this.hostname = serverName;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

}
