package com.TP1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Processing {
	 static void processFolder(String folderPath, List<testFileData> fileDataList) {
	        try {
	        	// 1. Je passe récursivement sur tout dans le dossier
	            File[] files = new File(folderPath).listFiles();
	            for (File file : files) {
	                if (file.isDirectory()) {
	                    // 1.1 et j'applique la récursivité sur mes sous dossiers
	                    processFolder(file.getAbsolutePath(), fileDataList);
	                } else if (file.isFile() && file.getName().endsWith("Test.java")) {
	                    // 1.2 et si c'est un fichier java de test, je le traite
	                    processJavaTestFile(file, fileDataList);
	                }
	            }
	        } catch (NullPointerException e) {
	            System.err.println("Invalid folder path: " + folderPath);
	        }
	    }

	 private static void processJavaTestFile(File javaTestFile, List<testFileData> fileDataList) {
	        try {
	        	// 1. Je vérifie que je fait bien affaire avec un ficier java
	            if (!javaTestFile.getName().endsWith(".java")) {
	                System.out.println("Not a Java file: " + javaTestFile.getName());
	                return;
	            }
	            // 2. On gather nos informations 
	            String fileName = javaTestFile.getName();
	            String className = fileName.substring(0, fileName.length() - ".java".length());
	            String packageName = null;
	            int tloc = 0;
	            int tassert = 0;
	            double tcm = 0;
	            // 2.1 Janky but works, on cherche le packagename dans le fichier
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

	            // 3. On crée l'objet représentant notre classe de test 
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

	 static void writeCsvFile(String outputCsvFile, List<String> outputLines, String firstLine) {
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFile))) {
			 writer.write(firstLine);
			 // 1. Je lis toutes les lignes sorties à la console sauvegardées et je les écrit :
			 for (String outputLine : outputLines) {
				 writer.write(outputLine + "\n");
			 }
			 System.out.println("CSV file créé à la sortie : " + outputCsvFile);
		 } catch (IOException e) {
			 System.err.println("Fichier non créé : " + e.getMessage());
		 }
	 }
	 // Analysis sans threshold
	 static void analysis(List<testFileData> fileDataList, List<String> outputLines) {
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
	 // Analysis avec threshold
	 static void analysis(List<testFileData> fileDataList, int threshold, List<String> outputLines) {
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
	 }