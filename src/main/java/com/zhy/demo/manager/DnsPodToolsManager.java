//package com.zhy.demo.manager;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author zhy
// */
//public class DnsPodToolsManager {
//    private static final String LOGIN_TOKEN = "201393,bd42ee7877678f04d51d83965e9d2eeb";
//    private static final String DEFAULT_DOMAIN = "pengyouai.com";
//    /**
//     * 改成自己桌面路径
//     */
//    private static final String FILE_PATH = "C:/Users/zhanghongyu/Desktop/dnspod_" + DEFAULT_DOMAIN;
//
//    private static final String RECORD_LIST_URL = "https://dnsapi.cn/Record.List";
//    private static final String RECORD_MODIFY_URL = "https://dnsapi.cn/Record.Modify";
//
//
//    public static void main(String[] args) throws Exception {
//        DnsPodToolsManager.exportRecords();
//
//        //DnsPodToolsManager.recordModify();
//    }
//
//    public static void exportRecords() throws IOException {
//        //获取记录列表
//        JSONArray records = DnsPodToolsManager.getRecordList();
//        if (records == null) {
//            return;
//        }
//        //写入excel文件
//        DnsPodToolsManager.exportExcel(records);
//    }
//
//    public static JSONArray getRecordList() {
//        //每次查100条
//        int length = 3;
//        //固定请求参数
//        HttpHeaders headers = DnsPodToolsManager.getHeaders();
//        RestTemplate restTemplate = new RestTemplate();
//        JSONArray resultRecords = new JSONArray();
//        //默认最大循环次数
//        int loop = 10;
//        for (int i = 0; i < loop; i++) {
//            //分页每次请求100条
//            MultiValueMap<String, String> body = DnsPodToolsManager.getCommonBody();
//            body.add("domain", DEFAULT_DOMAIN);
//            body.add("length", Integer.toString(length));
//            body.add("offset", "0" + (i * length));
//            HttpEntity<MultiValueMap<String, String>> requestParams = new HttpEntity<>(body, headers);
//            ResponseEntity<String> response = restTemplate.postForEntity(RECORD_LIST_URL, requestParams, String.class);
//            JSONObject result = JSONObject.parseObject(response.getBody());
//            //code!=1则打印返回值，返回null结束流程
//            if (!"1".equals(result.getJSONObject("status").getString("code"))) {
//                System.out.println(result);
//                return null;
//            }
//            JSONArray records = result.getJSONArray("records");
//            resultRecords.addAll(records);
//            if (records.size() == 0) {
//                break;
//            }
//        }
//        return resultRecords;
//    }
//
//    public static MultiValueMap<String, String> getCommonBody() {
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("login_token", LOGIN_TOKEN);
//        body.add("format", "json");
//        body.add("lang", "cn");
//        body.add("error_on_empty", "no");
//        return body;
//    }
//
//    public static HttpHeaders getHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        return headers;
//    }
//
//    public static void exportExcel(JSONArray records) throws IOException {
//        //创建工作薄对象
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        //创建工作表对象
//        HSSFSheet sheet = workbook.createSheet();
//        //创建工作表的第一行
//        HSSFRow row1 = sheet.createRow(0);
//        row1.createCell(0).setCellValue("记录ID");
//        row1.createCell(1).setCellValue("主机记录");
//        row1.createCell(2).setCellValue("记录类型");
//        row1.createCell(3).setCellValue("线路类型");
//        row1.createCell(4).setCellValue("记录值");
//        row1.createCell(5).setCellValue("权重");
//        row1.createCell(6).setCellValue("MX");
//        row1.createCell(7).setCellValue("TTL");
//        row1.createCell(8).setCellValue("最后操作时间");
//        row1.createCell(9).setCellValue("状态");
//        //遍历创建数据行
//        for (int i = 1; i <= records.size(); i++) {
//            //过滤停止状态的记录
//            JSONObject recordRow = JSONObject.parseObject(records.get(i - 1).toString());
//            if (!"1".equals(recordRow.getString("enabled"))) {
//                continue;
//            }
//            HSSFRow row = sheet.createRow(i);
//            row.createCell(0).setCellValue(recordRow.getString("id"));
//            row.createCell(1).setCellValue(recordRow.getString("name"));
//            row.createCell(2).setCellValue(recordRow.getString("type"));
//            row.createCell(3).setCellValue(recordRow.getString("line"));
//            row.createCell(4).setCellValue(recordRow.getString("value"));
//            row.createCell(5).setCellValue(recordRow.getString("weight"));
//            row.createCell(6).setCellValue(recordRow.getString("mx"));
//            row.createCell(7).setCellValue(recordRow.getString("ttl"));
//            row.createCell(8).setCellValue(recordRow.getString("updated_on"));
//            row.createCell(9).setCellValue(recordRow.getString("enabled"));
//        }
//        //文档输出
//        FileOutputStream out = new FileOutputStream(FILE_PATH + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls");
//        workbook.write(out);
//        out.close();
//    }
//
//    public static void recordModify() throws Exception {
//        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(FILE_PATH + ".xls"));
//        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
//        int lastRowNum = sheet.getLastRowNum();
//        if (lastRowNum <= 1) {
//            System.out.println("excel内无数据");
//            return;
//        }
//        RestTemplate restTemplate = new RestTemplate();
//        //固定请求参数
//        HttpHeaders headers = DnsPodToolsManager.getHeaders();
//        for (int i = 1; i <= lastRowNum; i++) {
//            MultiValueMap<String, String> body = DnsPodToolsManager.getCommonBody();
//            body.add("domain", DEFAULT_DOMAIN);
//            HSSFRow row = sheet.getRow(i);
//            //记录id
//            String recordId = row.getCell(0).getStringCellValue();
//            body.add("record_id", recordId);
//            //主机记录
//            String subDomain = row.getCell(1).getStringCellValue();
//            body.add("sub_domain", subDomain);
//            //记录类型
//            String recordType = row.getCell(2).getStringCellValue();
//            body.add("record_type", recordType);
//            //线路类型
//            String recordLine = row.getCell(3).getStringCellValue();
//            body.add("record_line", recordLine);
//            //记录值
//            String value = row.getCell(4).getStringCellValue();
//            body.add("value", value);
//            //权重，有可能为null
//            String weight;
//            if (row.getCell(5) != null) {
//                weight = row.getCell(5).getStringCellValue();
//            } else {
//                weight = null;
//            }
//            body.add("weight", weight);
//            //MX
//            String mx = row.getCell(6).getStringCellValue();
//            body.add("mx", mx);
//            //TTL
//            String ttl = row.getCell(7).getStringCellValue();
//            body.add("ttl", ttl);
//            HttpEntity<MultiValueMap<String, String>> requestParams = new HttpEntity<>(body, headers);
//            ResponseEntity<String> response = restTemplate.postForEntity(RECORD_MODIFY_URL, requestParams, String.class);
//            JSONObject result = JSONObject.parseObject(response.getBody());
//            System.out.println("i：" + i + ", 修改结果：" + result);
//            Thread.sleep(1000);
//        }
//    }
//}
