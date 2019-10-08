package com.dam.stock.task.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.stock.Configuration;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.task.Client;
import com.dam.stock.type.Symbol;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class AlphaVantageClient extends Client {
	@Autowired
	Configuration configuration;

//	protected final static String STOCK_DOMAIN = "ASSET";
	protected final static String NODE_RESPONSE_DAILY_LIST = "Time Series (Daily)";
//	protected final static String PATH_LIST_ASSET_CLASS = "getAssetClassList";

	private static final Logger logger = LoggerFactory.getLogger(AlphaVantageClient.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	/*
	 * Requests the Service Provider for a List with TransferToDepot Intents.
	 */
	public List<StockHistory> getStockData(Symbol symbol, String wkn) throws DamServiceException {
		JsonNode response = sendGetRequest(getStockProviderUrl(), getStockParams(symbol.getRequestSymbol()));
		JsonNode jsonHistoryList = jsonHelper.extractNodeFromNode(response, NODE_RESPONSE_DAILY_LIST);

		List<StockHistory> result = new ArrayList<>();
		Iterator<Entry<String, JsonNode>> it = jsonHistoryList.fields();

		while (it.hasNext()) {
			Entry<String, JsonNode> entry = it.next();
			StockHistory stockHistory = new StockHistory();

			String dateString = entry.getKey();
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
			} catch (ParseException e) {
			}

			JsonNode node = entry.getValue();
			String open = node.get("1. open").asText();
			if (null != open && !open.isEmpty()) {
				stockHistory.setOpen(Float.valueOf(open));
			}
			String close = node.get("4. close").asText();
			if (null != close && !close.isEmpty()) {
				stockHistory.setClose(Float.valueOf(close));
			}
			stockHistory.setSymbol(symbol);
			stockHistory.setWkn(wkn);
			stockHistory.setHistoryDate(date);

			result.add(stockHistory);
		}
		return result;
	}

	private String getStockProviderUrl() {
		return configuration.getStockServiceProtocol() + "://" + configuration.getStockServiceHost();
	}

	private String getStockParams(String requestSymbol) {
		String query = "query?";
		String function = "&function=" + configuration.getStockServiceFunction();
		String symbol = "&symbol=" + requestSymbol;
		String outputsize = "&outputsize=full";
		String apiKey = "&apikey=" + configuration.getStockServiceKey();

		return query + function + symbol + outputsize + apiKey;
	}

}
