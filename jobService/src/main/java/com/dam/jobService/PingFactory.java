package com.dam.jobService;

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
	TaskConfiguration taskConfiguration;
	
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
		
//		Iterator <String> itSuccessors = taskConfiguration.getSuccessorList().iterator();
//		String taskInfo = "tasksConfigured: [";
//		boolean first = true;
//		while (itSuccessors.hasNext()) {
//			if (!first) {
//				taskInfo+=", ";
//			}
//			taskInfo+= "{task: " + itSuccessors.next() + "}";
//			first=false;
//		}
//		taskInfo+= "]";
//		pingInfo.put("Active Jobs", taskInfo);
//
//		
//		
//		Iterator <ActionType> it = TaskController.getActiveJobs().iterator();
//		String actionInfo = "jobsActive: [";
//		first = true;
//		while (it.hasNext()) {
//			if (!first) {
//				actionInfo+=", ";
//			}
//			actionInfo+= "{job: " + it.next().name() + "}";
//			first=false;
//		}
//		actionInfo+= "]";
//		pingInfo.put("Active Jobs", actionInfo);
		
	}
	
	public Map<String, String> getPingInfo() {
		return this.pingInfo;
	}
	
	public JsonNode getNode() {
		return new JsonHelper().createNodeFromMap(getPingInfo());
	}
}
