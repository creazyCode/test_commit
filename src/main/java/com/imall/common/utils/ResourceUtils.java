package com.imall.common.utils;

import java.io.*;
import java.util.*;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtils {
	public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);
	
	/**
	 * 获得java临时文件目录
	 * 
	 * @return
	 */
	public static String getTempFileDirectory(){
		return System.getProperty("java.io.tmpdir") + getFileSeparator();
	}
	
	/**
	 * 得到文件系统目录分隔符
	 * 
	 * @return
	 */
	public static String getFileSeparator(){
		return System.getProperty("file.separator");
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	public static Configuration loadConfig(String fileName) {
		Configuration config = null;
    	try{
    		Parameters params = new Parameters();
    		FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
    				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
    				.configure(params.properties().setFileName(fileName));
    		builder.setAutoSave(false);
    	    config = builder.getConfiguration();
    	}catch(Exception e){
    		LOGGER.error("load config error", e);
    	}
    	return config;
    }
	
	/**
	 * @param file 文件相对路径，例如：/proxy/proxy_load.pid
	 * @return
	 */
	public static List<String> loadFile(String file) {
		List<String> contentList = new ArrayList<String>();
		InputStream inputStream = getFileInputStream(file);
		if(inputStream != null){
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String temp = null;
			try {
				while((temp = reader.readLine()) != null){  
					if(!StringUtils.isBlank(temp)){
						contentList.add(temp);
					}
				}
			} catch (IOException e) {
				LOGGER.error("parse " + file + " error, ignored", e);
			}
		}
		return contentList;
	}

	private static InputStream getFileInputStream(String file){
		InputStream inputStream = ResourceUtils.class.getResourceAsStream(file);
		if(inputStream == null){
			File fileObject = new File(file);
			if (fileObject.exists()){
				try {
					inputStream = new FileInputStream(fileObject);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		if(inputStream == null){
			File fileObject = new File(file);
			if (fileObject.exists()){
				try {
					inputStream = new FileInputStream(fileObject);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return inputStream;
	}
	
	public static String getJavaHome(){
		String javaHome = System.getProperty("java.home");
		return javaHome;
	}
	
	public static String getJavaCacertsPath(){
		String path = "/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home/jre/lib/security/cacerts";
		String os = System.getProperty("os.name");
		String pathSpliter = null;
		if(os.toLowerCase().startsWith("win")){
			pathSpliter = "\\";
		}else{
			pathSpliter = "/";
		}
		String javaHome = System.getProperty("java.home");
		if(!StringUtils.isBlank(javaHome)){
			String cacertsPath = pathSpliter + "lib" + pathSpliter + "security" + pathSpliter + "cacerts";
			if(!javaHome.contains(pathSpliter + "jre")){
				cacertsPath = pathSpliter + "jre" + cacertsPath;
			}
			path = javaHome + cacertsPath;
		}
		return path;
	}
	
	/**
	 * @param filePath
	 * @return
	 */
	public static Map<String, Map<String, List<String>>> readExcelWithTitle(String filePath){
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toLowerCase();
		InputStream is = null;
		Workbook wb = null;
		Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String, List<String>>>();
		try {
//			is = ResourceUtils.class.getResourceAsStream(filePath);
//			is = new FileInputStream(filepath);
			is = getFileInputStream(filePath);
			if (is != null && fileType.equals("xls")) {
				wb = new HSSFWorkbook(is);
			}
			// else if (fileType.equals("xlsx")) {
			// wb = new XSSFWorkbook(is);
			// }
			else {
				return null;
			}
			
			int sheetSize = wb.getNumberOfSheets();
			for (int i = 0; i < sheetSize; i++) {// 遍历sheet页
				Sheet sheet = wb.getSheetAt(i);
				Map<String, List<String>> sheetContent = new HashMap<String, List<String>>();
				map.put(sheet.getSheetName(), sheetContent);
				
				List<String> titles = new ArrayList<String>();// 放置所有的标题
				
				int rowSize = sheet.getLastRowNum() + 1;
				for (int j = 0; j < rowSize; j++) {// 遍历行
					Row row = sheet.getRow(j);
					if (row == null) {// 略过空行
						continue;
					}
					int cellSize = row.getLastCellNum();// 行中有多少个单元格，也就是有多少列
					if (j == 0) {// 第一行是标题行
						for (int k = 0; k < cellSize; k++) {
							Cell cell = row.getCell(k);
							if(cell != null && !StringUtils.isBlank(cell.toString())){
								titles.add(cell.getStringCellValue().trim());
								List<String> columnContent = new ArrayList<String>();
								sheetContent.put(cell.getStringCellValue().trim(), columnContent);
							}else{
								titles.add("");
							}
						}
					} else {// 其他行是数据行
						for (int k = 0; k < titles.size(); k++) {
							FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
							Cell cell = row.getCell(k);
							String key = titles.get(k);
							List<String> columnContent = sheetContent.get(key);
							if (cell != null && columnContent != null) {
								CellValue cellValue = evaluator.evaluate(cell);
								if(cellValue != null && cellValue.getCellType() == CellType.NUMERIC.getCode()){
									columnContent.add(String.valueOf(cellValue.getNumberValue()));
								}else{
									columnContent.add(cell.toString().trim());
								}
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

    /**
     * @param file
     * @return
     */
    public static String loadFileToString(String file){
	    String string = null;
		try {
		    File fileObject = new File(file);
		    if(fileObject.exists()){
                string = FileUtils.readFileToString(fileObject, "UTF-8");
            }
		} catch (IOException e) {
			LOGGER.error("load file: " + file + " error", e);
		}
		return string;
	}

    /**
     * @param file
     * @return
     */
    public static String loadFileToString(File file){
        String string = null;
        try {
            if(file.exists()){
                string = FileUtils.readFileToString(file, "UTF-8");
            }
        } catch (IOException e) {
            LOGGER.error("load file: " + file + " error", e);
        }
        return string;
    }

    /**
     * @param file
     * @return
     */
    public static byte[] loadFileToByteArray(String file){
	    byte[] bytes = null;
		try {
            File fileObject = new File(file);
            if(fileObject.exists()) {
                bytes = FileUtils.readFileToByteArray(fileObject);
            }
		} catch (IOException e) {
			LOGGER.error("load file: " + file + " error", e);
		}
		return bytes;
	}

	/**
	 * @param file
	 * @param content
	 */
	public static void writeToFile(File file, String content){
		try {
			FileUtils.write(file, content);
		} catch (IOException e) {
			LOGGER.error("write to file: " + file.getAbsolutePath() + " error", e);
		}
	}

	/**
	 * @param file
	 * @param contents
	 */
	public static void writeToFile(File file, Collection<?> contents){
		try {
			FileUtils.writeLines(file, contents);
		} catch (IOException e) {
			LOGGER.error("write to file: " + file.getAbsolutePath() + " error", e);
		}
	}

	/**
	 * @param file
	 * @param content
	 */
	public static void appendToFile(File file, String content){
		try {
		    String existedFileContent = "";
		    if(file.exists()){
                existedFileContent = ResourceUtils.loadFileToString(file);
                if(!StringUtils.isBlank(existedFileContent)){
                    existedFileContent += "\n";
                }
            }
			FileUtils.write(file, existedFileContent + content);
		} catch (IOException e) {
			LOGGER.error("append to file: " + file.getAbsolutePath() + " error", e);
		}
	}

	/**
	 * @param file
	 * @param contents
	 */
	public static void appendToFile(File file, Collection<?> contents){
		try {
		    List<String> existedLines = null;
		    if(file.exists()){
		        existedLines = FileUtils.readLines(file,"UTF-8");
		        if(existedLines != null && !existedLines.isEmpty()){
                    List<String> temp = (List<String>) contents;
                    contents = new ArrayList<String>();
                    ((List<String>)contents).addAll(existedLines);
                    ((List<String>)contents).addAll(temp);
                }
            }
			FileUtils.writeLines(file, contents);
		} catch (IOException e) {
			LOGGER.error("append to file: " + file.getAbsolutePath() + " error", e);
		}
	}

	/**
	 * @param file
	 * @param bytes
	 */
	public static void writeToFile(File file, byte[] bytes){
		try {
            FileUtils.writeByteArrayToFile(file, bytes);
		} catch (IOException e) {
			LOGGER.error("write to file: " + file.getAbsolutePath() + " error", e);
		}
	}

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if(file != null && file.exists()){
	        file.delete();
        }
    }
}
