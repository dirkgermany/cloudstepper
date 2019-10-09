package com.dam.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
public class ConfigProperties {

	@Value("${server.port}")
	String serverPort;

	@Value("${activated.services}")
	private String[] servicesInConfFile;
	
	@Value("${activated.domains}")
	private String[] domainsInConfFile;

	private List<String> configuredServices;
	private List<String> configuredDomains;
	
	private Map<String, Integer> indexDomain;
	private Map<String, Integer> indexService;
	
	private List<String> services;
	private List<String> domains;
	private List<String> ports;
	private List<String> hosts;
	private List<String> protocols;
	
	public Integer getIndexPerDomain(String domain) {
		if (null == indexDomain) {
			indexDomain = new HashMap<String, Integer>();
			
			Iterator <String> it = getDomains().iterator();
			Integer index = 0;
			while (it.hasNext()) {
				String name = it.next();
				indexDomain.put(name.toUpperCase(), index);
				index++;
			}
		}
		return indexDomain.get(domain.toUpperCase());
	}
	
	public Integer getIndexPerService(String service) {
		if (null == indexService) {
			indexService = new HashMap<String, Integer>();
			
			Iterator <String> it = getServices().iterator();
			Integer index = 0;
			while (it.hasNext()) {
				String name = it.next();
				indexService.put(name.toUpperCase(), index);
				index++;
			}
		}
		return indexService.get(service.toUpperCase());
	}
	
	public String getServiceUrl(int index) {
		return getProtocols().get(index) + "://" + getHosts().get(index) + ":" + getPorts().get(index);
	}
	
	public String getServiceName(int index) {
		return getServices().get(index);
	}


	public List<String> getServiceList() {
		if (null == configuredServices || configuredServices.isEmpty()) {
			configuredServices = Arrays.asList(servicesInConfFile);
		}
		return configuredServices;
	}

	public List<String> getDomainList() {
		if (null == configuredDomains || configuredDomains.isEmpty()) {
			configuredDomains = Arrays.asList(domainsInConfFile);
		}
		return configuredDomains;
	}

	public String[] getServicesInConfFile() {
		return servicesInConfFile;
	}

	public void setServicesInConfFile(String[] servicesInConfFile) {
		this.servicesInConfFile = servicesInConfFile;
	}


	public List<String> getConfiguredServices() {
		return configuredServices;
	}

	public void setConfiguredServices(List<String> configuredServices) {
		this.configuredServices = configuredServices;
	}
	
	public String[] getDomainsInConfFile() {
		return domainsInConfFile;
	}

	public void setDomainsInConfFile(String[] domainsInConfFile) {
		this.domainsInConfFile = domainsInConfFile;
	}

	public List<String> getConfiguredDomains() {
		return configuredDomains;
	}

	public void setConfiguredDomains(List<String> configuredDomains) {
		this.configuredDomains = configuredDomains;
	}


	public Map<String, Integer> getIndexes() {
		return indexDomain;
	}

	public void setIndexes(Map<String, Integer> indexes) {
		this.indexDomain = indexes;
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}

	public List<String> getPorts() {
		return ports;
	}

	public void setPorts(List<String> ports) {
		this.ports = ports;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public List<String> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<String> protocols) {
		this.protocols = protocols;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
}
