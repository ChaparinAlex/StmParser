package com.epam.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

class FileBuilder {

    String getCreatedDirectoryName(String currentDirPath, String dirName){
        String dirNameAbsPath = currentDirPath + File.separator + dirName;
        if(isDirExists(dirNameAbsPath)){
            dirNameAbsPath = dirNameAbsPath + "_" + LocalDateTime.now().getNano();
        }
        new File(dirNameAbsPath).mkdir();
        return dirNameAbsPath;
    }

    void createFile(String fileAbsPath){
        try {
            new File(fileAbsPath).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file " + fileAbsPath + " due to: " + e.getLocalizedMessage());
        }
    }

    void writeToFile(String content, String fileAbsPath){
        Path path = Paths.get(fileAbsPath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)){
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write to file " + fileAbsPath + " due to: " + e.getLocalizedMessage());
        }
    }

    String getDirectory(String fileAbsPath){
        return Paths.get(fileAbsPath).getParent().toString();
    }

    boolean isDirExists(String dirPath){
        File file = new File(dirPath);
        return file.exists() && file.isDirectory();
    }

}
