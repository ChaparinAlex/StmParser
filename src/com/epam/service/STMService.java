package com.epam.service;

import com.epam.model.Table;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.io.IOException;

public class STMService {

    private Parser parser;
    private TableService tableService;
    private FileBuilder fileBuilder;
    private ScriptBuilder scriptBuilder;

    public STMService(){
        parser = new Parser();
        tableService = new TableService();
        fileBuilder = new FileBuilder();
        scriptBuilder = new ScriptBuilder();
    }

    public void buildTemplates(String xlsFile, int sheetNum) throws IOException {
        XSSFSheet sheet = parser.getXssfSheet(xlsFile, sheetNum);
        Table targetTable = tableService.getTargetTable(sheet);
        Table rawTable = tableService.getRawTable(sheet);
        String subDirAbsPath = getCreatedSubdirectory(targetTable, xlsFile);
        buildDdlScript(targetTable, subDirAbsPath);
        buildDmlScript(targetTable, rawTable, subDirAbsPath);
    }

    private String getCreatedSubdirectory(Table targetTable, String xlsFile){
        String tableName = targetTable.getName().toUpperCase();
        String sourceDir = fileBuilder.getDirectory(xlsFile);
        String subDir = tableName + "_HQL";
        return fileBuilder.getCreatedDirectoryName(sourceDir, subDir);
    }

    private void buildDdlScript(Table targetTable, String dirPath){
        String tableName = targetTable.getName().toUpperCase();
        String fileDdlAbsPath = dirPath + File.separator + tableName + ".hql";
        fileBuilder.createFile(fileDdlAbsPath);
        String ddlScript = scriptBuilder.getDdlScript(targetTable);
        fileBuilder.writeToFile(ddlScript, fileDdlAbsPath);
    }

    private void buildDmlScript(Table targetTable, Table rawTable, String dirPath){
        String targetTableName = targetTable.getName().toUpperCase();
        String fileDmlAbsPath = dirPath + File.separator + targetTableName + "_FILL" + ".hql";
        fileBuilder.createFile(fileDmlAbsPath);
        String dmlScript = scriptBuilder.getDmlScript(targetTable, rawTable);
        fileBuilder.writeToFile(dmlScript, fileDmlAbsPath);
    }
}
