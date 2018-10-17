package com.epam.service;

import com.epam.model.Table;
import com.epam.model.Column;
import com.epam.model.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;
import java.util.stream.Collectors;

import static com.epam.model.Terms.*;

class TableService {

    private ColumnService columnService;
    private RowService rowService;

    TableService(){
        columnService = new ColumnService();
        rowService = new RowService();
    }

    Table getRawTable(XSSFSheet sheet){
        Column rawTableNameData = columnService.getColumnData(RAW_TABLE_NAME, sheet);
        String tableName = getCommonTableName(rawTableNameData.getData());
        List<Row> rows = getDataRows(sheet,
                columnService.getColumnCoordinates(RAW_TABLE_NAME, sheet)[0], true);
        List<Column> columns = new ArrayList<>();
        Arrays.asList(RAW_COLUMN_NAME, RAW_DATA_TYPE, RAW_DESCRIPTION)
                .forEach(colName -> columns.add(columnService.getColumnData(colName, sheet)));
        return populatedTable(RAW_DB_NAME, tableName, rows, columns);
    }

    Table getTargetTable(XSSFSheet sheet){
        Column targetTableNameData = columnService.getColumnData(TARGET_TABLE_NAME, sheet);
        String tableName = getCommonTableName(targetTableNameData.getData());
        List<Row> rows = getDataRows(sheet,
                columnService.getColumnCoordinates(TARGET_TABLE_NAME, sheet)[0], false);
        List<Column> columns = new ArrayList<>();
        Arrays.asList(TARGET_COLUMN_NAME, TARGET_DATA_TYPE, TARGET_DESCRIPTION)
                .forEach(colName -> columns.add(columnService.getColumnData(colName, sheet)));
        return populatedTable(TARGET_DB_NAME, tableName, rows, columnService.getNormalizedColumns(columns));
    }

    private List<Row> getDataRows(XSSFSheet sheet, int startPos, boolean isRawData){
        List<Row> result = new ArrayList<>();
        for(int i = startPos; i < sheet.getPhysicalNumberOfRows(); i++){
            result.add(isRawData ? rowService.getRawRowData(i, sheet) : rowService.getTargetRowData(i, sheet));
        }
        return result.stream().filter(row -> !rowService.isRowEmpty(row)).collect(Collectors.toList());
    }

    private Table populatedTable(String dbName, String name, List<Row> rows, List<Column> columns){
        return new Table().withDbName(dbName).withName(name).withRows(rows).withColumns(columns);
    }

    private String getCommonTableName(List<String> tableNameData){
        List<String> tableNames = tableNameData.stream().filter(name ->
                !StringUtils.isBlank(name)).distinct().collect(Collectors.toList());
        Map<String, Integer> frequencies = new HashMap<>();
        tableNames.forEach(name -> frequencies.put(name, Collections.frequency(tableNameData, name)));
        return Collections.max(frequencies.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }
}
