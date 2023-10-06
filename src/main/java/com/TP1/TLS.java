package com.TP1;
import java.util.*;

public class TLS {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Arguments manquants : java TLS <folder_path> <output_csv_file>");
            return;
        }
        String folderPath = args[0];
        String outputCsvFile = args[1];
        List<testFileData> fileDataList = new ArrayList<>();
        List<String> outputLines = new ArrayList<>();
        // Je traverse récursivement mon dossier à la recherche de fichiers java
        Processing.processFolder(folderPath, fileDataList);
        Processing.analysis(fileDataList, outputLines);
        // J'output tout en CSV (dans tous les cas, pas comme avec TROPCOMP)
        String firstline = "filePath,Package Name,Class Name,TLOC,TASSERT,TCM\n";
        Processing.writeCsvFile(outputCsvFile, outputLines,firstline);
    } 
}
