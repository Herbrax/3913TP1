package com.TP1;
import java.io.*;
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

        // Recursively traverse the folder structure and collect information about test classes
        processFolder(folderPath, fileDataList);

        analysis(fileDataList, outputLines);
        
        // Write the collected information to a CSV file
        writeCsvFile(outputCsvFile, outputLines);
    }

    private static void processFolder(String folderPath, List<testFileData> fileDataList) {
        try {
        	// Je passe récursivement sur tout dans le dossier
            File[] files = new File(folderPath).listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // et j'applique la récursivité sur mes sous dossiers
                    processFolder(file.getAbsolutePath(), fileDataList);
                } else if (file.isFile() && file.getName().endsWith("Test.java")) {
                    // et si c'est un fichier java de test, je le traite
                    processJavaTestFile(file, fileDataList);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Invalid folder path: " + folderPath);
        }
    }

    private static void processJavaTestFile(File javaTestFile, List<testFileData> fileDataList) {
        try {

            if (!javaTestFile.getName().endsWith(".java")) {
                System.out.println("Not a Java file: " + javaTestFile.getName());
                return;
            }
            
            String fileName = javaTestFile.getName();
            String className = fileName.substring(0, fileName.length() - ".java".length());
            String packageName = null;
            int tloc = 0;
            int tassert = 0;
            double tcm = 0;
            // Janky but works, on cherche le packagename dans le fichier
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("package ")) {
                        packageName = line.substring("package ".length()).replace(";", "").trim();
                        break;
                    }
                }

                // on réutilise TLOCCounter et TASSERTCOunter pour avoir nos valeurs
                tloc = TLOCCounter.calculateTLOC(javaTestFile.getAbsolutePath());
                tassert = TASSERTCounter.countAssertions(javaTestFile.getAbsolutePath());
                tcm = (tassert == 0) ? 0.0 : (double) tloc / tassert;

            }

            // On crée l'objet représentant notre classe de test 
            testFileData testClass = new testFileData();
            testClass.filePath = javaTestFile.getAbsolutePath();
            testClass.packageName = packageName;
            testClass.className = className;
            testClass.tloc = tloc;
            testClass.tassert = tassert;
            testClass.tcm = tcm;
            fileDataList.add(testClass);

        } catch (IOException e) {
            System.err.println("File unreadable: " + e.getMessage());
        }
    }

    private static void analysis(List<testFileData> fileDataList, List<String> outputLines) {
        for (testFileData fileData: fileDataList) {
            // Je génère mon output
            String outputLine = String.format("%s;%s;%s;%d;%d;%.2f;%s",
            		fileData.filePath,
            		fileData.packageName,
            		fileData.className,
            		fileData.tloc,
            		fileData.tassert,
            		fileData.tcm);

            // J'output la line et je la garde en mémoire pour mon CSV
            outputLines.add(outputLine);
            System.out.println(outputLine);
        }
    }
    
    private static void writeCsvFile(String outputCsvFile, List<String> outputLines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFile))) {
            writer.write("filePath,Package Name,Class Name,TLOC,TASSERT,TCM");
            for (String outputLine : outputLines) {
                writer.write(outputLine + "\n");
            }

            System.out.println("CSV file créé à la sortie : " + outputCsvFile);
        } catch (IOException e) {
            System.err.println("Fichier non créé : " + e.getMessage());
        }
    }
}
