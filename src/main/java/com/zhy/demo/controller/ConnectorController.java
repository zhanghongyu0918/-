package com.zhy.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.zhy.demo.dto.connector.CreateDTO;
import com.zhy.demo.dto.connector.DeleteDTO;
import com.zhy.demo.util.ConnectorParamsUtil;
import com.zhy.demo.util.HttpUtil;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhy
 */
@RestController
@RequestMapping("/connector")
public class ConnectorController {
	private static final String CONNECTOR_URL = "http://localhost:8083/connectors/";

	@PostMapping("/create")
	public String create(@RequestBody CreateDTO createDTO) {
		try {
			List<JSONObject> paramsList = getParams(createDTO);
			for (JSONObject jsonObject : paramsList) {
				String s = HttpUtil.doPostJson(createDTO.getUrl(), jsonObject.toJSONString());
				System.out.println(s);
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	private List<JSONObject> getParams(@RequestBody CreateDTO createDTO) throws Exception {
		List<JSONObject> paramsList;
		switch (createDTO.getType()) {
			case "mysqlSource":
				paramsList = ConnectorParamsUtil.getMysqlSourceData(createDTO);
				break;
			case "httpSource":
				paramsList = ConnectorParamsUtil.getHttpSourceData(createDTO);
				break;
			case "messagebusSource":
				paramsList = ConnectorParamsUtil.getMessagebusSourceData(createDTO);
				break;
			case "jdbcSink":
				paramsList = ConnectorParamsUtil.getJdbcSinkData(createDTO);
				break;
			case "httpSink":
				paramsList = ConnectorParamsUtil.getHttpSinkData(createDTO);
				break;
			case "messagebusSink":
				paramsList = ConnectorParamsUtil.getMessagebusSinkData(createDTO);
				break;
			case "rrcElasticSearchSink":
				paramsList = ConnectorParamsUtil.getRrcElasticSearchSinkData(createDTO);
				break;
			case "jdbcAdaptive":
				paramsList = ConnectorParamsUtil.getJdbcAdaptiveData(createDTO);
				break;
			case "elasticSearchAdaptive":
				paramsList = ConnectorParamsUtil.getElasticSearchAdaptiveData(createDTO);
				break;
			case "httpAdaptive":
				paramsList = ConnectorParamsUtil.getHttpAdaptiveData(createDTO);
				break;
			default:
				throw new Exception("error");
		}
		return paramsList;
	}

	@PostMapping("/delete")
	public void delete(@RequestBody DeleteDTO deleteDTO) {
		Integer start = deleteDTO.getStart();
		for (int i = start; i <= deleteDTO.getEnd(); i++) {
			String name = deleteDTO.getName() + i;
			HttpUtil.doDelete(deleteDTO.getUrl() + name, null);
			System.out.println(i);
		}
	}

	@PostMapping("/update")
	public String update(@RequestBody CreateDTO createDTO) {
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Content-Type", "application/json;charset=utf-8");
		try {
			List<JSONObject> paramsList = getParams(createDTO);
			for (JSONObject jsonObject : paramsList) {
				String name = jsonObject.getString("name");
				String params = jsonObject.getString("config");
				String url = CONNECTOR_URL + name + "/config";
				String result = HttpUtil.doPutHttpRequest(url, headerMap, params);
				System.out.println(result);
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("/getAllStatus")
	public String getAllConnectorStatus() {
		String result1 = HttpUtil.doGet(CONNECTOR_URL);
		String[] array = result1.replace("[\"", "").replace("\"]", "").replace("\"", "").split(",");
		JSONObject jsonObject = new JSONObject();
		for (String s : array) {
			if ("".equals(s) || s == null || "[]".equals(s)) {
				continue;
			}
			String url2 = CONNECTOR_URL + s + "/tasks/0/status";
			String result2 = HttpUtil.doGet(url2);
			jsonObject.put(s, result2);
		}
		return jsonObject.toJSONString();
	}

	@GetMapping("/changeEsUrl")
	public void changeEsOldToNew() {
		String result1 = HttpUtil.doGet(CONNECTOR_URL);
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Content-Type", "application/json;charset=utf-8");
		String[] array = result1.replace("[\"", "").replace("\"]", "").replace("\"", "").split(",");
		for (String name : array) {
			if ("".equals(name) || name == null || "[]".equals(name)) {
				continue;
			}
			String url2 = CONNECTOR_URL + name;
			String result2 = HttpUtil.doGet(url2);
			JSONObject config = JSONObject.parseObject(result2).getJSONObject("config");
			if ("com.renrenche.kafka.connect.elasticsearch.ElasticsearchSinkConnector".equals(config.getString("connector.class"))) {
				if ("rrcElasticSearchSink_1123_1_1".equals(name)) {
					config.put("connection.url", "http://172.17.0.1:9200");
					String url = CONNECTOR_URL + "test" + "/config";
					try {
						HttpUtil.doPutHttpRequest(url, headerMap, config.toJSONString());
						Thread.sleep(5000);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@PostMapping("/createByAllParams")
	public String createByAllParams(@RequestBody JSONObject jsonObject) {
		try {
			return HttpUtil.doPostJson(CONNECTOR_URL, jsonObject.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

	public static void main(String[] args) {
		String url1 = "http://172.20.33.42:8083/connectors/";
		String result1 = HttpUtil.doGet(url1);
		String[] array = result1.replace("[\"", "").replace("\"]", "").replace("\"", "").split(",");
		for (String name : array) {
			if ("".equals(name) || name == null || "[]".equals(name)) {
				continue;
			}
			String url2 = url1 + name;
			String result2 = HttpUtil.doGet(url2);
			JSONObject config = JSONObject.parseObject(result2).getJSONObject("config");
			//if (config.getString("topics") != null && config.getString("topics").contains("dc.car_painter.cp_used_car.source4.rrc.cp_used_car")) {
			//    System.out.println(name);
			//}
			if (config.getString("connector.class").contains("io.debezium.connector.mysql.MySqlConnector")) {
				if (config.getString("snapshot.select.statement.overrides") != null) {
					System.out.println(name);
				}
			}
		}
	}
}
