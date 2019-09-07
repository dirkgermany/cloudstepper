package com.dam.person;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class PingFactory {
	private Map<String, String> pingInfo = new HashMap<String, String>();

	public PingFactory(String serviceName) {

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
