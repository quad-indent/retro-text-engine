package storyBits;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class FileParser {
    public static String joinConfigFolder(String fileName) {
        if (fileName == null) {
            return null;
        }
        return "configs/" + fileName;
    }
    public static List<String> parseFile(String fileName, String hasToStartWith, boolean negateStartStr) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            fileName = "textAdv.txt";
        }
        boolean shouldSkipLine;
        try {
            GlobalConf.issueLog("Attempting to open " + fileName + ". . .",
                    GlobalConf.SEVERITY_LEVEL_INFO);
            List<String> storyLines = new ArrayList<>();
            FileReader storyFile = new FileReader(fileName);
            Scanner storyReader = new Scanner(storyFile);
            String thisLine = "";
            while (storyReader.hasNextLine()) {
                thisLine = storyReader.nextLine();
                shouldSkipLine = !thisLine.startsWith(hasToStartWith);
                if (negateStartStr) {
                    shouldSkipLine = !shouldSkipLine;
                }
                if (shouldSkipLine)
                    continue;
                storyLines.add(thisLine);
            }
            storyReader.close();
            return storyLines;
        } catch (FileNotFoundException e) {
            GlobalConf.issueLog(e.getMessage(), GlobalConf.SEVERITY_LEVEL_ERROR, false);
        }
        return null;
    }
    public static Map<String, String> prettifyParsedPlayerSheetConfig(List<String> parsedLinez) throws Exception {
        Map<String, String> returnal = new LinkedHashMap<>();
        for (String curEntry: parsedLinez) {
            if (curEntry.charAt(0) != '[') {
                GlobalConf.issueLog("Wrong format than what was expected! " +
                        "Expected each line to start with [keyword]!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
            }
            String[] tempiez = curEntry.substring(1).split("]");
            if (tempiez.length != 2) {
                GlobalConf.issueLog("Wrong format than what was expected in configs/" +
                        "playerSheetConfig.txt! Expected a keyword-value pair, " +
                        "instead got " + tempiez.length + " elements!", GlobalConf.SEVERITY_LEVEL_ERROR,
                        true);
            }
            tempiez[1] = StoryDisplayer.removeWhiteSpace(tempiez[1]);
            returnal.put(tempiez[0].toLowerCase(), tempiez[1]);
        }
        return returnal;
    }
}