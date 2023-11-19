package combat;

import foeTypes.foeArchetypes.ArmourBooster;
import foeTypes.foeArchetypes.ArmourShredder;
import foeTypes.foeArchetypes.BasicFoe;
import foeTypes.foeArchetypes.MultiAttacker;
import player.PlayerClass;
import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryDisplayer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FoeParser {

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

    public static Foe parseFoe(String foeName, String overrideDispName) throws Exception {
        if (!foeName.endsWith("Config.txt")) {
            foeName += "Config.txt";
        }
        if (!foeName.startsWith("enemyConfigs/")) {
            foeName = "enemyConfigs/" + foeName;
        }
        List<String> shit = FileParser.parseFile(foeName, "#", true);
        Map<String, String> parsedValz = new LinkedHashMap<>();
        String tempKey, tempVal;
        for (String curLine: shit) {
            String[] splitz = curLine.split("::");
            if (splitz.length != 2) {
                GlobalConf.issueLog("Expected value in custom Foe file to be a key::value pair, " +
                        "got " + splitz.length + " elements instead! Faulty line: " +
                        curLine, GlobalConf.SEVERITY_LEVEL_ERROR, false);
                continue;
            }
            tempKey = StoryDisplayer.removeWhiteSpace(splitz[0]);
            tempVal = StoryDisplayer.removeWhiteSpace(splitz[1]);
            tempVal = tempVal.replaceAll("matchPlayerLevel", String.valueOf(PlayerClass.getPlayerStat("playerLevel")));
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
        if (!overrideDispName.isEmpty()) {
            parsedValz.put("name", overrideDispName);
        }
        return switch (parsedValz.get("specialAttackType").toLowerCase()) {
            case "multiattacker" -> new MultiAttacker(parsedValz);
            case "armourbooster" -> new ArmourBooster(parsedValz);
            case "armourshredder" -> new ArmourShredder(parsedValz);
            default -> new BasicFoe(parsedValz);
        };
    }
}
