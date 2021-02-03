package com.zhy.demo.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author zhy
 */
public class HttpUtil {

	public static String doGet(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}


	public static String doPostJson(String url, String json) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                assert response != null;
                response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resultString;
	}

	public static void doDelete(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			// 创建Http Delete请求
			HttpDelete httpDelete = new HttpDelete(uri);
			// 执行http请求
			response = httpClient.execute(httpDelete);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String doPut(String url) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Put请求
			HttpPut httpPut = new HttpPut(url);
			// 执行http请求
			response = httpClient.execute(httpPut);
			resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doPutHttpRequest(String url, Map<String, String> headerMap, String requestBody) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String entityStr = null;
		CloseableHttpResponse response;
		try {
			HttpPut post = new HttpPut(url);
			//添加头部信息
			for (Map.Entry<String, String> header : headerMap.entrySet()) {
				post.addHeader(header.getKey(), header.getValue());
			}
			HttpEntity entity = new StringEntity(requestBody, "Utf-8");
			System.out.println("请求体是：" + requestBody);
			post.setEntity(entity);
			response = httpClient.execute(post);
			// 获得响应的实体对象
			HttpEntity httpEntity = response.getEntity();
			// 使用Apache提供的工具类进行转换成字符串
			entityStr = EntityUtils.toString(httpEntity, "UTF-8");
			System.out.println("PUT请求结果：" + entityStr);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.close();
		}
		return entityStr;
	}


	public static HttpHeaders getHeaders(String basicAuth) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic " + basicAuth);
		return requestHeaders;
	}

	public static <T> T get(String url, Class<T> responseType, HttpHeaders headers) {
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<T> responseEntity = template.exchange(url, HttpMethod.GET, new org.springframework.http.HttpEntity<>(null, headers), responseType);
			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
		} catch (HttpClientErrorException e) {
			return null;
		}
	}

	public static <T> T post(String url, Map<String, Object> body, Class<T> responseType, HttpHeaders headers) {
		org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
		if (response.getStatusCodeValue() != 200 && response.getStatusCodeValue() != 201) {
			System.out.println("post failure:" + body);
		}
		System.out.println("post response:" + response.getBody());
		return response.getBody();
	}

	public static <T> T patch(String url, Map<String, Object> data, Class<T> responseType, HttpHeaders headers) {
		RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		try {
			StringBuilder builder = new StringBuilder(url);
			builder.append("?");
			data.forEach((k, v) -> builder.append(k).append("=").append(v));
			ResponseEntity<T> responseEntity = template.exchange(builder.toString(), HttpMethod.PATCH, new org.springframework.http.HttpEntity<>(null, headers), responseType);
			return responseEntity.getStatusCodeValue() == 200 ? responseEntity.getBody() : null;
		} catch (HttpClientErrorException e) {
			return null;
		}
	}
}
