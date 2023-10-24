import java.util.ArrayList;
import java.util.List;

public class StoryBlock {
    private String promptText;
    private List<String> choices = new ArrayList<>();
    private List<String[]> combatantInfo = new ArrayList<>(); // [0] is foe type, [1] is foe name, [2] is foe level (optional)
    private List<Boolean> isEnding = new ArrayList<>();
    private List<Integer> choiceDestinations = new ArrayList<>();
    private List<Boolean> areHiddenStatChecks = new ArrayList<>();
    private List<Integer> statVal = new ArrayList<>();
    private List<String> relevantStat = new ArrayList<>();

    public StoryBlock(String tempPromptText) {
        promptText = tempPromptText;
        choices = new ArrayList<String>();
        combatantInfo = new ArrayList<String[]>();
        isEnding = new ArrayList<Boolean>();
        choiceDestinations = new ArrayList<Integer>();
        areHiddenStatChecks = new ArrayList<>();
        statVal = new ArrayList<>();
        relevantStat = new ArrayList<>();
    }

    public void initChoices(List<String> tempChoices, List<Integer> tempChoiceDestinations, List<String[]> tempCombatantInfo,
                            List<Boolean> tempIsEnding, List<Boolean> tempAreHiddenChecks,
                            List<Integer> tempStatVal, List<String> tempRelevantStat) {
        choices = new ArrayList<>(tempChoices);
        choiceDestinations = new ArrayList<>(tempChoiceDestinations);
        combatantInfo = new ArrayList<>(tempCombatantInfo);
        isEnding = new ArrayList<>(tempIsEnding);
        areHiddenStatChecks = tempAreHiddenChecks;
        statVal = tempStatVal;
        relevantStat = tempRelevantStat;
    }

    public int getChoiceDestinationAtID(int ID) {
        if (ID < 0 || ID >= choiceDestinations.size()) {
            System.out.println("Incorrect ID provided!");
            return -1;
        }
        return choiceDestinations.get(ID);
    }

    public String getPromptText() {
        return this.promptText;
    }

    public List<String> getChoices() {
        List<String> returnChoices = new ArrayList<String>();
        for (int i=0; i<this.choices.size(); i++) {
            if (!this.areHiddenStatChecks.get(i)) {
                // If not a hidden stat check, just append to list
                returnChoices.add(this.choices.get(i));
                continue;
            }
            if (PlayerClass.statComparer(this.statVal.get(i), this.relevantStat.get(i))) {
                // If not a HIDDEN stat check, only display if check passes
                String prettifiedStat = this.relevantStat.get(i);
                String comparisonSign = this.statVal.get(i) > 0 ? ">" : "<";
                if (prettifiedStat.equalsIgnoreCase("curHealth")) {
                    prettifiedStat = "Current Health";
                } else if (prettifiedStat.equalsIgnoreCase("curMana")) {
                    prettifiedStat = "Current Mana";
                }
                returnChoices.add("[" + prettifiedStat + ": " + comparisonSign +
                        Math.abs(this.statVal.get(i)) + this.choices.get(i));
            }
        }
        return this.choices;
    }

}