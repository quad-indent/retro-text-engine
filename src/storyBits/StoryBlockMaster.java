package storyBits;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoryBlockMaster {
    private static List<Integer[]> poppedEphemeralz = new ArrayList<>();
    private ArrayList<StoryBlock> storyObj;
    public ArrayList<StoryBlock> getStoryObj() {
        return storyObj;
    }

    public static List<Integer[]> getPoppedEphemeralz() {
        return poppedEphemeralz;
    }

    public static void setPoppedEphemeralz(List<Integer[]> poppedEphemeralz) {
        StoryBlockMaster.poppedEphemeralz = poppedEphemeralz;
    }

    public StoryBlockMaster(String storyFile) throws Exception {
        storyObj = new ArrayList<>();
        List<String> storyData = FileParser.parseFile(storyFile, "[", false);
        if (storyData == null) {
            System.out.println("File could not be parsed!");
            return;
        }
        GlobalConf.verifyStoryIntegrity(storyData);
        List<ArrayList<String>> tempStoryBits = genTempStoryBits(storyData);
        int tempStoryID = 0;
        List<String> tempChoices = new ArrayList<>();
        List<Integer> tempDestinationBlocks = new ArrayList<>();
        List<String[]> tempCombatant = new ArrayList<>();
        List<Boolean> tempIsEnding = new ArrayList<>();
        List<Boolean> tempIsStatCheck = new ArrayList<>();
        List<Boolean> tempIsHiddenCheck = new ArrayList<>();
        List<Integer> tempStatValue = new ArrayList<>();
        List<String> tempRelevantStat = new ArrayList<>();
        List<Boolean> tempEphemeralChoices = new ArrayList<>();
        for (ArrayList<String> tempStoryBit : tempStoryBits) {
            // iterates over story-bits that are not just prompts, i.e. have choices to pick from
            tempStoryID = processPrompt(tempChoices, tempDestinationBlocks, tempCombatant, tempIsEnding,
                    tempIsStatCheck, tempIsHiddenCheck, tempStatValue, tempRelevantStat,
                    tempEphemeralChoices, tempStoryBit, tempStoryID);
        }
        if (tempStoryID >= storyObj.size()) {
            GlobalConf.issueLog("Mismatch of story text IDs and actual story text length! " +
                            "Total story text size is " + storyObj.size() + " blocks, but attempting to parse ID #" +
                            tempStoryID + " (last ID should be #" + (storyObj.size() - 1) +
                            ")! Please double-check all your IDs!",
                    GlobalConf.SEVERITY_LEVEL_ERROR,
                    true);
        }
        storyObj.get(tempStoryID).initChoices(tempChoices, tempDestinationBlocks,
                tempCombatant, tempIsEnding, tempIsStatCheck, tempIsHiddenCheck,
                tempStatValue, tempRelevantStat, tempEphemeralChoices);
        if (storyObj.get(storyObj.size() - 1).getRawChoices().isEmpty()) {
            GlobalConf.issueLog("Final story text has no choices! This might indicate that you have " +
                    "some overlapping (non-unique) story IDs!", GlobalConf.SEVERITY_LEVEL_WARNING, false);
        }
        // init results of last iteration
    }

    private int processPrompt(List<String> tempChoices,
                            List<Integer> tempDestinationBlocks,
                            List<String[]> tempCombatant,
                            List<Boolean> tempIsEnding,
                            List<Boolean> tempIsStatCheck,
                            List<Boolean> tempIsHiddenCheck,
                            List<Integer> tempStatValue,
                            List<String> tempRelevantStat,
                            List<Boolean> tempEphemeralStat,
                            ArrayList<String> tempStoryBit,
                               int tempStoryID) throws Exception {
        int thisTempStorySize = tempStoryBit.size();
        int thisTempStoryID = Integer.parseInt(tempStoryBit.get(0));
        if (thisTempStoryID != tempStoryID) {
            if (tempStoryID >= storyObj.size()) {
                GlobalConf.issueLog("Mismatch of story text IDs and actual story text length! " +
                                "Total story text size is " + storyObj.size() + " blocks, but attempting to parse ID #" +
                                tempStoryID + " (last ID should be #" + (storyObj.size() - 1) +
                                ")! Please double-check all your IDs!",
                        GlobalConf.SEVERITY_LEVEL_ERROR,
                        true);
            }
            // if loop hits a new story block, update the old storyObj before moving on to this next, new one
            storyObj.get(tempStoryID).initChoices(tempChoices, tempDestinationBlocks,
                    tempCombatant, tempIsEnding, tempIsStatCheck, tempIsHiddenCheck,
                    tempStatValue, tempRelevantStat, tempEphemeralStat);
            tempStoryID = thisTempStoryID;
            tempChoices.clear();
            tempDestinationBlocks.clear();
            tempCombatant.clear();
            tempIsEnding.clear();
            tempIsStatCheck.clear();
            tempIsHiddenCheck.clear();
            tempStatValue.clear();
            tempRelevantStat.clear();
            tempEphemeralStat.clear();
        }
        tempDestinationBlocks.add(getNextStoryStep(tempStoryBit.get(1))); // 1st index contains story destination
        tempChoices.add(tempStoryBit.get(thisTempStorySize - 1)); // Last element is always text to guide player
        tempIsEnding.add(tempStoryBit.get(1).equals("END")); // whether it's an end option

        tempEphemeralStat.add((tempStoryBit.get(2).equalsIgnoreCase("ephemeral") ||
                (tempStoryBit.size() > 3 && tempStoryBit.get(3).equalsIgnoreCase("ephemeral"))));
        if (!stringContainsAny(tempStoryBit.get(2), new char[]{'+', '-', '<', '>', ':'}) ||
                tempStoryBit.size() == 3) {
            // combat info and stat boosts or checks are always >3 in size
            if (tempStoryBit.size() > 3) {
                tempCombatant.add(produceCombatantInfo(tempStoryBit)); // empty str arr if none
            } else {
                tempCombatant.add(null);
            }
            tempIsStatCheck.add(false);
            tempIsHiddenCheck.add(false);
            tempRelevantStat.add("");
            tempStatValue.add(0);
            return tempStoryID;
        }
        String[] statsInfo = tempStoryBit.get(2).split("[<>+-]");
        int statValModifier = -1;
        boolean isThisStatCheck = stringContainsAny(tempStoryBit.get(2), new char[]{'>', '<'});
        boolean isThisItemCheck = tempStoryBit.get(2).toLowerCase().contains("has:") ||
                tempStoryBit.get(2).toLowerCase().contains("hasnot:");
        boolean hasCheck = tempStoryBit.get(2).toLowerCase().contains("has:");
        boolean hiddenItemCheck = false;
        tempIsStatCheck.add(isThisStatCheck);
        if (isThisItemCheck) {
            hiddenItemCheck = tempStoryBit.get(3).equalsIgnoreCase("hidden");
            tempIsHiddenCheck.add(true);
            tempStatValue.add((hasCheck ? 1 : -1));
            // tempStatValue.add(0);
            //adds item ID
            String itemID = StoryDisplayer.removeWhiteSpace(tempStoryBit.get(2).split((hasCheck ? "has:" : "hasnot:"))[1]);
            tempRelevantStat.add(itemID);
        } else if (isThisStatCheck) {
            // is a stat check
            if (tempStoryBit.size() == 5) {
                // if specifies hidden/plain stat check
                tempIsHiddenCheck.add(tempStoryBit.get(3).equalsIgnoreCase("hidden"));
            } else {
                tempIsHiddenCheck.add(false);
            }
            statValModifier = tempStoryBit.get(2).charAt(statsInfo[0].length()) == '>' ? 1 : -1;
            tempStatValue.add(Integer.parseInt(statsInfo[1]) * statValModifier);
            tempRelevantStat.add(statsInfo[0]);
        } else {
            // is a stat boost
            statValModifier = tempStoryBit.get(2).charAt(statsInfo[0].length()) == '+' ? 1 : -1;
            if (statsInfo[1].equals("max")) {
                tempStatValue.add(statValModifier == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            } else if (statsInfo[0].equalsIgnoreCase("item")) {
                //adding (or unadding) item
                tempStatValue.add(Integer.parseInt(statsInfo[1]));
            } else {
                tempStatValue.add(Integer.parseInt(statsInfo[1]) * statValModifier);
            }
            tempIsHiddenCheck.add(false);
            if (statsInfo[0].equalsIgnoreCase("item")) {
                tempRelevantStat.add(statsInfo[0] + tempStoryBit.get(2).charAt(statsInfo[0].length()));
            } else {
                tempRelevantStat.add(statsInfo[0]);
            }
        }
        if (!hiddenItemCheck) {
            tempCombatant.add(null);
        } else {
            tempCombatant.add(new String[]{"hiddenimus"});
        }
        return tempStoryID;
    }

    public void showMeSomeStories() throws Exception {
        for (StoryBlock x : storyObj) {
            System.out.println(x.getPromptText());
            for (String c : x.getChoices())
                System.out.println(c);
        }
    }

    public String[] produceCombatantInfo(ArrayList<String> tempStoryBit) {
        int tempStorySize = tempStoryBit.size();
        if (tempStorySize < ReturnsAndDataEnums.COMBAT_PROMPT.val) {
            return null;
        }
        int combatantRange = tempStorySize == ReturnsAndDataEnums.COMBAT_PROMPT.val ? 3 : 4;
        // last ID of combatant info will be 3 if level is not specified; otherwise 4
        String[] combatantInfo = new String[combatantRange - 1]; // 2 or 3 values
        for (int i = 2; i <= combatantRange; i++) // combatant info starts at ID 2
        {
            combatantInfo[i - 2] = tempStoryBit.get(i);
        }
        return combatantInfo;
    }

    public List<ArrayList<String>> genTempStoryBits(List<String> storyData) {
        List<ArrayList<String>> tempStoryBits = new ArrayList<>();
        List<ArrayList<String>> splitResultz = new ArrayList<>();
        for (String storyLine : storyData) {
            ArrayList<String> splitResult = stringSplitter(storyLine);
            splitResultz.add(splitResult);
        }
        splitResultz.sort(Comparator.comparingInt(o -> Integer.parseInt(o.get(0))));
        // By running the loop above and subsequent sorting,
        // storybits can be placed in the textAdv.txt file out of order
        // and still be parsed and initialised properly
        for (ArrayList<String> storyLine : splitResultz) {
            if (storyLine.size() == ReturnsAndDataEnums.STORY_PROMPT.val || storyLine.get(1).toLowerCase().contains("tune")) {
                // if is pure story thing
                String tuneToPlay = "";
                if (storyLine.get(1).toLowerCase().contains("tune:")) {
                    tuneToPlay = storyLine.get(1).toLowerCase().split("tune:")[1];
                }
                storyObj.add(new StoryBlock(storyLine.get(storyLine.size() - 1), tuneToPlay));
                continue;
                // In this case, that's all the use of this particular splitResult as it only contains relevant
                // story info. No id needed as they're marked from 0 to n, so just use .get() on storyObj
            }
            tempStoryBits.add(storyLine);
        }
        return tempStoryBits;
    }

    public ArrayList<String> stringSplitter(String line) {
        ArrayList<String> splits = new ArrayList<>(List.of(line.split("]")));
        splits.replaceAll(String::trim);
        // breaks up the initial line by ]
        // also removes trailing whitespace

        ArrayList<String> rawNodeData = new ArrayList<>(List.of(splits.get(0).split("\\.")));
        // Takes the first item, which is always info on story segment, and breaks it up by .

        for (int i = 1; i < splits.size(); i++) {
            rawNodeData.add(splits.get(i));
        }
        // Appends optional combat info and story text to rawNodeData

        rawNodeData.replaceAll(e -> e.replaceAll("\\[", ""));
        // Cleans up the [
        return rawNodeData;
    }

    public static boolean stringContainsAny(String sourceStr, char[] charsToMatch) {
        for (char i: sourceStr.toCharArray()) {
            for (char j: charsToMatch) {
                if (i == j) { return true; }
            }
        }
        return false;
    }
    public int getNextStoryStep(String storyStepID) {
        if (storyStepID.equals("END"))
            return -1;
        return Integer.parseInt((storyStepID));
    }

    public static ArrayList<StoryBlock> massPopEphemeralz(List<Integer[]> pops, ArrayList<StoryBlock> storyObj) {
        if (pops == null) {
            return storyObj;
        }
        for (Integer[] curPop: pops) {
            storyObj.get(curPop[0]).popChoice(curPop[1]);
        }
        return storyObj;
    }
}
