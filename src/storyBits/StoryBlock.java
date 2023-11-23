package storyBits;

import inventory.Inventory;
import inventory.Item;
import player.*;

import java.util.ArrayList;
import java.util.List;
public class StoryBlock {
    private String promptText;
    private List<String> choices = new ArrayList<>();
    private List<String[]> combatantInfo = new ArrayList<>(); // [0] is foe type, [1] is foe name, [2] is foe level (optional)
    private List<Boolean> isEnding = new ArrayList<>();
    private List<Integer> choiceDestinations = new ArrayList<>();
    private List<Boolean> areHiddenStatChecks = new ArrayList<>();
    private List<Boolean> areStatChecks = new ArrayList<>();
    private List<Integer> statVal = new ArrayList<>();
    private List<String> relevantStat = new ArrayList<>();
    private List<Boolean> ephemeralChoices = new ArrayList<>();
    private String tuneToPlay;

    public void setTuneToPlay(String tune) { this.tuneToPlay = tune; }
    public String getTuneToPlay() { return tuneToPlay; }

    public List<String> getRelevantStat() { return relevantStat; }
    public boolean isStatCheckAtChoiceID(int choiceID) {
        return areStatChecks.get(choiceID);
    }
    public boolean isStatCheckAtChoiceStr(String choiceStr) {
        String tempie = removeBracketAndWhiteSpace(choiceStr);
        for (int i = 0; i < getRawChoices().size(); i++) {
            if (getRawChoices().get(i).equals(tempie)) {
                return isStatCheckAtChoiceID(i);
            }
        }
        return false;
    }

    public List<String[]> getCombatantInfo() {
        return combatantInfo;
    }

    public String[] getCombatantAtChoice(String choiceStr) {
        String tempie = removeBracketAndWhiteSpace(choiceStr);
        for (int i = 0; i < getRawChoices().size(); i++) {
            if (getRawChoices().get(i).equals(tempie)) {
                return getCombatantInfo().get(i);
            }
        }
        return null;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public void setCombatantInfo(List<String[]> combatantInfo) {
        this.combatantInfo = combatantInfo;
    }

    public List<Boolean> getIsEnding() {
        return isEnding;
    }

    public void setIsEnding(List<Boolean> isEnding) {
        this.isEnding = isEnding;
    }

    public List<Integer> getChoiceDestinations() {
        return choiceDestinations;
    }

    public void setChoiceDestinations(List<Integer> choiceDestinations) {
        this.choiceDestinations = choiceDestinations;
    }

    public List<Boolean> getAreHiddenStatChecks() {
        return areHiddenStatChecks;
    }

    public void setAreHiddenStatChecks(List<Boolean> areHiddenStatChecks) {
        this.areHiddenStatChecks = areHiddenStatChecks;
    }

    public List<Boolean> getAreStatChecks() {
        return areStatChecks;
    }

    public void setAreStatChecks(List<Boolean> areStatChecks) {
        this.areStatChecks = areStatChecks;
    }

    public List<Integer> getStatVal() {
        return statVal;
    }

    public List<Boolean> getEphemeralChoices() {
        return ephemeralChoices;
    }

    public void setStatVal(List<Integer> statVal) {
        this.statVal = statVal;
    }

    public void setRelevantStat(List<String> relevantStat) {
        this.relevantStat = relevantStat;
    }

    public StoryBlock(String tempPromptText, String tempTuneToPlay) {
        promptText = tempPromptText;
        choices = new ArrayList<>();
        combatantInfo = new ArrayList<>();
        isEnding = new ArrayList<>();
        choiceDestinations = new ArrayList<>();
        areHiddenStatChecks = new ArrayList<>();
        areStatChecks = new ArrayList<>();
        statVal = new ArrayList<>();
        relevantStat = new ArrayList<>();
        tuneToPlay = tempTuneToPlay;
        ephemeralChoices = new ArrayList<>();
    }
    public StoryBlock(StoryBlock obj) {
        this.promptText = obj.getPromptText();
        this.choices = obj.getRawChoices();
        this.combatantInfo = obj.getCombatantInfo();
        this.isEnding = obj.getIsEnding();
        this.choiceDestinations = obj.getChoiceDestinations();
        this.areHiddenStatChecks = obj.getAreHiddenStatChecks();
        this.areStatChecks = obj.getAreStatChecks();
        this.statVal = obj.getStatVal();
        this.relevantStat = obj.getRelevantStat();
        this.tuneToPlay = obj.getTuneToPlay();
        this.ephemeralChoices = obj.getEphemeralChoices();
    }

    public void initChoices(List<String> tempChoices, List<Integer> tempChoiceDestinations, List<String[]> tempCombatantInfo,
                            List<Boolean> tempIsEnding, List<Boolean> tempAreStatChecks, List<Boolean> tempAreHiddenChecks,
                            List<Integer> tempStatVal, List<String> tempRelevantStat, List<Boolean> tempEphemeralStat) {
        this.choices = new ArrayList<>(tempChoices);
        this.choiceDestinations = new ArrayList<>(tempChoiceDestinations);
        this.combatantInfo = new ArrayList<>(tempCombatantInfo);
        this.isEnding = new ArrayList<>(tempIsEnding);
        this.areHiddenStatChecks = new ArrayList<>(tempAreHiddenChecks);
        this.areStatChecks = new ArrayList<>(tempAreStatChecks);
        this.statVal = new ArrayList<>(tempStatVal);
        this.relevantStat = new ArrayList<>(tempRelevantStat);
        this.ephemeralChoices = new ArrayList<>(tempEphemeralStat);
    }

    public int getChoiceDestinationAtID(int ID) {
        if (ID < 0 || ID >= getChoiceDestinations().size()) {
            System.out.println("Incorrect ID provided!");
            return -1;
        }
        return getChoiceDestinations().get(ID);
    }

    public int getChoiceDestinationAtChoiceStr(String choiceStr) {
        String tempie = removeBracketAndWhiteSpace(choiceStr);
        for (int i = 0; i < getRawChoices().size(); i++) {
            if (getRawChoices().get(i).equals(tempie)) {
                return getChoiceDestinationAtID(i);
            }
        }
        return -1;
    }

    public void popChoice(String choiceStr) throws Exception {
        int popIndex = -1;
        for (int i = 0; i < this.getRawChoices().size(); i++) {
            if (this.getRawChoices().get(i).equalsIgnoreCase(choiceStr)) {
                popIndex = i;
                break;
            }
        }
        if (popIndex == -1) {
            GlobalConf.issueLog("Couldn't process the ephemeral choice " + choiceStr + "!",
                    GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        getRawChoices().remove(popIndex);
        getChoiceDestinations().remove(popIndex);
        getCombatantInfo().remove(popIndex);
        getIsEnding().remove(popIndex);
        getAreHiddenStatChecks().remove(popIndex);
        getAreStatChecks().remove(popIndex);
        getStatVal().remove(popIndex);
        getRelevantStat().remove(popIndex);
        getEphemeralChoices().remove(popIndex);
    }
    public String getPromptText() {
        return promptText;
    }

    public List<String> getRawChoices() { return choices; }

    public void appendBlockCopy(List<String> tempChoices,
                                List<Integer> tempChoiceDestinations,
                                List<String[]> tempCombatantInfo,
                                List<Boolean> tempIsEnding,
                                List<Boolean> tempAreStatChecks,
                                List<Boolean> tempAreHiddenStatChecks,
                                List<Integer> tempStatVal,
                                List<Boolean> tempEphemeralVal,
                                List<String> tempRelevantStat,
                                int i) {
        tempChoices.add(this.getRawChoices().get(i));
        tempChoiceDestinations.add(this.getChoiceDestinations().get(i));
        tempCombatantInfo.add(this.getCombatantInfo().get(i));
        tempIsEnding.add(this.getIsEnding().get(i));
        tempAreStatChecks.add(this.getAreStatChecks().get(i));
        tempAreHiddenStatChecks.add(this.getAreHiddenStatChecks().get(i));
        tempStatVal.add(this.getStatVal().get(i));
        tempRelevantStat.add(this.getRelevantStat().get(i));
        tempEphemeralVal.add(this.getEphemeralChoices().get(i));
    }
    public StoryBlock refineCurStoryBlock() {
        StoryBlock tempie = new StoryBlock(this.getPromptText(), this.getTuneToPlay());
        List<String> tempChoices = new ArrayList<>();
        List<Integer> tempChoiceDestinations = new ArrayList<>();
        List<String[]> tempCombatantInfo = new ArrayList<>();
        List<Boolean> tempIsEnding = new ArrayList<>();
        List<Boolean> tempAreStatChecks = new ArrayList<>();
        List<Boolean> tempAreHiddenStatChecks = new ArrayList<>();
        List<Integer> tempStatVal = new ArrayList<>();
        List<String> tempRelevantStat = new ArrayList<>();
        List<Boolean> tempEphemeralStat = new ArrayList<>();
        boolean addAppendage = false;
        for (int i=0; i<this.getRawChoices().size() + 1; i++) {
            if (addAppendage) {
                appendBlockCopy(tempChoices, tempChoiceDestinations, tempCombatantInfo,
                        tempIsEnding, tempAreStatChecks, tempAreHiddenStatChecks, tempStatVal,
                        tempEphemeralStat, tempRelevantStat, i-1);
            }
            if (i == this.getRawChoices().size()) {
                break;
            }
            addAppendage = false;
            if (!(this.getAreHiddenStatChecks().get(i) || this.isStatCheckAtChoiceID(i) ||
                    this.getRelevantStat().get(i).equalsIgnoreCase("item-"))) {
                // If not any kind of stat check, just append to list
                addAppendage = true;
                continue;
            }
            if (this.getRelevantStat().get(i).matches("^-?[0-9]\\d*$") && this.getAreHiddenStatChecks().get(i)) {
                // if item-check
                if (this.getStatVal().get(i) == -1) {
                    // if check requires NOT having an item
                    if (Inventory.getInventoryItemByItemID(Integer.parseInt(getRelevantStat().get(i))) == null) {
                        addAppendage = true;
                    }
                } else {
                    Item itemInQuestion = Inventory.getInventoryItemByItemID(Integer.parseInt(getRelevantStat().get(i)));
                    if (itemInQuestion == null) {
                        continue;
                    }
                    addAppendage = true;
                }
            } else if (this.getRelevantStat().get(i).equalsIgnoreCase("item-")) {
                // if the choice is an item decrement, check if the player has that item
                if (Inventory.getInventoryItemByItemID(this.getStatVal().get(i)) == null) {
                    continue;
                }
                addAppendage = true;
            } else if (this.isStatCheckAtChoiceID(i) && !this.getAreHiddenStatChecks().get(i)) {
                // if unknown (to player) stat check
                addAppendage = true;
            } else if (PlayerClass.statComparer(this.getStatVal().get(i), this.getRelevantStat().get(i))) {
                // If a HIDDEN stat check, only display if check passes
                addAppendage = true;
            }
        }
        tempie.initChoices(tempChoices, tempChoiceDestinations, tempCombatantInfo, tempIsEnding,
                tempAreStatChecks, tempAreHiddenStatChecks, tempStatVal, tempRelevantStat, tempEphemeralStat);
        return tempie;
    }
    public List<String> getChoices() throws Exception {
        // skipping checks here and focusing on prettifying stats, as the choices are assumed to be passable already
        // (as in, put through refinement prior to call
        List<String> returnChoices = new ArrayList<>();
        for (int i=0; i<this.getRawChoices().size(); i++) {
            if (!(this.getAreHiddenStatChecks().get(i) || this.isStatCheckAtChoiceID(i))) {
                // If not any kind of stat check, just append to list
                returnChoices.add(this.getRawChoices().get(i));
                continue;
            }
            String prettifiedStat = this.getRelevantStat().get(i);
            String comparisonSign = this.getStatVal().get(i) > 0 ? ">" : "<";
            if (prettifiedStat.equalsIgnoreCase("curHealth")) {
                prettifiedStat = "Current Health";
            } else if (prettifiedStat.equalsIgnoreCase("curMana")) {
                prettifiedStat = "Current Mana";
            }
            if (this.getRelevantStat().get(i).matches("^-?[0-9]\\d*$") && this.getAreHiddenStatChecks().get(i)) {
                // if item-check
                if (this.getStatVal().get(i) == -1) {
                    // if check requires NOT having an item
                    if (Inventory.getInventoryItemByItemID(Integer.parseInt(getRelevantStat().get(i))) == null) {
                        returnChoices.add(this.getRawChoices().get(i));
                    }
                } else {
                    Item itemInQuestion = Inventory.getInventoryItemByItemID(Integer.parseInt(getRelevantStat().get(i)));
                    if (itemInQuestion == null) {
                        GlobalConf.issueLog("Failed to retrieve item info!", GlobalConf.SEVERITY_LEVEL_ERROR,
                                true);
                    }
                    if (this.getCombatantInfo().get(i) != null) {
                        if (this.getCombatantInfo().get(i)[0].equalsIgnoreCase("hiddenimus")) {
                            returnChoices.add(this.getRawChoices().get(i));
                        }
                    } else {
                        returnChoices.add("[requires " + itemInQuestion.getName() + "] " + this.getRawChoices().get(i));
                    }
                }
            } else if (this.isStatCheckAtChoiceID(i) && !this.getAreHiddenStatChecks().get(i)) {
                // if unknown (to player) stat check
                returnChoices.add("[" + prettifiedStat + "] " + this.getRawChoices().get(i));
            } else if (PlayerClass.statComparer(this.getStatVal().get(i), this.getRelevantStat().get(i))) {
                // If a HIDDEN stat check, only display if check passes
                returnChoices.add("[" + prettifiedStat + " " + comparisonSign + " " +
                        Math.abs(this.getStatVal().get(i)) + "] " + this.getRawChoices().get(i));
            }
        }
        return returnChoices;
    }

    public String removeBracketAndWhiteSpace(String strToPurify) {
        String[] tempie = strToPurify.split("]");
        return StoryDisplayer.removeWhiteSpace(tempie[tempie.length-1]);
    }

}