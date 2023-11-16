package storyBits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class FileParser {
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
    public static Map<String, String> prettifyParsedPlayerSheetConfig(List<String> parsedLinez) {
        Map<String, String> returnal = new LinkedHashMap<>();
        for (String curEntry: parsedLinez) {
            if (curEntry.charAt(0) != '[') {
                throw new AssertionError("Wrong format than what was expected! " +
                        "Expected each line to start with [keyword]!");
            }
            String[] tempiez = curEntry.substring(1).split("]");
            if (tempiez.length != 2) {
                throw new AssertionError("Wrong format than what was expected in playerSheetConfig.txt!" +
                        " Expected a keyword-value pair, instead got " + tempiez.length + " elements!");
            }
            tempiez[1] = StoryDisplayer.removeWhiteSpace(tempiez[1]);
            returnal.put(tempiez[0].toLowerCase(), tempiez[1]);
        }
        return returnal;
    }
}