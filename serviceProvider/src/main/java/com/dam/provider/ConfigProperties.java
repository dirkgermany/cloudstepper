package com.dam.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.dam.exception.CsServiceException;
import com.dam.provider.rest.consumer.Client;
import com.fasterxml.jackson.databind.JsonNode;

@ConfigurationProperties(prefix = "service")
public class ConfigProperties {

	@Autowired
	Client client;

	@Value("${server.port}")
	String serverPort;

	@Value("${service.user.name}")
	private String userName;

	@Value("${service.user.password}")
	private String password;

	@Value("${service.configuration.port}")
	private String configServicePort;

	@Value("${service.configuration.protocol}")
	private String configServiceProtocol;

	@Value("${service.configuration.host}")
	private String configServiceHost;

	@Value("${service.authentication.port}")
	private String authenticationServicePort;

	@Value("${service.authentication.protocol}")
	private String authenticationServiceProtocol;

	@Value("${service.authentication.host}")
	private String authenticationServiceHost;
	
	@Value("${token.cache.maxage}")
	private Long tokenCacheMaxAge;
	
	@Value("${token.cache.active}")
	private Boolean tokenCacheActive;

	private Map<String, Domain> domainList = new HashMap<>();
	private JsonHelper jsonHelper = new JsonHelper();
	private static boolean initialized = false;

	public ConfigProperties() {

	}

	public void init() throws CsServiceException {
		if (initialized) {
			return;
		}

		JsonNode loginData = login();
		readDomainConfigListFromDb(loginData);
		initialized = true;
	}

	private JsonNode login() throws CsServiceException {
		try {
			JsonNode loginBody = jsonHelper.createEmptyNode();
			loginBody = jsonHelper.addToJsonNode(loginBody, "userName", this.userName);
			loginBody = jsonHelper.addToJsonNode(loginBody, "password", this.password);

			ResponseEntity<JsonNode> responseEntity = client.postLogin(loginBody);
			return responseEntity.getBody();
		} catch (CsServiceException cse) {
			System.out.println(
					"Login fehl geschlagen. Service Provider konnte nicht authentifiziert werden. Der Service wird beendet.");
			System.exit(500);
			return null;
		}
	}

	private void readDomainConfigListFromDb(JsonNode loginData) throws CsServiceException {

		Long userId = jsonHelper.extractLongFromNode(loginData, "userId");
		String tokenId = jsonHelper.extractStringFromJsonNode(loginData, "tokenId");
		String key = "domain";

		Map<String, String> requestParams = new HashMap<>();
		Map<String, String> headers = new HashMap<>();

		requestParams.put("userId", userId.toString());
		requestParams.put("key", key);

		headers.put("requestoruserid", userId.toString());
		headers.put("tokenid", tokenId);
		headers.put("rights", "RWD+RWD");

		String action = "getConfiguration";

		String URI = configServiceProtocol + "://" + configServiceHost + ":" + configServicePort + "/" + action;

		ResponseEntity<JsonNode> responseEntity = client.sendMessageWithBodyAsOptional(URI, null, requestParams,
				headers, HttpMethod.GET);
		JsonNode responseNode = responseEntity.getBody();

		Boolean isList = jsonHelper.extractBooleanFromNode(responseNode, "isList");

		if (isList) {
			JsonNode domainListNode = jsonHelper.extractNodeFromNode(responseNode, "configurations");
			List<Object> domainNodes = jsonHelper.toArray(domainListNode, JsonNode.class);
			for (Object obj : domainNodes) {
				try {
					JsonNode objNode = JsonNode.class.cast(obj);
					String domainNodeAsString = jsonHelper.extractNodeFromNode(objNode, "value").toString();
					domainNodeAsString = prepareJsonString(domainNodeAsString);
					JsonNode domainNode = jsonHelper.getObjectMapper().readTree(domainNodeAsString);
					Domain domain = jsonHelper.getObjectMapper().treeToValue(domainNode, Domain.class);
					
					domainList.put(domain.getDomainName(), domain);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			JsonNode configurationNode = jsonHelper.extractNodeFromNode(responseNode, "configuration");
			try {
				String domainNodeAsString = jsonHelper.extractNodeFromNode(configurationNode, "value").toString();
				domainNodeAsString = prepareJsonString(domainNodeAsString);
				JsonNode domainNode = jsonHelper.getObjectMapper().readTree(domainNodeAsString);
				Domain domain = jsonHelper.getObjectMapper().treeToValue(domainNode, Domain.class);
				domainList.put(domain.getDomainName(), domain);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private String prepareJsonString(String jsonString) {
		jsonString = jsonString.trim();
		if (jsonString.contains("\\")) {
			jsonString = jsonString.replace("\\", "");
		}
		if (jsonString.contains("\"{")) {
			jsonString = jsonString.replace("\"{", "{");
		}
		if (jsonString.contains("}\"")) {
			jsonString = jsonString.replace("}\"", "}");
		}
		return jsonString;
	}

	public String getAuthenticationUrl() {
		return this.authenticationServiceProtocol + "://" + this.authenticationServiceHost + ":"
				+ this.authenticationServicePort;
	}

	public String getServiceUrl(String domainName) {
		Domain domain = domainList.get(domainName);
		if (null == domain) {
			return null;
		}
		return domain.getUrl();
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public Long getTokenCacheMaxAge() {
		return tokenCacheMaxAge;
	}

	public Boolean getTokenCacheActive() {
		return tokenCacheActive;
	}

}
