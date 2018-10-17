package com.epam.service;

import com.epam.model.Table;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

class ScriptBuilder {

    private static final String DOUBLE_HYPHEN = "--";
    private static final String EOL = "\n";
    private static final String WS = " ";
    private static final String DELIM = ";";
    private static final String COMMENT = "COMMENT";
    private static final String QUOT = "'";
    private static final String COMMA = ",";
    private static final String SET = "SET";
    private static final String EQUAL_SIGN = "=";
    private static final String UNDEFINED = "'UNDEFINED'";
    private static final String AS = "AS";
    private static final String FROM = "FROM";
    private static final String POINT = ".";

    private StringBuilder sb;

    ScriptBuilder(){
        sb = new StringBuilder();
    }

    String getDdlScript(Table table){
        String tableName = table.getName();
        completeHeader(tableName, "Create table", tableName.toUpperCase() + ".hql");
        appendSymbols(2, EOL);
        appendSymbols(1, "USE ");
        appendSymbols(1, table.getDbName());
        appendSymbols(1, DELIM);
        appendSymbols(2, EOL);
        appendSymbols(1, "DROP TABLE IF EXISTS ");
        appendSymbols(1, tableName);
        appendSymbols(1, DELIM);
        appendSymbols(1, EOL);
        appendSymbols(1, "CREATE EXTERNAL TABLE ");
        appendSymbols(1, tableName);
        appendSymbols(1, EOL);
        appendSymbols(1, "(");
        appendSymbols(1, EOL);

        appendTableBody(table);

        appendSymbols(1, ")");
        appendSymbols(1, EOL);

        completeFooter(tableName);

        String result = sb.toString();
        flushStringBuilder();
        return result;
    }

    String getDmlScript(Table targetTable, Table rawTable){
        String targetTableName = targetTable.getName();
        completeHeader(targetTableName, "Populate table", targetTableName.toUpperCase() + "_FILL.hql");
        appendSymbols(2, EOL);
        appendSettings(getDmlScriptSettings());
        appendSymbols(1, EOL);
        appendSymbols(1, "USE ");
        appendSymbols(1, targetTable.getDbName());
        appendSymbols(1, DELIM);
        appendSymbols(2, EOL);
        appendSymbols(1, "INSERT OVERWRITE TABLE ");
        appendSymbols(1, targetTableName.toUpperCase());
        appendSymbols(1, EOL);
        appendSymbols(1, "SELECT");
        appendSymbols(1, EOL);

        appendPopulateDataFromTables(targetTable, rawTable);
        appendSymbols(1, FROM);
        appendSymbols(1, WS);
        appendSymbols(1, rawTable.getDbName().toLowerCase());
        appendSymbols(1, POINT);
        appendSymbols(1, rawTable.getName().toLowerCase());
        appendSymbols(1, EOL);
        appendSymbols(1, DELIM);

        String result = sb.toString();
        flushStringBuilder();
        return result;
    }

    private void appendPopulateDataFromTables(Table targetTable, Table rawTable){
        List<String> rawColumns = getTransformedRawColumns(rawTable.getColumns().get(0).getData());
        List<String> targetColumns = targetTable.getColumns().get(0).getData();
        int maxRawColumnNameLength = maxStringLength(rawColumns);
        for(int i = 0; i < rawColumns.size(); i++){
            appendSymbols(2, WS);
            String rawColName = rawColumns.get(i);
            appendSymbols(1, rawColName);
            appendSymbols(wsNumber(rawColName, maxRawColumnNameLength), WS);
            appendSymbols(1, AS);
            appendSymbols(1, WS);
            appendSymbols(1, targetColumns.get(i).toLowerCase());
            if(i != rawColumns.size() - 1){
                appendSymbols(1, COMMA);
            }
            appendSymbols(1, EOL);
        }
    }

    private List<String> getTransformedRawColumns(List<String> rawColumns){
        return rawColumns.stream().map(this::populatedEmptyString).collect(Collectors.toList());
    }

    private String populatedEmptyString(String data){
        return StringUtils.isBlank(data) ? UNDEFINED : data.toLowerCase();
    }

    private void appendTableBody(Table table){
        List<String> columnNames = table.getColumns().get(0).getData();
        int maxColumnNameLength = maxStringLength(columnNames);

        List<String> dataTypes = table.getColumns().get(1).getData();
        int maxDataTypeLength = maxStringLength(dataTypes);

        List<String> descriptions = table.getColumns().get(2).getData();

        for(int i = 0; i < columnNames.size(); i++){
            String curColName = columnNames.get(i);
            appendSymbols(1, curColName);
            appendSymbols(wsNumber(curColName, maxColumnNameLength), WS);
            String curDataType = dataTypes.get(i);
            appendSymbols(1, curDataType);
            appendSymbols(wsNumber(curDataType, maxDataTypeLength), WS);
            appendSymbols(1, COMMENT);
            appendSymbols(1, WS);
            surroundWithQuotes(descriptions.get(i));
            if(i != columnNames.size() - 1){
                appendSymbols(1, COMMA);
            }
            appendSymbols(1, EOL);
        }
    }

    private void surroundWithQuotes(String text){
        appendSymbols(1, QUOT);
        appendSymbols(1, text);
        appendSymbols(1, QUOT);
    }

    private int wsNumber(String currentString, int maxLength){
        return maxLength + 3 - currentString.length();
    }

    private int maxStringLength(List<String> strings){
        String max = Collections.max(strings, Comparator.comparing(String::length));
        return max.length();
    }

    private void completeHeader(String tableName, String scriptType, String scriptName){
        appendSymbols(38, DOUBLE_HYPHEN);
        addStandardSymbols();
        appendSymbols(1, scriptName);
        addStandardSymbols();
        appendSymbols(1, scriptType);
        appendSymbols(1, WS);
        appendSymbols(1, tableName.toUpperCase());
        addStandardSymbols();
        appendSymbols(1, "Revision history");
        addStandardSymbols();
        appendSymbols(1, "Date");
        appendSymbols(8, WS);
        appendSymbols(1, "Author");
        appendSymbols(11, WS);
        appendSymbols(1, "Description");
        appendSymbols(1, EOL);
        appendSymbols(38, DOUBLE_HYPHEN);
        appendSymbols(1, EOL);
        appendSymbols(1, DOUBLE_HYPHEN);
        appendSymbols(1, WS);
        appendSymbols(1, LocalDate.now().toString());
        appendSymbols(2, WS);
        appendSymbols(1, "<NAME>.<SURNAME>");
        appendSymbols(4, WS);
        appendSymbols(1, "<LINK_TO_JIRA_TICKET>");
        appendSymbols(1, EOL);
        appendSymbols(38, DOUBLE_HYPHEN);
    }

    private void completeFooter(String tableName){
        appendSymbols(1, COMMENT);
        appendSymbols(1, WS);
        surroundWithQuotes(tableName);
        appendSymbols(1, EOL);
        appendSymbols(1, "PARTITIONED BY (<COL_NAME> <DATA_TYPE>)");
        appendSymbols(1, EOL);
        appendSymbols(1, "STORED AS ORC");
        appendSymbols(1, EOL);
        appendSymbols(1, "TBLPROPERTIES(\"orc.compress\"=\"ZLIB\")");
        appendSymbols(1, EOL);
        appendSymbols(1, DELIM);
        appendSymbols(1, EOL);
        appendSymbols(1, "ALTER TABLE ");
        appendSymbols(1, tableName);
        appendSymbols(1, WS);
        appendSymbols(1, "ENABLE NO_DROP");
        appendSymbols(1, DELIM);
    }

    private void addStandardSymbols(){
        appendSymbols(1, EOL);
        appendSymbols(1, DOUBLE_HYPHEN);
        appendSymbols(1, EOL);
        appendSymbols(1, DOUBLE_HYPHEN);
        appendSymbols(1, WS);
    }

    private void appendSymbols(int num, String symbol){
        for(int i = 0; i < num; i++){
            sb.append(symbol);
        }
    }

    private void flushStringBuilder(){
        sb = new StringBuilder();
    }

    private Map<String, String> getDmlScriptSettings(){
        Map<String, String> settingsMap = new HashMap<>();
        settingsMap.put("hive.default.fileformat", "orc");
        settingsMap.put("hive.exec.orc.default.compress", "ZLIB");
        settingsMap.put("hive.exec.orc.encoding.strategy", "SPEED");
        settingsMap.put("hive.exec.orc.default.stripe.size", "67108864");
        settingsMap.put("hive.exec.orc.zerocopy", "true");
        settingsMap.put("hive.exec.dynamic.partition", "true");
        settingsMap.put("hive.exec.dynamic.partition.mode", "nonstrict");
        settingsMap.put("hive.exec.max.dynamic.partitions.pernode", "10000");
        settingsMap.put("hive.exec.max.dynamic.partitions", "10000");
        settingsMap.put("hive.exec.max.created.files", "150000");
        settingsMap.put("hive.exec.parallel", "true");
        settingsMap.put("hive.execution.engine", "tez");
        return  settingsMap;
    }

    private void appendSettings(Map<String, String> dmlScriptSettings){
        int maxLengthSettingsKey = maxStringLength(new ArrayList<>(dmlScriptSettings.keySet()));
        dmlScriptSettings.forEach((k, v) -> performAppendingSettings(k, v, maxLengthSettingsKey));
    }

    private void performAppendingSettings(String key, String value, int keyMaxLength){
        appendSymbols(1, SET);
        appendSymbols(1, WS);
        appendSymbols(1, key);
        appendSymbols(wsNumber(key, keyMaxLength), WS);
        appendSymbols(1, EQUAL_SIGN);
        appendSymbols(1, value);
        appendSymbols(1, DELIM);
        appendSymbols(1, EOL);
    }
}
