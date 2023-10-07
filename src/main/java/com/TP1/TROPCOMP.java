package com.TP1;
import java.util.*;

public class TROPCOMP {
    public static void main(String[] args) {
        String outputCsvFile = null;
        // Vu que je peux commencer par mon argument optionnel -o ou pas, je garde l'index de début en mémoire
        int startIndex = 0;
    	if (args.length > 0 && "-o".equals(args[0])) {
            if (args.length < 4) {
                System.out.println("Arguments manquants : TROPCOMP -o <chemin-à-la-sortie.csv> <chemin-de-l'entrée> <seuil>");
                //return;
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
        outputCsvFile = "outputt.csv";
        List<testFileData> fileDataList = new ArrayList<>();
        List<String> outputLines = new ArrayList<>();
        Processing.processFolder(folderPath, fileDataList);
        Processing.analysis(fileDataList, threshold, outputLines);
        // J'output tout en CSV si un output à été fourni
        if (outputCsvFile != null) {
        	String firstline = "filePath,Package Name,Class Name,TLOC,TASSERT,TCM,Seuil dépassé?\n";
            Processing.writeCsvFile(outputCsvFile, outputLines,firstline);
        }
    }
}
