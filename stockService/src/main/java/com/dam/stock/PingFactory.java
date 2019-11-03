package com.dam.stock;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PingFactory {

	@Autowired
	Configuration configuration;

	private Map<String, String> pingInfo = new HashMap<String, String>();

	public PingFactory() {
	}


	public void setInfo(String serviceName) {

		// Own app name
		pingInfo.put("service", serviceName);

		// service port
		pingInfo.put("port", configuration.getServerPort());

		// Status
		pingInfo.put("status", "OK");

		// Time
		pingInfo.put("systime", LocalDateTime.now().toString());

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
			pingInfo.put("hostName", InetAddress.getLocalHost().getCanonicalHostName());
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
