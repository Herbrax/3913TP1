package com.TP1;
import java.io.*;
import java.util.*;

import com.TP1.TLS.TestClass;

public class TROPCOMP {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Arguments manquants : java TROPCOMP <folder_path> <threshold> [-o <output_csv_file>]");
            return;
        }

        String folderPath = args[0];
        int threshold = Integer.parseInt(args[1]);
        String outputCsvFile = null;
        if (args.length == 4 && "-o".equals(args[2])) {
            outputCsvFile = args[3];
        }
        
        List<TestClass> testClassList = new ArrayList<>();
        processFolder(folderPath, testClassList);
        
        // à réécrire
        if (outputCsvFile != null) {
            writeCsvFile(outputCsvFile, testClassList);
        }
    }

    private static void processFolder(String folderPath, List<TestClass> testClassList) {
        try {
        	// Je passe récursivement sur tout dans le dossier
            File[] files = new File(folderPath).listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    // et j'applique la récursivité sur mes sous dossiers
                    processFolder(file.getAbsolutePath(), testClassList);
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    // et si c'est un fichier java, je le traite
                    processJavaFile(file, testClassList);
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Invalid folder path: " + folderPath);
        }
    }

    private static void processJavaFile(File javaTestFile, List<TestClass> testClassList) {
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
            TestClass testClass = new TestClass();
            testClass.filePath = javaTestFile.getAbsolutePath();
            testClass.packageName = packageName;
            testClass.className = className;
            testClass.tloc = tloc;
            testClass.tassert = tassert;
            testClass.tcm = tcm;
            testClassList.add(testClass);

        } catch (IOException e) {
            System.err.println("File unreadable: " + e.getMessage());
        }
    }
    
    private static void writeCsvFile(String outputCsvFile, List<TestClass> testClassList) {
        // Votre logique pour écrire les informations dans un fichier CSV
        // Formattez les données et écrivez-les dans le format souhaité.
    }

    // Define a class to store information about test classes
    static class TestInfo {
        String filePath;
        String packageName;
        String className;
        int tloc;
        int tassert;
        double tcm;

        TestInfo(String filePath, String packageName, String className, int tloc, int tassert, double tcm) {
            this.filePath = filePath;
            this.packageName = packageName;
            this.className = className;
            this.tloc = tloc;
            this.tassert = tassert;
            this.tcm = tcm;
        }
    }
}
