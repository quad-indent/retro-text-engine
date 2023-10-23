import java.io.*;
import java.util.*;

public class PlayerClass {
    private static final String DEFAULT_PLAYER_FILE_NAME = "playerSheet.storyData"; // pitiful obfuscation of format, marginally better than nothing
    private String playerName = "";
    private int armour = 0;
    Map<String, Integer> playerBaseVals = new LinkedHashMap<>();
    Map<String, Integer> playerAtts = new LinkedHashMap<>();
    StoryDisplayer dispObj;
    PlayerClass(String characterSheetPath, StoryDisplayer dispObjRef)
    {
        dispObj = dispObjRef;
        // Json would be better, but dependencies. This will be some funky .txt parsing instead
        if (characterSheetPath == null)
            characterSheetPath = DEFAULT_PLAYER_FILE_NAME;
        File playerFile = new File(characterSheetPath);
        if (!playerFile.isFile())
        {
            characterCreator();
            saveCharacter(characterSheetPath);
        }
        else
        {
            loadCharacter(characterSheetPath);
        }
    }
    public void defaultPlayerInit()
    {
        playerBaseVals.put("playerLevel", 1);
        playerBaseVals.put("curXP", 0);
        playerBaseVals.put("neededXP", LevelEnums.XPArray[2]);
        playerBaseVals.put("maxHealth", 50);
        playerBaseVals.put("curHealth", 50);
        playerBaseVals.put("maxMana", 20);
        playerBaseVals.put("curMana", 20);
        playerAtts.put("Strength", 10);
        playerAtts.put("Dexterity", 10);
        playerAtts.put("Intellect", 10);
        playerAtts.put("Diplomacy", 0);
        playerAtts.put("Subterfuge", 0);
        playerAtts.put("Arcana", 0);
        playerAtts.put("Willpower", 0);
        playerAtts.put("Keen Eye", 0);
    }
    public void characterCreator()
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
                preciseStatPicker(6, 4);
        }
    }

    public int saveCharacter(String playerFile)
    {
        try {
            if (playerFile == null)
                playerFile = DEFAULT_PLAYER_FILE_NAME;
            FileWriter fileWriter = new FileWriter(playerFile);
            PrintWriter printWriter = getPrintWriter(fileWriter);
            printWriter.close();
            return 0;
        }
        catch (IOException e) {
            System.out.println("Error! Could not create player save file \"" + playerFile + "\"");
            return 1;
        }
    }
    private PrintWriter getPrintWriter(FileWriter fileWriter) {
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(playerName);
        for (Integer baseLv: playerBaseVals.values())
            printWriter.println(baseLv);
        printWriter.println(armour);
        for (Integer statLv: playerAtts.values())
            printWriter.println(statLv);
        return printWriter;
    }

    public int loadCharacter(String playerFile)
    {
        if (playerFile == null)
            playerFile = DEFAULT_PLAYER_FILE_NAME;
        try {
            FileReader infoReader = new FileReader(playerFile);
            Scanner storyReader = new Scanner(infoReader);
            playerName = storyReader.nextLine();
            playerBaseVals.replaceAll((n, v) -> Integer.parseInt(storyReader.nextLine()));
            armour = Integer.parseInt(storyReader.nextLine());
            playerAtts.replaceAll((n, v) -> Integer.parseInt(storyReader.nextLine()));
            storyReader.close();
            return 0;
        }
        catch (IOException e) {
            System.out.println("Error! Could not open player save file \"" + playerFile + "\"");
            return 1;
        }
    }
    public void preciseStatPicker(int majorPoints, int minorPoints)
    {
        Map<String, Integer> playerAttsOld = new LinkedHashMap<>(playerAtts);
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
                if (playerAttsOld.get(curChoicePicked) >= playerAtts.get(curChoicePicked))
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
