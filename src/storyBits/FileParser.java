package storyBits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class FileParser {
    public static List<String> parseFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            fileName = "textAdv.txt";
        }
        try {
            System.out.println("Attempting to open " + fileName + ". . .");
            List<String> storyLines = new ArrayList<String>();
            FileReader storyFile = new FileReader(fileName);
            Scanner storyReader = new Scanner(storyFile);
            String thisLine = "";
            while (storyReader.hasNextLine()) {
                thisLine = storyReader.nextLine();
                if (!thisLine.startsWith("["))
                    continue;
                storyLines.add(thisLine);
            }
            storyReader.close();
            return storyLines;
        } catch (FileNotFoundException e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
        return null;
    }
}