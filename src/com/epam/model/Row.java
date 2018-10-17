package com.epam.model;

public class Row {

    private int num;
    private String columnName;
    private String dataType;
    private String description;

    public Row withNum(int num){
        this.num = num;
        return this;
    }

    public Row withColumnName(String columnName){
        this.columnName = columnName;
        return this;
    }

    public Row withDataType(String dataType){
        this.dataType = dataType;
        return this;
    }

    public Row withDescription(String description){
        this.description = description;
        return this;
    }

    public int getNum() {
        return num;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }
}
