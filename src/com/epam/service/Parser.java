package com.epam.service;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class Parser {

    XSSFSheet getXssfSheet(String xlsFile, int sheetNum) throws IOException {
        File excelFile = new File(xlsFile);
        FileInputStream fis = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        try {
            return workbook.getSheetAt(sheetNum);
        }finally {
            workbook.close();
            fis.close();
        }
    }
}
