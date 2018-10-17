package com.epam.model;

import java.util.List;

public class Table {

    private String dbName;
    private String name;
    private List<Row> rows;
    private List<Column> columns;

    public Table withDbName(String dbName){
        this.dbName = dbName;
        return this;
    }

    public Table withName(String name){
        this.name = name;
        return this;
    }

    public Table withRows(List<Row> rows){
        this.rows = rows;
        return this;
    }

    public Table withColumns(List<Column> columns){
        this.columns = columns;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }
}
