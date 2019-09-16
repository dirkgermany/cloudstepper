package com.dam.serviceProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
public class ConfigProperties {

	@Value("${activated.services}")
	private String[] servicesInConfFile;
	
	@Value("${activated.domains}")
	private String[] domainsInConfFile;

	private List<String> configuredServices;
	private List<String> configuredDomains;
	
	private Map<String, Integer> indexes;
	
	private List<String> services;
	private List<String> domains;
	private List<String> ports;
	private List<String> hosts;
	private List<String> protocols;
	
	public Integer getIndexPerDomain(String domain) {
		if (null == indexes) {
			indexes = new HashMap<String, Integer>();
			
			Iterator <String> it = getDomains().iterator();
			Integer index = 0;
			while (it.hasNext()) {
				String name = it.next();
				indexes.put(name, index);
				index++;
			}
		}
		return indexes.get(domain);
	}
	
	public String getServiceUrl(int index) {
		return getProtocols().get(index) + "://" + getHosts().get(index) + ":" + getPorts().get(index);
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
		return indexes;
	}

	public void setIndexes(Map<String, Integer> indexes) {
		this.indexes = indexes;
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
}
