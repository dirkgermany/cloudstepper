package com.dam.portfolio;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PingFactory {
	
	@Autowired
	PortfolioStore portfolioStore;
	
	@Autowired
	AssetClassStore assetClassStore;
	
	@Autowired
	AssetClassToPortfolioMapStore mapStore;
	
	private Map<String, String> pingInfo = new HashMap<String, String>();

	public PingFactory() {
	}
	
	public void init(String serviceName) {

		// Own app name
		pingInfo.put("service", serviceName);

		// Status
		pingInfo.put("status", "OK");

		// Time
		pingInfo.put("systime", new Date().toString());

		// Uptime
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		Long upTime = runtimeBean.getUptime();
		pingInfo.put("uptime", upTime.toString() + "ms");

		// Database
		String recordsAsset = String.valueOf(assetClassStore.count());
		String recordsPortfolio = String.valueOf(portfolioStore.count());
		String recordsMap = String.valueOf(mapStore.count());
		pingInfo.put("database", "records available: portfolios (" + recordsPortfolio + "); asset classes (" + recordsAsset + "); map entries (" + recordsMap + ")");

		// Server
		try {
			pingInfo.put("hostAddress", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
		}

		try {
			InetAddress.getLocalHost().getHostName();
			pingInfo.put("hostName", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e1) {
		}
	}
	
	public Map<String, String> getPingInfo() {
		return this.pingInfo;
	}
	
	public JsonNode getNode() {
		return new JsonHelper().createNodeFromMap(getPingInfo());
	}
}
