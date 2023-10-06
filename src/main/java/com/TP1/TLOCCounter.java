package com.TP1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TLOCCounter {

	public static void main(String[] args) {
	    String filePath = "";
	    if (args.length != 1) {
	        System.out.println("No argument provided");
	        return;
	    } else {
	        filePath = args[0]; // Assign the provided argument to filePath
	        System.out.println("Calculating TLOC for: " + filePath);
		    System.out.println("TLOC :" + calculateTLOC(filePath));
	    }
	}

    public static int calculateTLOC(String filePath) {
        int tloc = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // if empty, single or multi line comment
                if (line.isEmpty() || line.startsWith("//") || (line.startsWith("/*") && line.endsWith("*/"))) {
                    continue;
                }
                tloc++;
            }
        } catch (IOException e) {
            System.err.println("File unreadable: " + e.getMessage());
        }
        return tloc;
    }
}
