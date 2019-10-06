package com.dam.depot;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.depot.store.AccountStatusStore;
import com.dam.depot.store.AccountTransactionStore;
import com.dam.depot.store.DepotStore;
import com.dam.depot.store.DepotTransactionStore;
import com.dam.depot.store.IntentStore;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PingFactory {
	
	@Autowired
	private DepotTransactionStore depotTransactionStore;
	
	@Autowired
	private AccountTransactionStore accountTransactionStore;
	
	@Autowired
	private DepotStore depotStore;
	
	@Autowired
	private AccountStatusStore accountStatusStore;
	
	@Autowired
	private IntentStore intentStore;
	
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
		String recordsDepotTransactions = String.valueOf(depotTransactionStore.count());
		String recordsAccountTransactions  = String.valueOf(accountTransactionStore.count());
		String recordsDepot = String.valueOf(depotStore.count());
		String recordsAccountStatus = String.valueOf(accountStatusStore.count());
		String recordsIntent = String.valueOf(intentStore.count());
		pingInfo.put("database", "records available: [{table: depot, records: " + recordsDepot + "},"
                +  "{table: accountTransactions, records: " + recordsAccountTransactions + "}, "
                +  "{table: depotTransactions, records: " + recordsDepotTransactions + "}, "
                +  "{table: accountStatus, records: " + recordsAccountStatus + "}, "
                + " {table: intent, records: " + recordsIntent + "}]");

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
