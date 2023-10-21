import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class StoryBlock {
    private String promptText;
    private List<String> choices = new ArrayList<>();
    private List<String[]> combatantInfo = new ArrayList<>(); // [0] is foe type, [1] is foe name, [2] is foe level (optional)
    private List<Boolean> isEnding = new ArrayList<>();
    private List<StoryBlock> choiceDestinations = new ArrayList<>();
    public StoryBlock(String tempPromptText)
    {
        promptText = tempPromptText;
        choices = new ArrayList<String>();
        combatantInfo = new ArrayList<String[]>();
        isEnding = new ArrayList<Boolean>();
        choiceDestinations = new ArrayList<StoryBlock>();
    }
    public void initChoices(List<String> tempChoices, List<StoryBlock> tempChoiceDestinations, List<String[]> tempCombatantInfo,
                            List<Boolean> tempIsEnding)
    {
        choices = new ArrayList<>(tempChoices);
        choiceDestinations = new ArrayList<>(tempChoiceDestinations);
        combatantInfo = new ArrayList<>(tempCombatantInfo);
        isEnding = new ArrayList<>(tempIsEnding);
    }

    public String getPromptText() { return this.promptText; }
    public List<String> getChoices() { return this.choices; }
}