package com.zhy.demo.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhy.demo.dto.gateway.Profile;
import com.zhy.demo.util.HttpUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author zhy
 */
public class GatewayManager {
	private static final Profile TESTING = new Profile("http://172.21.60.209:8080/proxy", "YWRtaW46YXJr");
	//private static final Profile PRODUCTION_WEB = new Profile("http://172.20.241.73:8080/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");
	//private static final Profile PRODUCTION_APP = new Profile("http://172.20.241.73:8090/proxy", "YWRtaW46SjJjSFRNYW9pYUtqSm52cA==");

	private static final String OLD_DOMAIN = "zhy.com";
	private static final String NEW_DOMAIN = "renrenaiche.com";
	private static final String SERVICES_PREFIX = "change_domain_";

	public static void main(String[] args) throws IOException {
		new GatewayManager(TESTING).getOldDomainAndCreateNew();

		//new GatewayManager(TESTING).batchCreateNewServiceAndRoute();
	}

	private final Profile profile;
	private final String SERVICES_URL;
	private final String PLUGINS_URL;
	private final String SERVICE_ALL_ROUTES;
	private final String CREATE_SERVICE_URL;
	private final String CREATE_ROUTE_URL;
	private final String CREATE_PLUGIN_URL;
	private final String API_PATH_URL;
	private final String API_PERMISSION_URL;
	private final String CREATE_API_PATH_URL;
	private final String CREATE_API_PERSMISSION_URL;

	public GatewayManager(Profile profile) {
		this.profile = profile;
		Objects.requireNonNull(profile);
		String baseUrl = Objects.requireNonNull(profile.getBaseUrl());
		Objects.requireNonNull(profile.getBasicAuth());
		SERVICES_URL = baseUrl + "/services?size=1000";
		PLUGINS_URL = baseUrl + "/plugins?size=1000";
		SERVICE_ALL_ROUTES = baseUrl + "/services/%s/routes?";
		CREATE_SERVICE_URL = baseUrl + "/services";
		CREATE_ROUTE_URL = baseUrl + "/routes";
		CREATE_PLUGIN_URL = baseUrl + "/plugins";
		API_PATH_URL = baseUrl + "/routes/%s/business_apipaths";
		API_PERMISSION_URL = baseUrl + "/business_apipaths/%s/apis_permission";
		CREATE_API_PATH_URL = baseUrl + "/business_apipaths";
		CREATE_API_PERSMISSION_URL = baseUrl + "/apis_permission";
	}

	public void getOldDomainAndCreateNew() {
		//查询全部service
		List<JSONObject> services = getByUrl(SERVICES_URL);
		//查询全部plugin
		List<JSONObject> plugins = getByUrl(PLUGINS_URL);
		for (JSONObject service : services) {
			String serviceId = service.getString("id");
			String serviceName = service.getString("name");
			//查询service的全部route
			List<JSONObject> routes = getByUrl(String.format(SERVICE_ALL_ROUTES, serviceId));
			if (routes.size() == 0) {
				System.out.println(serviceName + " : " + routes.size());
				//continue;
			}
			//routeHost是否包含旧域名
			//boolean flag = false;
			//for (JSONObject route : routes) {
			//	if (route.getJSONArray("hosts") == null || route.getJSONArray("hosts").size() == 0) {
			//		continue;
			//	}
			//	List<String> routeHosts = JSONObject.parseArray(route.getJSONArray("hosts").toJSONString(), String.class);
			//	//目前每个route只配一个host
			//	if (routeHosts.get(0).contains(OLD_DOMAIN)) {
			//		flag = true;
			//		break;
			//	}
			//}
			////如果有新域名直接false
			//for (JSONObject route : routes) {
			//	if (route.getJSONArray("hosts") == null || route.getJSONArray("hosts").size() == 0) {
			//		continue;
			//	}
			//	List<String> routeHosts = JSONObject.parseArray(route.getJSONArray("hosts").toJSONString(), String.class);
			//	//目前每个route只配一个host
			//	if (routeHosts.get(0).contains(NEW_DOMAIN)) {
			//		System.out.println(serviceName + " 已经配置过了使用新域名作为host的route");
			//		flag = false;
			//		break;
			//	}
			//}
			////false跳出当前循环
			//if (!flag) {
			//	continue;
			//}
			////查找此service是否配置了cas和permission插件
			//JSONObject casPlugin = getPluginByServiceId(plugins, serviceId, "rrc-cas");
			//JSONObject permissionPlugin = getPluginByServiceId(plugins, serviceId, "rrc-permission");
			//if (casPlugin == null) {
			//	//在现有service上创建新域名的route
			//	createNewRoutes(routes, serviceId);
			//} else {
			//	// 创建新的service
			//	JSONObject newService = createNewService(service);
			//	if (newService == null) {
			//		System.out.println(serviceName + "在创建新service时发生异常");
			//		break;
			//	}
			//	String newServiceId = newService.getString("id");
			//	//创建新的route
			//	List<JSONObject> newRoutes = createNewRoutes(routes, newServiceId);
			//	//创建新的插件
			//	createCasPlugin(casPlugin, newServiceId);
			//	if (permissionPlugin != null) {
			//		createPermissionPlugin(permissionPlugin, newServiceId);
			//		//添加权限码
			//		addPermissionCode(routes.get(0).getString("id"), newRoutes.get(0).getString("id"), newServiceId);
			//	}
			//}
		}
	}

	public List<JSONObject> getByUrl(String url) {
		JSONObject result = HttpUtil.get(url, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
		if (result == null) {
			return new ArrayList<>();
		}
		return JSONObject.parseArray(result.getJSONArray("data").toJSONString(), JSONObject.class);
	}

	public JSONObject getPluginByServiceId(List<JSONObject> plugins, String serviceId, String pluginName) {
		for (JSONObject plugin : plugins) {
			if (serviceId.equals(plugin.getString("service_id")) && pluginName.equals(plugin.getString("name"))) {
				return plugin;
			}
		}
		return null;
	}

	public List<JSONObject> createNewRoutes(List<JSONObject> routes, String serviceId) {
		List<JSONObject> newRoutes = new ArrayList<>();
		for (JSONObject route : routes) {
			//route中的host如果是null，跳出当前循环
			JSONArray hosts = route.getJSONArray("hosts");
			if (hosts == null || hosts.size() == 0) {
				continue;
			}
			JSONArray newHosts = new JSONArray();
			if (hosts.getString(0).contains(OLD_DOMAIN)) {
				String newHost = hosts.getString(0).replace(OLD_DOMAIN, NEW_DOMAIN);
				newHosts.add(newHost);
			} else {
				continue;
			}
			Map<String, Object> params = new HashMap<>(8);
			JSONObject service = new JSONObject();
			service.put("id", serviceId);
			params.put("service", service);
			params.put("protocols", route.getJSONArray("protocols"));
			if (route.getJSONArray("methods") != null) {
				params.put("methods", route.getJSONArray("methods"));
			}
			params.put("hosts", newHosts);
			params.put("paths", route.getJSONArray("paths"));
			if (route.getBoolean("strip_path") != null) {
				params.put("strip_path", route.getBoolean("strip_path"));
			}
			if (route.getBoolean("preserve_host") != null) {
				params.put("preserve_host", route.getBoolean("preserve_host"));
			}
			if (route.getInteger("regex_priority") != null) {
				params.put("regex_priority", route.getInteger("regex_priority"));
			}
			newRoutes.add(HttpUtil.post(CREATE_ROUTE_URL, params, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth())));
		}
		return newRoutes;
	}

	public JSONObject createNewService(JSONObject service) {
		Map<String, Object> params = new HashMap<>(16);
		if (service.getString("name").contains(SERVICES_PREFIX)) {
			System.out.println(service.getString("name") + "已经存在，请删除干净后再重新创建");
			return null;
		}
		params.put("name", SERVICES_PREFIX + service.getString("name"));
		params.put("protocol", service.getString("protocol"));
		params.put("host", service.getString("host"));
		params.put("port", service.getInteger("port"));
		params.put("path", service.getString("path"));
		if (service.getInteger("retries") != null) {
			params.put("retries", service.getInteger("retries"));
		}
		if (service.getInteger("connect_timeout") != null) {
			params.put("connect_timeout", service.getInteger("connect_timeout"));
		}
		if (service.getInteger("write_timeout") != null) {
			params.put("write_timeout", service.getInteger("write_timeout"));
		}
		if (service.getInteger("read_timeout") != null) {
			params.put("read_timeout", service.getInteger("read_timeout"));
		}
		if (service.getString("url") != null) {
			params.put("url", service.getString("url"));
		}
		return HttpUtil.post(CREATE_SERVICE_URL, params, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
	}

	public void createCasPlugin(JSONObject casPlugin, String serviceId) {
		Map<String, Object> newConfig = new HashMap<>(8);
		JSONObject config = casPlugin.getJSONObject("config");
		newConfig.put("business_domain", config.getString("business_domain").replace(OLD_DOMAIN, NEW_DOMAIN));
		newConfig.put("cas_by", config.getString("cas_by"));
		newConfig.put("cas_callback_expire", config.getInteger("cas_callback_expire"));
		newConfig.put("cas_host", config.getString("cas_host"));
		newConfig.put("redis_database", config.getInteger("redis_database"));
		newConfig.put("redis_host", config.getString("redis_host"));
		newConfig.put("redis_password", config.getString("redis_password"));
		newConfig.put("redis_port", config.getInteger("redis_port"));
		newConfig.put("redis_timeout", config.getInteger("redis_timeout"));
		newConfig.put("shiro_expire", config.getInteger("shiro_expire"));
		newConfig.put("shiro_secret", config.getString("shiro_secret"));
		Map<String, Object> params = new HashMap<>(8);
		params.put("name", "rrc-cas");
		params.put("service_id", serviceId);
		params.put("enabled", casPlugin.getBoolean("enabled"));
		params.put("config", newConfig);
		HttpUtil.post(CREATE_PLUGIN_URL, params, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
	}

	public void createPermissionPlugin(JSONObject permissionPlugin, String serviceId) {
		Map<String, Object> newConfig = new HashMap<>(8);
		JSONObject config = permissionPlugin.getJSONObject("config");
		newConfig.put("guest", config.getBoolean("guest"));
		newConfig.put("octo_expire", config.getInteger("octo_expire"));
		newConfig.put("octo_host", config.getString("octo_host"));
		newConfig.put("octo_key", config.getString("octo_key"));
		newConfig.put("octo_secret", config.getString("octo_secret"));
		newConfig.put("permissions_by", config.getString("permissions_by"));
		newConfig.put("permissions_center_host", config.getString("permissions_center_host"));
		Map<String, Object> params = new HashMap<>(8);
		params.put("name", "rrc-permission");
		params.put("service_id", serviceId);
		params.put("enabled", permissionPlugin.getBoolean("enabled"));
		params.put("config", newConfig);
		HttpUtil.post(CREATE_PLUGIN_URL, params, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
	}

	public void addPermissionCode(String routeId, String newRouteId, String newServiceId) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> apiPaths = HttpUtil.get(String.format(API_PATH_URL, routeId), List.class, HttpUtil.getHeaders(profile.getBasicAuth()));
		if (apiPaths == null || apiPaths.size() == 0) {
			return;
		}
		for (Map<String, Object> apiPath : apiPaths) {
			String apiPathId = apiPath.get("id").toString();
			Map<String, Object> apiPathParams = new HashMap<>(8);
			apiPathParams.put("service_id", newServiceId);
			apiPathParams.put("route_id", newRouteId);
			apiPathParams.put("methods", apiPath.get("methods"));
			apiPathParams.put("used", Integer.valueOf(apiPath.get("used").toString()));
			apiPathParams.put("path", apiPath.get("path").toString());
			apiPathParams.put("path_type", apiPath.get("path_type").toString());
			JSONObject apiPathResult = HttpUtil.post(CREATE_API_PATH_URL, apiPathParams, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
			String newApiPathId = apiPathResult.getString("id");

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> apiPermissions = HttpUtil.get(String.format(API_PERMISSION_URL, apiPathId), List.class, HttpUtil.getHeaders(profile.getBasicAuth()));
			if (apiPermissions == null || apiPermissions.size() == 0) {
				continue;
			}
			for (Map<String, Object> apiPermission : apiPermissions) {
				if (!StringUtils.isEmpty(apiPathId)) {
					Map<String, Object> apiPermissionParams = new HashMap<>();
					apiPermissionParams.put("apis_id", newApiPathId);
					apiPermissionParams.put("permission_code", apiPermission.get("permission_code"));
					apiPermissionParams.put("used", Integer.valueOf(apiPermission.get("used").toString()));
					HttpUtil.post(CREATE_API_PERSMISSION_URL, apiPermissionParams, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
				}
			}
		}
	}

	public void batchCreateNewServiceAndRoute() throws IOException {
		final String filePath = "C:/Users/zhanghongyu/Desktop/renrenaiche.xlsx";
		final String newDomain = ".renrenaiche.cn";
		int sheetIndex = 0;
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(filePath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(sheetIndex);
		int lastRowNum = sheet.getLastRowNum();
		Map<String, List<String>> map = new HashMap<>(64);
		for (int i = 0; i <= lastRowNum; i++) {
			XSSFRow row = sheet.getRow(i);
			String routeHost = row.getCell(0).getStringCellValue();
			routeHost += newDomain;
			String serviceHost = row.getCell(1).getStringCellValue();
			List<String> routeHostList;
			if (map.containsKey(serviceHost)) {
				routeHostList = map.get(serviceHost);
				routeHostList.add(routeHost);
			} else {
				routeHostList = new ArrayList<>();
				routeHostList.add(routeHost);
			}
			map.put(serviceHost, routeHostList);
		}
		map.forEach((serviceHost, routeHostList) -> {
			Map<String, Object> serviceParams = new HashMap<>(16);
			String name = "batch_create_" + UUID.randomUUID().toString().substring(28);
			serviceParams.put("name", name);
			serviceParams.put("protocol", "http");
			serviceParams.put("host", serviceHost);
			serviceParams.put("port", 80);
			JSONObject result = HttpUtil.post(CREATE_SERVICE_URL, serviceParams, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
			String serviceId = result.getString("id");
			routeHostList.forEach(routeHost -> {
				Map<String, Object> routeParams = new HashMap<>(8);
				JSONObject service = new JSONObject();
				service.put("id", serviceId);
				routeParams.put("service", service);
				List<String> hostList = new ArrayList<>();
				hostList.add(routeHost);
				routeParams.put("hosts", hostList);
				List<String> paths = new ArrayList<>();
				paths.add("/");
				routeParams.put("paths", paths);
				routeParams.put("strip_path", true);
				routeParams.put("preserve_host", false);
				HttpUtil.post(CREATE_ROUTE_URL, routeParams, JSONObject.class, HttpUtil.getHeaders(profile.getBasicAuth()));
			});
		});
	}
}
