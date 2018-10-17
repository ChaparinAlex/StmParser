package com.epam.model;

import java.util.List;

public class Column {

    private int num;
    private String name;
    private List<String> data;

    public Column withNum(int num){
        this.num = num;
        return this;
    }

    public Column withName(String name){
        this.name = name;
        return this;
    }

    public Column withData(List<String> data){
        this.data = data;
        return this;
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public List<String> getData() {
        return data;
    }
}
