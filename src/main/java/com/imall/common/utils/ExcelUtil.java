package com.imall.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;


import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    public static List<ArrayList<String>> readXlsx (MultipartFile file) {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        InputStream input = null;
        XSSFWorkbook wb = null;

        try {
            input = file.getInputStream();
            //创建文档

            wb = new XSSFWorkbook(input);

            ArrayList<String> rowList = null;
            int totoalRows = 0;//总行数
            int totalCells = 0;//总列数
            //读取sheet(页)
            for (int sheetIndex = 0 ; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
                XSSFSheet xssfSheet = wb.getSheetAt(sheetIndex);

                if (xssfSheet == null) {
                    continue;
                }
                totoalRows = xssfSheet.getLastRowNum();
                //读取row
                for (int rowIndex = 0; rowIndex <= totoalRows; rowIndex++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowIndex);

                    if (xssfRow == null) {
                        continue;
                    }
                    rowList = new ArrayList<String>();
                    totalCells = xssfRow.getLastCellNum();

                    //读取列
                    for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
                        XSSFCell xssfCell = xssfRow.getCell(cellIndex);
                        if (xssfCell == null) {
                            rowList.add("");
                        } else {
                            xssfCell.setCellType(Cell.CELL_TYPE_STRING);
                            rowList.add(String.valueOf(xssfCell.getStringCellValue()));
                        }
                    }

                    list.add(rowList);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (wb != null) {
                    wb.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            }
        }

        return list;
    }

    /**
     * 读取 .xls内容
     * @param file
     * @return
     * @throws IOException
     */
    public static List<ArrayList<String>> readXls (MultipartFile file)  {
        List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

        //创建输入流
        InputStream input = null;
        //创建文档
        HSSFWorkbook wb = null;

        try {
            input = file.getInputStream();
            //创建文档
            wb = new HSSFWorkbook(input);

            ArrayList<String> rowList = null;
            int totoalRows = 0;//总行数
            int totalCells = 0;//总列数
            //读取sheet(页)
            for (int sheetIndex = 0 ; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
                HSSFSheet hssfSheet = wb.getSheetAt(sheetIndex);

                if (hssfSheet == null) {
                    continue;
                }

                totoalRows = hssfSheet.getLastRowNum();
                //读取row
                for (int rowIndex = 0; rowIndex <= totoalRows; rowIndex++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowIndex);

                    if (hssfRow == null) {
                        continue;
                    }
                    rowList = new ArrayList<String>();
                    totalCells = hssfRow.getLastCellNum();

                    //读取列
                    for (int cellIndex = 0; cellIndex < totalCells; cellIndex++) {
                        HSSFCell hssfCell = hssfRow.getCell(cellIndex);
                        if (hssfCell == null) {
                            rowList.add("");
                        } else {
                            hssfCell.setCellType(Cell.CELL_TYPE_STRING);
                            rowList.add(String.valueOf(hssfCell.getStringCellValue()));
                        }
                    }

                    list.add(rowList);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if ( wb != null) {
                    wb.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
            }
        }
        return list;
    }



    /**
     * 获取文件类型
     * @param path
     * @return
     */
    public static String getPostfix (String path) {
        if (StringUtils.isBlank(path) || !path.contains(".")) {
            return null;
        }
        return path.substring(path.lastIndexOf(".") + 1, path.length()).trim();
    }


}
