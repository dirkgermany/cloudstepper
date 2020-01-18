package com.dam.provider;

public class Domain {
	
	public Domain () {
		
	}
	
	private String domainName;
	private String serviceName;
	private int port;
	private String hostName;
	private String protocol;
	
	public String getUrl() {
		return getProtocol() + "://" + getHostName() + ":" + Integer.valueOf(getPort()).toString();
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}

