package combat;

import foeTypes.*;
import player.PlayerClass;
import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryDisplayer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FoeParser {

    /**
     * Performs random number generation upon argz specified by string
     */
    public static String handleRandomNumz(String rawStr) throws Exception {
        String methodName = "genRandomNum";
        if (!rawStr.contains(methodName)) {
            return rawStr;
        }
        String tempBeginning = getString(rawStr, methodName);
        String[] paramz = tempBeginning.split(",");
        if (paramz.length != 2) {
            GlobalConf.issueLog("Got a mismatch in a custom Foe config string! Expected 2 " +
                    "arguments for genRandomNum, found " + paramz.length + "!", GlobalConf.SEVERITY_LEVEL_ERROR,
                    false);
            return null;
        }
        String arg1 = StoryDisplayer.removeWhiteSpace(paramz[0]);
        String arg2 = StoryDisplayer.removeWhiteSpace(paramz[1]);
        rawStr = rawStr.replace(methodName + "(" + tempBeginning + ")",
                String.valueOf(CombatUtils.genRandomNum((int)MathsParser.eval(arg1), (int)MathsParser.eval(arg2))));
        return rawStr;
    }

    /**
     * When parsing genRandomNum call on string, this method extracts the part with the arguments being provided to
     * method
     */
    private static String getString(String rawStr, String methodName) {
        int indexOfOpenBracket = rawStr.indexOf(methodName) + methodName.length() + 1;
        int indexOfClosedBracket = -1;
        int bracketzToSkip = 0;
        for (int i = indexOfOpenBracket; i < rawStr.length(); i++) {
            if (rawStr.charAt(i) == '(') {
                bracketzToSkip++;
            } else if (rawStr.charAt(i) == ')') {
                if (bracketzToSkip > 0) {
                    bracketzToSkip--;
                    continue;
                }
                indexOfClosedBracket = i;
                break;
            }
        }
        return rawStr.substring(indexOfOpenBracket, indexOfClosedBracket);
    }

    /**
     * Takes in the default foe name as per the .txt file with its config (e.g. for "gnomeConfig.txt", you'd be
     * expected to provide "gnome" as this argument). Optionally you can provide another name to be displayed as in-game.
     * @return parsed foe of its proper class
     */
    public static Foe parseFoe(String foeName, String overrideDispName) throws Exception {
        List<String> parsedLinez = verifyAndOpenFile(foeName);
        Map<String, String> parsedValz = new LinkedHashMap<>();
        for (String curLine: parsedLinez) {
            processLine(curLine, parsedValz);
        }
        if (!overrideDispName.isEmpty()) {
            parsedValz.put("name", overrideDispName);
        }
        return switch (parsedValz.get("specialAttackType").toLowerCase()) {
            case "multiattacker" -> new MultiAttacker(parsedValz);
            case "armourbooster" -> new ArmourBooster(parsedValz);
            case "armourshredder" -> new ArmourShredder(parsedValz);
            case "lifeleecher" -> new LifeLeecher(parsedValz);
            case "multitalented" -> new MultiTalented(parsedValz);
            default -> new BasicFoe(parsedValz);
        };
    }

    /**
     * Expects a foe config file, and attempts to parse it
     * @return String list of lines inside the specified file
     */
    private static List<String> verifyAndOpenFile(String fileName) throws Exception {
        if (!fileName.endsWith("Config.txt")) {
            fileName += "Config.txt";
        }
        if (!fileName.startsWith("enemyConfigs/")) {
            fileName = "enemyConfigs/" + fileName;
        }
        List<String> parsedLinez = FileParser.parseFile(fileName, "#", true);
        if (parsedLinez == null) {
            GlobalConf.issueLog("Parsing of " + fileName + " failed!", GlobalConf.SEVERITY_LEVEL_ERROR,
                    true);
        }
        return parsedLinez;
    }

    /**
     * Takes in the string line and a map of parsedValz that will be used to initialise foe. Replaces
     * keywordz with proper valuez as well for proper displaying in-game
     */
    private static void processLine(String curLine, Map<String, String> parsedValz) throws Exception {
        String[] splitz = curLine.split("::");
        String tempKey, tempVal;
        if (splitz.length != 2) {
            GlobalConf.issueLog("Expected value in custom Foe file to be a key::value pair, " +
                    "got " + splitz.length + " elements instead! Faulty line: " +
                    curLine, GlobalConf.SEVERITY_LEVEL_ERROR, false);
            return;
        }
        tempKey = StoryDisplayer.removeWhiteSpace(splitz[0]);
        tempVal = StoryDisplayer.removeWhiteSpace(splitz[1]);
        tempVal = tempVal.replaceAll("playerLevel", String.valueOf(PlayerClass.getPlayerStat("playerLevel")));
        tempVal = tempVal.replaceAll("playerName", PlayerClass.getPlayerName());
        tempVal = tempVal.replaceAll("getLevel", parsedValz.getOrDefault("level", ""));

        String calcResultz = tempVal;
        try {
            calcResultz = String.valueOf((int)(MathsParser.eval(handleRandomNumz(tempVal))));
        } catch (Exception ignored) {

        }
        tempVal = calcResultz;
        parsedValz.put(tempKey, tempVal);
    }
}
