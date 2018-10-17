package com.epam.service;

public class StringUtils {

    public static boolean isBlank(String data){
        return data == null || data.trim().isEmpty();
    }

    static String getStringWithoutEOL(String data){
        return data.replaceAll("\\s+"," ");
    }
}
