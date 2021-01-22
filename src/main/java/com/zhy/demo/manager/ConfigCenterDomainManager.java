package com.zhy.demo.manager;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhy
 */
public class ConfigCenterDomainManager {
	private static final String FILE_PATH = "C:/Users/zhanghongyu/Desktop/old";

	public static void main(String[] args) throws Exception {
		ConfigCenterDomainManager.getDomain();
	}

	public static void getDomain() throws Exception {
		//定义正则表达式
		String regex = "[-a-zA-Z0-9]{0,62}\\.shanyishanmei\\.com";
		Pattern pattern = Pattern.compile(regex);

		int sheetIndex = 0;
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(FILE_PATH + ".xlsx"));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(sheetIndex);
		int lastRowNum = sheet.getLastRowNum();
		List<JSONObject> list = new ArrayList<>();
		for (int i = 1; i <= lastRowNum; i++) {
			JSONObject jsonObject = new JSONObject();
			XSSFRow row = sheet.getRow(i);
			jsonObject.put("appGroupId", row.getCell(0).getStringCellValue());
			jsonObject.put("appId", row.getCell(1).getStringCellValue());
			jsonObject.put("fileName", row.getCell(2).getStringCellValue());
			String content = row.getCell(3).getStringCellValue();
			jsonObject.put("content", content);
			Matcher matcher = pattern.matcher(content);
			String domain = "";
			while(matcher.find()){
				domain = domain + matcher.group() + ",";
			}
			jsonObject.put("domain", domain);
			list.add(jsonObject);
		}
		ConfigCenterDomainManager.exportExcel(list);
	}

	public static void exportExcel(List<JSONObject> records) throws IOException {
		//创建工作薄对象
		HSSFWorkbook workbook = new HSSFWorkbook();
		//创建工作表对象
		HSSFSheet sheet = workbook.createSheet();
		//创建工作表的第一行
		HSSFRow row1 = sheet.createRow(0);
		row1.createCell(0).setCellValue("应用组ID");
		row1.createCell(1).setCellValue("应用ID");
		row1.createCell(2).setCellValue("文件名");
		row1.createCell(3).setCellValue("shanyishanmei域名");
		row1.createCell(4).setCellValue("文件内容");
		//遍历创建数据行
		for (int i = 1; i <= records.size(); i++) {
			JSONObject recordRow = records.get(i - 1);
			HSSFRow row = sheet.createRow(i);
			row.createCell(0).setCellValue(recordRow.getString("appGroupId"));
			row.createCell(1).setCellValue(recordRow.getString("appId"));
			row.createCell(2).setCellValue(recordRow.getString("fileName"));
			row.createCell(3).setCellValue(recordRow.getString("domain"));
			row.createCell(4).setCellValue(recordRow.getString("content"));
		}
		//文档输出
		FileOutputStream out = new FileOutputStream(FILE_PATH + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls");
		workbook.write(out);
		out.close();
	}
}
