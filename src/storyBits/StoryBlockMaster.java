package storyBits;

import java.util.ArrayList;
import java.util.List;

public class StoryBlockMaster {
    private ArrayList<StoryBlock> storyObj;

    public ArrayList<StoryBlock> getStoryObj() {
        return storyObj;
    }
    public StoryBlockMaster(String storyFile) {
        storyObj = new ArrayList<StoryBlock>();
        List<String> storyData = FileParser.parseFile(storyFile);
        if (storyData == null) {
            System.out.println("File could not be parsed!");
            return;
        }
        List<ArrayList<String>> tempStoryBits = genTempStoryBits(storyData);

        int tempStoryID = 0;
        List<String> tempChoices = new ArrayList<String>();
        List<Integer> tempDestinationBlocks = new ArrayList<Integer>();
        List<String[]> tempCombatant = new ArrayList<String[]>();
        List<Boolean> tempIsEnding = new ArrayList<Boolean>();
        List<Boolean> tempIsStatCheck = new ArrayList<Boolean>();
        List<Boolean> tempIsHiddenCheck = new ArrayList<Boolean>();
        List<Integer> tempStatValue = new ArrayList<Integer>();
        List<String> tempRelevantStat = new ArrayList<String>();
        for (ArrayList<String> tempStoryBit : tempStoryBits) {
            // iterates over story-bits that are not just prompts, i.e. have choices to pick from
            int thisTempStorySize = tempStoryBit.size();
            int thisTempStoryID = Integer.parseInt(tempStoryBit.get(0));
            if (thisTempStoryID != tempStoryID) {
                // if loop hits a new story block, update the old storyObj before moving on to this next, new one
                storyObj.get(tempStoryID).initChoices(tempChoices, tempDestinationBlocks,
                        tempCombatant, tempIsEnding, tempIsStatCheck, tempIsHiddenCheck,
                        tempStatValue, tempRelevantStat);
                tempStoryID = thisTempStoryID;
                tempChoices.clear();
                tempDestinationBlocks.clear();
                tempCombatant.clear();
                tempIsEnding.clear();
                tempIsStatCheck.clear();
                tempIsHiddenCheck.clear();
                tempStatValue.clear();
                tempRelevantStat.clear();
            }
            tempDestinationBlocks.add(getNextStoryStep(tempStoryBit.get(1))); // 1st index contains story destination
            tempChoices.add(tempStoryBit.get(thisTempStorySize - 1)); // Last element is always text to guide player
            tempIsEnding.add(tempStoryBit.get(1).equals("END")); // whether it's an end option

            if (!stringContainsAny(tempStoryBit.get(2), new char[]{'+', '-', '<', '>'}) ||
                tempStoryBit.size() <= 3) {
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
                continue;
            }
            String[] statsInfo = tempStoryBit.get(2).split("[<>+-]");
            int statValModifier = -1;
            boolean isThisStatCheck = stringContainsAny(tempStoryBit.get(2), new char[]{'>', '<'});
            tempIsStatCheck.add(isThisStatCheck);
            if (isThisStatCheck) {
                // is a stat check
                if (tempStoryBit.size() == 5) {
                    // if specifies hidden/plain stat check
                    tempIsHiddenCheck.add(tempStoryBit.get(3).equalsIgnoreCase("hidden"));
                } else {
                    tempIsHiddenCheck.add(false);
                }
                statValModifier = tempStoryBit.get(2).charAt(statsInfo[0].length()) == '>' ? 1 : -1;
                tempStatValue.add(Integer.parseInt(statsInfo[1]) * statValModifier);
            } else {
                // is a stat boost
                statValModifier = tempStoryBit.get(2).charAt(statsInfo[0].length()) == '+' ? 1 : -1;
                if (statsInfo[1].equals("max")) {
                    tempStatValue.add(statValModifier == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE);
                } else {
                    tempStatValue.add(Integer.parseInt(statsInfo[1]) * statValModifier);
                }
                tempIsHiddenCheck.add(false);
            }
            tempRelevantStat.add(statsInfo[0]);
            tempCombatant.add(null);
        }
        storyObj.get(tempStoryID).initChoices(tempChoices, tempDestinationBlocks,
                tempCombatant, tempIsEnding, tempIsStatCheck, tempIsHiddenCheck,
                tempStatValue, tempRelevantStat);
        // init results of last iteration
    }

    public void showMeSomeStories() {
        for (StoryBlock x : storyObj) {
            System.out.println(x.getPromptText());
            for (String c : x.getChoices())
                System.out.println(c);
        }
    }

    public String[] produceCombatantInfo(ArrayList<String> tempStoryBit) {
        int tempStorySize = tempStoryBit.size();
        if (tempStorySize < SplitReturnsEnum.COMBAT_PROMPT.val) {
            return null;
        }
        int combatantRange = tempStorySize == SplitReturnsEnum.COMBAT_PROMPT.val ? 3 : 4;
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
        splitResultz.sort((o1, o2) -> Integer.parseInt(o1.get(0)) - Integer.parseInt(o2.get(0)));
        // By running the loop above and subsequent sorting,
        // storybits can be placed in the textAdv.txt file out of order
        // and still be parsed and initialised properly
        for (ArrayList<String> storyLine : splitResultz) {
            if (storyLine.size() == SplitReturnsEnum.STORY_PROMPT.val) {
                storyObj.add(new StoryBlock(storyLine.get(storyLine.size() - 1)));
                continue;
                // In this case, that's all the use of this particular splitResult as it only contains relevant
                // story info. No id needed as they're marked from 0 to n, so just use .get() on storyObj
            }
            tempStoryBits.add(storyLine);
        }
        return tempStoryBits;
    }

    public ArrayList<String> stringSplitter(String line) {
        ArrayList<String> splits = new ArrayList<String>(List.of(line.split("]")));
        splits.replaceAll(String::trim);
        // breaks up the initial line by ]
        // also removes trailing whitespace

        ArrayList<String> rawNodeData = new ArrayList<String>(List.of(splits.get(0).split("\\.")));
        // Takes the first item, which is always info on story segment, and breaks it up by .

        for (int i = 1; i < splits.size(); i++) {
            rawNodeData.add(splits.get(i));
        }
        // Appends optional combat info and story text to rawNodeData

        rawNodeData.replaceAll(e -> e.replaceAll("\\[", ""));
        // Cleans up the [
        return rawNodeData;
    }

    public boolean stringContainsAny(String sourceStr, char[] charsToMatch) {
        for (char i: sourceStr.toCharArray()) {
            for (char j: charsToMatch) {
                if (Character.compare(i, j) == 0) { return true; }
            }
        }
        return false;
    }
    public int getNextStoryStep(String storyStepID) {
        if (storyStepID.equals("END"))
            return -1;
        return Integer.parseInt((storyStepID));
    }
}
