import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerClass {
    private String playerName = "";
    private int playerLevel = 1;
    private int curXP = 0;
    private int neededXP = LevelEnums.XPArray[2];
    private int maxHealth = 50;
    private int curHealth = 50;
    private int maxMana = 20;
    private int curMana = 20;
    private int Armour = 0;
    Map<String, Integer> playerAtts = new LinkedHashMap<>();
    StoryDisplayer dispObj;
    PlayerClass(String characterSheetPath, StoryDisplayer dispObjRef)
    {
        dispObj = dispObjRef;
        // Json would be better, but dependencies. This will be some funky .txt parsing instead
        if (characterSheetPath == null)
            characterSheetPath = "playerSheet.storyData"; // pitiful obfuscation of format, marginally better than nothing
        File playerFile = new File(characterSheetPath);
        if (!playerFile.isFile())
        {
            characterCreator();
        }
    }
    void defaultPlayerInit()
    {
        playerAtts.put("Strength", 10);
        playerAtts.put("Dexterity", 10);
        playerAtts.put("Intellect", 10);
        playerAtts.put("Diplomacy", 0);
        playerAtts.put("Subterfuge", 0);
        playerAtts.put("Arcana", 0);
        playerAtts.put("Willpower", 0);
        playerAtts.put("Keen Eye", 0);
    }
    void characterCreator()
    {
        defaultPlayerInit();
        System.out.println(">> Hello, and welcome to this story. I am your Narrator.\n>> I will always guide you through " +
                "this beginning, and who knows, maybe we will meet again down the line!");
        System.out.println(">> Tell me, what is your name?");
        playerName = dispObj.awaitChoiceInput(false, true);
        System.out.println(">> Tell me about yourself, " + playerName + ". Would you prefer to have a little chat about yourself, " +
                "or simply provide me with the precise information about your strengths and weaknesses?");
        System.out.println(">> [1] - Let's do this properly\n>> [2] - Let's skip the small talk");
        int[] choicez = {0, 1};
        int creatorChoice = dispObj.awaitChoiceInput(2);
        switch (creatorChoice)
        {
            case 0:
                break;
            case 1:
                preciseStatPicker(10, 10);
        }
    }
    void preciseStatPicker(int majorPoints, int minorPoints)
    {
        Map<String, Integer> playerAttsOld = playerAtts;
        System.out.println(">> Your stats are as follows:");
        int printCtr = 1;
        Map<Integer, String> tempStatIDMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> curStat: playerAtts.entrySet())
        {
            tempStatIDMap.put(printCtr, curStat.getKey());
            printCtr++;
            // System.out.println(">> [" + printCtr++ + "] " + curStat.getKey() + " - " + curStat.getValue());
        }

        int numOptions = printCtr-1; // it now stores numOptions
        int maxIDofMajor = 2; // str, dex, int
        int[] curChoices = new int[2];
        String curChoicePicked = "";
        int curChoiceLevel = 0;
        boolean usingMajorPoints = false;

        while (true)
        {
            spamStatDescriptions(majorPoints, minorPoints);

            curChoices = dispObj.awaitChoiceInputWithIncrement(numOptions, "done");

            if (curChoices[0] == -1)
            {
                if (minorPoints == 0 && majorPoints == 0)
                    return;
                continue;
            }

            curChoicePicked = tempStatIDMap.get(curChoices[0]);
            curChoiceLevel = playerAtts.get(curChoicePicked);
            usingMajorPoints = curChoices[0] < 3;

            if (curChoices[1] == 1)
            {
                if ((majorPoints < 1 && usingMajorPoints) || (minorPoints < 1 && !usingMajorPoints))
                    continue;
                playerAtts.put(curChoicePicked, curChoiceLevel+1);
                if (usingMajorPoints)
                    majorPoints--;
                else
                    minorPoints--;
            }
            else
            {
                if (playerAttsOld.get(curChoicePicked) <= playerAtts.get(curChoicePicked))
                    continue;
                // if player attempts to decrement below what they already had, continues
                playerAtts.put(curChoicePicked, curChoiceLevel-1);
                if (usingMajorPoints)
                    majorPoints++;
                else
                    minorPoints++;
            }
        }
    }

    public void spamStatDescriptions(int majorPoints, int minorPoints)
    {
        System.out.println("\n>> You have [" + majorPoints + "] major trait points, and [" + minorPoints + "] minor trait points");
        int printCtr = 1;
        for (Map.Entry<String, Integer> curStat: playerAtts.entrySet())
        {
            System.out.println(">> [" + printCtr++ + "] " + curStat.getKey() + " - " + curStat.getValue());
        }
        System.out.println(">> Major traits:\n>> Strength boosts your max health and accuracy of your strong attacks");
        System.out.println(">> Dexterity boosts your critical-strike chance and accuracy of your quick attacks");
        System.out.println(">> Intellect boosts your max mana and your spell power (WIP, useless to pick)");
        System.out.println(">> However, the accuracy of your attacks is also determined by your foe's stats!");
        System.out.println("\n>> Minor traits that have no direct use in combat, but may prove invaluable in exploration and story elements!");
        System.out.println(">> Diplomacy - how good you are at convincing others to do what you want them to");
        System.out.println(">> Subterfuge - for staying out of sight and opening locks you're not supposed to");
        System.out.println(">> Arcana - your knowledge of the world beyond this one: rites, rituals, old tongues, incantations");
        System.out.println(">> Willpower - your ability to persevere, even when every part of your body is breaking");
        System.out.println(">> Keen Eye - how good you are at spotting what is meant to be hidden. Also, boosts the amount of gold you find");
        System.out.println(">> You can increase or decrease (so as to undo assignment of what you currently have) your stats by\n" +
                "writing the trait's corresponding number and a + or -, for instance 1+ to increase strength");
        System.out.println(">> Once you're happy with the distribution and have spent all points, write [done]");
    }
}
