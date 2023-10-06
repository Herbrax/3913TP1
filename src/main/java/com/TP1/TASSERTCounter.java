package com.TP1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TASSERTCounter {

    public static void main(String[] args) {
    	String filePath = "";
	    if (args.length != 1) {
            System.out.println("Arguments manquants : java TASSERTCounter <filePath>");
	        return;
	    } else {
	        filePath = args[0]; // Assign the provided argument to filePath
	        System.out.println("Calculating TASSERT for: " + filePath);
		    System.out.println("TASSERT :" + countAssertions(filePath));
	    }
    }

    public static int countAssertions(String filePath) {
        int assertionCount = 0;
        boolean junit = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	if (!junit && (line.contains("import org.junit.Assert;") || line.contains("import org.junit.jupiter.api.Assertions;"))) {
    				junit = true; // JUnit Assert exists
    			}
            	// Source : https://junit.org/junit4/javadoc/latest/org/junit/Assert.html
            	if (line.contains("assertArrayEquals") ||
            			line.contains("assertEquals") ||
            			line.contains("assertNotEquals") ||
            			line.contains("assertTrue") ||
            			line.contains("assertFalse") ||
             			line.contains("assertSame") ||
            			line.contains("assertNotSame") ||
            			line.contains("assertThrows") ||
            			line.contains("assertNull") ||
            			line.contains("assertThat") ||
            			line.contains("fail")) {
                    assertionCount++;
                }
            }
        } catch (IOException e) {
            System.err.println("File unreadable: " + e.getMessage());
        }
        if(!junit) {
        	System.err.println("Yo y'a pas de junit.Assert dans ton code boy");
        }
        return assertionCount;
    }
}