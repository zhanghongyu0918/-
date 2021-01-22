//package com.zhy.demo.manager;
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * @author zhy
// */
//public class GatewayManager {
//	private static final Profile TESTING = new Profile("http://172.21.60.209:8080/proxy", "YWRtaW46YXJr");
//	//private static final Profile PRODUCTION_WEB = new Profile("http://172.20.241.73:8080/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");
//	//private static final Profile PRODUCTION_APP = new Profile("http://172.20.241.73:8090/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");
//
//	//private final String OLD_DOMAIN = "zhy.com";
//	//private final String NEW_DOMAIN = "zhy1.com";
//
//	private final Profile profile;
//	private final String SERVICES_URL;
//
//	public GatewayManager(Profile profile) {
//		this.profile = profile;
//		Objects.requireNonNull(profile);
//		String baseUrl = Objects.requireNonNull(profile.getBaseUrl());
//		Objects.requireNonNull(profile.getBasicAuth());
//		SERVICES_URL = baseUrl + "/services?";
//	}
//
//	public static void main(String[] args) {
//		new GatewayManager(TESTING).getOldDomainAndCreateNew();
//	}
//
//	public void getOldDomainAndCreateNew() {
//		List<JSONObject> services = getServices();
//	}
//
//	public List<JSONObject> getServices() {
//		String servicesUrl = SERVICES_URL;
//		List<JSONObject> services = new ArrayList<>();
//		while (true) {
//			@SuppressWarnings("unchecked")
//			JSONObject base = get(servicesUrl, JSONObject.class);
//			if (base == null) {
//				break;
//			}
//			if (base.getJSONArray("data") != null) {
//				services.addAll(base.getData());
//			}
//			if (base.getNext() != null) {
//				servicesUrl = SERVICES_URL.replace("/services?", base.getNext());
//			} else {
//				break;
//			}
//		}
//		return services;
//	}
//
//
//	private HttpHeaders getHeaders() {
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.add("Authorization", "Basic " + profile.getBasicAuth());
//		return requestHeaders;
//	}
//
//	private <T> T get(String url, Class<T> responseType) {
//		RestTemplate template = new RestTemplate();
//		try {
//			ResponseEntity<T> responseEntity = template.exchange(url, HttpMethod.GET, new HttpEntity<>(null, getHeaders()), responseType);
//			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
//		} catch (HttpClientErrorException e) {
//			return null;
//		}
//	}
//
//	private <T> T put(String url, Map<String, Object> body, Class<T> responseType) {
//		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
//		if (response.getStatusCodeValue() != 200 && response.getStatusCodeValue() != 201) {
//			System.out.println("put failure:" + body);
//		}
//		System.out.println("put response:" + response.getBody());
//		return response.getBody();
//	}
//
//	private <T> T patch(String url, Map<String, Object> data, Class<T> responseType) {
//		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//		try {
//			StringBuilder builder = new StringBuilder(url);
//			builder.append("?");
//			data.forEach((k, v) -> builder.append(k).append("=").append(v));
//			ResponseEntity<T> responseEntity = template.exchange(builder.toString(), HttpMethod.PATCH, new HttpEntity<>(null, getHeaders()), responseType);
//			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
//		} catch (HttpClientErrorException e) {
//			return null;
//		}
//	}
//}
