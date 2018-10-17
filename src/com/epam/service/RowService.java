package com.epam.service;

import com.epam.model.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import static com.epam.model.Terms.*;

class RowService {

    private ColumnService columnService;

    RowService(){
        columnService = new ColumnService();
    }

    @SuppressWarnings("unchecked")
    Row getTargetRowData(int rowNum, XSSFSheet sheet){
        int columnNameColNum = columnService.getColumnNum(TARGET_COLUMN_NAME, sheet);
        int dataTypeColNum = columnService.getColumnNum(TARGET_DATA_TYPE, sheet);
        int descriptionColNum = columnService.getColumnNum(TARGET_DESCRIPTION, sheet);
        return populatedTableRow(rowNum, getCellData(sheet, rowNum, columnNameColNum),
                getCellData(sheet, rowNum, dataTypeColNum),
                StringUtils.getStringWithoutEOL(getCellData(sheet, rowNum, descriptionColNum)));
    }

    @SuppressWarnings("unchecked")
    Row getRawRowData(int rowNum, XSSFSheet sheet){
        int columnNameColNum = columnService.getColumnNum(RAW_COLUMN_NAME, sheet);
        int dataTypeColNum = columnService.getColumnNum(RAW_DATA_TYPE, sheet);
        int descriptionColNum = columnService.getColumnNum(RAW_DESCRIPTION, sheet);
        return populatedTableRow(rowNum, getCellData(sheet, rowNum, columnNameColNum),
                getCellData(sheet, rowNum, dataTypeColNum),
                StringUtils.getStringWithoutEOL(getCellData(sheet, rowNum, descriptionColNum)));
    }

    boolean isRowEmpty(Row row){
        return StringUtils.isBlank(row.getColumnName()) && StringUtils.isBlank(row.getDataType()) &&
                StringUtils.isBlank(row.getDescription());
    }

    private String getCellData(XSSFSheet sheet, int rowNum, int colNum){
        if(colNum != UNEXISTING_COORD){
            Cell cell =  sheet.getRow(rowNum).getCell(colNum);
            return cell != null ? cell.toString().trim() : EMPTY_STRING;
        }else {
            return EMPTY_STRING;
        }
    }

    private Row populatedTableRow(int num, String columnName, String dataType, String description){
        return new Row().withNum(num).withColumnName(columnName)
                .withDataType(dataType).withDescription(description);
    }
}
