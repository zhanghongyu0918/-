package com.zhy.demo.manager;

import com.alibaba.fastjson.JSONObject;
import com.zhy.demo.dto.gateway.Profile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhy
 */
public class GatewayGetServiceHostByDomain {

	private static final Profile TESTING = new Profile("http://172.21.60.209:8080/proxy", "YWRtaW46YXJr");
	private static final Profile PRODUCTION_WEB = new Profile("http://172.20.241.73:8080/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");
	private static final Profile PRODUCTION_APP = new Profile("http://172.20.241.73:8090/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");

	private static final String FILE_PATH = "C:/Users/zhanghongyu/Desktop/gateway_domain";
	private final Profile profile;
	private final String ROUTES_URL;
	private final String SERVICE_URL;

	public GatewayGetServiceHostByDomain(Profile profile) {
		this.profile = profile;
		Objects.requireNonNull(profile);
		String baseUrl = Objects.requireNonNull(profile.getBaseUrl());
		Objects.requireNonNull(profile.getBasicAuth());
		ROUTES_URL = baseUrl + "/routes?";
		SERVICE_URL = baseUrl + "/services/";
	}

	public static void main(String[] args) throws IOException {
		new GatewayGetServiceHostByDomain(TESTING).start();
	}

	public void start() throws IOException {
		List<JSONObject> routes = getRoutes();
		System.out.println("routes.size(): " + routes.size());
		getServiceAndExport(routes);
	}

	public List<JSONObject> getRoutes() {
		String routesUrl = ROUTES_URL;
		List<JSONObject> routes = new ArrayList<>();
		while (true) {
			JSONObject result = get(routesUrl, JSONObject.class);
			if (result == null) {
				break;
			}
			if (result.getJSONArray("data") != null) {
				routes.addAll(JSONObject.parseArray(result.getJSONArray("data").toJSONString(), JSONObject.class));
			}
			if (result.getString("next") != null) {
				routesUrl = ROUTES_URL.replace("/routes?", result.getString("next"));
			} else {
				break;
			}
		}
		List<JSONObject> shanyishanmeiRoutes = new ArrayList<>();
		for (JSONObject route : routes) {
			if (route.getJSONArray("hosts") == null) {
				continue;
			}
			List<String> hosts = JSONObject.parseArray(route.getJSONArray("hosts").toJSONString(), String.class);
			for (String host : hosts) {
				if (host != null && host.contains(".shanyishanmei.com")) {
					shanyishanmeiRoutes.add(route);
					break;
				}
			}
		}
		return shanyishanmeiRoutes;
	}

	private void getServiceAndExport(List<JSONObject> routes) throws IOException {
		//创建工作薄对象
		HSSFWorkbook workbook = new HSSFWorkbook();
		//创建工作表对象
		HSSFSheet sheet = workbook.createSheet();
		//创建工作表的第一行
		HSSFRow row1 = sheet.createRow(0);
		row1.createCell(0).setCellValue("routeHosts");
		row1.createCell(1).setCellValue("routePaths");
		row1.createCell(2).setCellValue("serviceHost");
		row1.createCell(3).setCellValue("servicePath");
		for (int i = 0; i < routes.size(); i++) {
			JSONObject route = routes.get(i);
			if (route.getJSONArray("hosts") == null) {
				continue;
			}
			List<String> hostsList = JSONObject.parseArray(route.getJSONArray("hosts").toJSONString(), String.class);
			String routeHosts = StringUtils.join(hostsList.toArray(), ",");
			String routePaths = "";
			if (route.getJSONArray("paths") != null) {
				List<String> pathsList = JSONObject.parseArray(route.getJSONArray("paths").toJSONString(), String.class);
				routePaths = StringUtils.join(pathsList.toArray(), ",");
			}
			String serviceId = route.getJSONObject("service").getString("id");
			String getServiceUrl = SERVICE_URL + serviceId;
			JSONObject result = get(getServiceUrl, JSONObject.class);
			String serviceHost = "";
			String servicePath = "";
			if (result != null) {
				serviceHost = result.getString("host");
				servicePath = result.getString("path");
			}
			HSSFRow row = sheet.createRow(i + 1);
			row.createCell(0).setCellValue(routeHosts);
			row.createCell(1).setCellValue(routePaths);
			row.createCell(2).setCellValue(serviceHost);
			row.createCell(3).setCellValue(servicePath);
		}
		//文档输出
		FileOutputStream out = new FileOutputStream(FILE_PATH + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls");
		workbook.write(out);
		out.close();
	}

	private HttpHeaders getHeaders() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic " + profile.getBasicAuth());
		return requestHeaders;
	}

	private <T> T get(String url, Class<T> responseType) {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<T> responseEntity = template.exchange(url, HttpMethod.GET, new HttpEntity<>(null, getHeaders()), responseType);
			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
		} catch (HttpClientErrorException e) {
			return null;
		}
	}

	private <T> T put(String url, Map<String, Object> body, Class<T> responseType) {
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, getHeaders());
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
		if (response.getStatusCodeValue() != 200 && response.getStatusCodeValue() != 201) {
			System.out.println("put failure:" + body);
		}
		System.out.println("put response:" + response.getBody());
		return response.getBody();
	}

	private <T> T patch(String url, Map<String, Object> data, Class<T> responseType) {
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		try {
			StringBuilder builder = new StringBuilder(url);
			builder.append("?");
			data.forEach((k, v) -> builder.append(k).append("=").append(v));
			ResponseEntity<T> responseEntity = template.exchange(builder.toString(), HttpMethod.PATCH, new HttpEntity<>(null, getHeaders()), responseType);
			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
		} catch (HttpClientErrorException e) {
			return null;
		}
	}
}
