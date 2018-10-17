package com.epam.service;

import com.epam.model.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;
import java.util.stream.Collectors;

import static com.epam.model.Terms.EMPTY_STRING;
import static com.epam.model.Terms.UNEXISTING_COORD;

class ColumnService {

    Column getColumnData(String columnName, XSSFSheet sheet){
        int[] columnCoordinates = getColumnCoordinates(columnName, sheet);
        return populatedColumn(columnCoordinates[1], columnName,
                getAllColumnData(sheet, columnCoordinates[0], columnCoordinates[1]));
    }

    int[] getColumnCoordinates(String columnName, XSSFSheet sheet){
        for (Row row : sheet) {
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if(columnName.equals(cell.toString().trim())){
                    return  new int[]{cell.getRowIndex(), cell.getColumnIndex()};
                }
            }
        }
        return  new int[]{UNEXISTING_COORD, UNEXISTING_COORD};
    }

    int getColumnNum(String columnName, XSSFSheet sheet){
        return getColumnCoordinates(columnName, sheet)[1];
    }

    List<Column> getNormalizedColumns(List<Column> oldColumns){
        List<Column> newColumns = new ArrayList<>();
        List<List> lists = oldColumns.stream().map(Column::getData).collect(Collectors.toList());
        int maxListSize = getMaxListsSize(lists);
        oldColumns.forEach(column -> newColumns.add(checkSizeAndPopulate(column, maxListSize)));
        return newColumns;
    }

    private Column checkSizeAndPopulate(Column column, int maxListSize){
        int listSize = column.getData().size();
        if(listSize < maxListSize){
            List<String> newList = new ArrayList<>(column.getData());
            for(int i = 0; i < maxListSize - listSize; i++){
                newList.add(EMPTY_STRING);
            }
            column.withData(newList);
        }
        return column;
    }

    private int getMaxListsSize(List<List> lists){
        return Collections.max(lists, Comparator.comparing(List::size)).size();
    }

    private Column populatedColumn(int num, String name, List<String> data){
        return new Column().withNum(num).withName(name).withData(data);
    }

    private List<String> getAllColumnData(XSSFSheet sheet, int rowNum, int colNum){
        List<String> result = new ArrayList<>();
        if(rowNum == UNEXISTING_COORD && colNum == UNEXISTING_COORD){
            result.add(EMPTY_STRING);
        }else {
            int numberOfRows = sheet.getPhysicalNumberOfRows();
            for (int i = rowNum + 1; i < numberOfRows; i++) {
                Row currentRow = sheet.getRow(i);
                Cell cell = currentRow.getCell(colNum);
                result.add(cell != null ? StringUtils.getStringWithoutEOL(cell.toString()) : EMPTY_STRING);
            }
        }
        return result;
    }
}
