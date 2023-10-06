package com.TP1;
import java.io.*;
import java.util.*;

public class TROPCOMP {

    public static void main(String[] args) {
        String outputCsvFile = null;
        // Vu que je peux commencer par mon argument optionnel -o ou pas, je garde l'index de début en mémoire
        int startIndex = 0;
    	
    	if (args.length > 0 && "-o".equals(args[0])) {
            if (args.length < 4) {
                System.out.println("Arguments manquants : TROPCOMP -o <chemin-à-la-sortie.csv> <chemin-de-l'entrée> <seuil>");
                return;
            }
            
            outputCsvFile = args[1];
            startIndex = 2; // Le reste des arguments commence à l'index 2
        } else {
            if (args.length < 3) {
                System.out.println("Arguments manquants : TROPCOMP <chemin-de-l'entrée> <seuil>");
                return;
            }
            startIndex = 0; // Le reste des arguments commence à l'index 0
        }

        String folderPath = args[startIndex];
        int threshold = Integer.parseInt(args[startIndex + 1]);
        
        List<testFileData> fileDataList = new ArrayList<>();
        List<String> outputLines = new ArrayList<>();

        processFolder(folderPath, fileDataList);
        analysis(fileDataList, threshold, outputLines);

        // à réécrire
        if (outputCsvFile != null) {
            writeCsvFile(outputCsvFile, outputLines);
        }
    }

    private static void processFolder(String folderPath, List<testFileData> fileDataList) {
        try {
        	// Je passe récursivement sur tout dans le dossier
            File[] files = new File(folderPath).listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // et j'applique la récursivité sur mes sous dossiers
                    processFolder(file.getAbsolutePath(), fileDataList);
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    // et si c'est un fichier java, je le traite
                    processJavaFile(file, fileDataList);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Invalid folder path: " + folderPath);
        }
    }

    private static void processJavaFile(File javaFile, List<testFileData> fileDataList) {
        try {

            if (!javaFile.getName().endsWith(".java")) {
                System.out.println("Not a Java file: " + javaFile.getName());
                return;
            }
            
            String fileName = javaFile.getName();
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
                tloc = TLOCCounter.calculateTLOC(javaFile.getAbsolutePath());
                tassert = TASSERTCounter.countAssertions(javaFile.getAbsolutePath());
                tcm = (tassert == 0) ? 0.0 : (double) tloc / tassert;

            }

            // On crée l'objet représentant notre classe de test 
            testFileData testClass = new testFileData();
            testClass.filePath = javaFile.getAbsolutePath();
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
    
    private static void analysis(List<testFileData> fileDataList, int threshold, List<String> outputLines) {
        for (testFileData fileData: fileDataList) {
            // Vérifiez si le TLOC ou le TCM est supérieur au seuil
            boolean aboveThreshold = fileData.tloc > threshold || fileData.tcm > threshold;
            if (aboveThreshold) {
            // Génération de la sortie
            String outputLine = String.format("%s;%s;%s;%d;%d;%.2f;%s",
            		fileData.filePath,
            		fileData.packageName,
            		fileData.className,
            		fileData.tloc,
            		fileData.tassert,
            		fileData.tcm,
            		aboveThreshold);

            // J'output la line et je la garde en mémoire pour mon CSV
            outputLines.add(outputLine);
            System.out.println(outputLine);
            };
        }
    }

    
    private static void writeCsvFile(String outputCsvFile, List<String> outputLines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFile))) {
            writer.write("filePath,Package Name,Class Name,TLOC,TASSERT,TCM,Seuil dépassé?\n");
            for (String outputLine : outputLines) {
                writer.write(outputLine + "\n");
            }

            System.out.println("CSV file créé à la sortie : " + outputCsvFile);
        } catch (IOException e) {
            System.err.println("Fichier non créé : " + e.getMessage());
        }
    }


}
