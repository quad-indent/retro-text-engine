import java.io.*;
import java.util.*;

public class PlayerClass {
    private static final String DEFAULT_PLAYER_FILE_NAME = "playerSheet.storyData"; // pitiful obfuscation of format, marginally better than nothing
    private static Map<String, Integer> playerBaseVals = new LinkedHashMap<>();
    private static Map<String, Integer> playerAtts = new LinkedHashMap<>();
    private static String playerName = "";
    private static int armour = 0;

    public static String getPlayerName() { return playerName; }
    public static int getPlayerStat(String stat) {
        if (stat.equalsIgnoreCase("Armour")) { return armour; }
        return playerBaseVals.containsKey(stat) ?
                playerBaseVals.get(stat) : playerAtts.get(stat);
    }

    static {
        // Json would be better, but dependencies. This will be some funky .txt parsing instead
//        if (characterSheetPath == null)
//            characterSheetPath = DEFAULT_PLAYER_FILE_NAME;
//        File playerFile = new File(characterSheetPath);
//        if (!playerFile.isFile()) {
//            characterCreator();
//            saveCharacter(characterSheetPath);
//        } else {
//            loadCharacter(characterSheetPath);
//        }
    }

    public static void initPlayer(String characterSheetPath) {
        if (characterSheetPath == null)
            characterSheetPath = DEFAULT_PLAYER_FILE_NAME;
        File playerFile = new File(characterSheetPath);
        if (!playerFile.isFile()) {
            characterCreator();
            saveCharacter(characterSheetPath);
        } else {
            loadCharacter(characterSheetPath);
        }
    }
    public static void defaultPlayerInit() {
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

    public static void characterCreator() {
        defaultPlayerInit();
        System.out.println(">> Hello, and welcome to this story. I am your Narrator.\n>> I will always guide you through " +
                "this beginning, and who knows, maybe we will meet again down the line!");
        System.out.println(">> Tell me, what is your name?");
        playerName = StoryDisplayer.awaitChoiceInput(false, true);
        System.out.println(">> Tell me about yourself, " + playerName + ". Would you prefer to have a little chat about yourself, " +
                "or simply provide me with the precise information about your strengths and weaknesses?");
        System.out.println(">> [1] - Let's do this properly\n>> [2] - Let's skip the small talk");
        int creatorChoice = StoryDisplayer.awaitChoiceInput(2);
        switch (creatorChoice) {
            case 0:
                storyStatPicker();
                break;
            case 1:
                preciseStatPicker(6, 4, null);
                break;
            default:
                break;
        }
    }

    public static int saveCharacter(String playerFile) {
        try {
            if (playerFile == null)
                playerFile = DEFAULT_PLAYER_FILE_NAME;
            FileWriter fileWriter = new FileWriter(playerFile);
            PrintWriter printWriter = getPrintWriter(fileWriter);
            printWriter.close();
            return 0;
        } catch (IOException e) {
            System.out.println("Error! Could not create player save file \"" + playerFile + "\"");
            return 1;
        }
    }

    private static PrintWriter getPrintWriter(FileWriter fileWriter) {
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(playerName);
        for (Integer baseLv : playerBaseVals.values())
            printWriter.println(baseLv);
        printWriter.println(armour);
        for (Integer statLv : playerAtts.values())
            printWriter.println(statLv);
        return printWriter;
    }

    public static int loadCharacter(String playerFile) {
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
        } catch (IOException e) {
            System.out.println("Error! Could not open player save file \"" + playerFile + "\"");
            return 1;
        }
    }

    public static void storyStatPicker() { // 6 major, 4 minor points to assign
        Map<String, Integer> playerAttsOld = new LinkedHashMap<>(playerAtts);
        System.out.println(">> How kind of you to humour me. Tell me, then: what is mightier? " +
                "The pen or the sword?");
        int ans = StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"The pen", "The sword",
                "A knife in the back"});
        String valToIncr = "";
        String secondaryIncr = "";
        String sassRemark = "";
        sassRemark = switch (ans) {
            case 0 -> {
                valToIncr = "Intellect";
                yield "One wonders if that's because you truly think it superior, or simply " +
                        "because wielding the pen is the upper limit of what your physique allows";
            }
            case 1 -> {
                valToIncr = "Strength";
                yield "I imagine a world in which everyone shares your attitude " +
                        "to be a scorched wasteland. I wonder if that's what you aim for";
            }
            case 2 -> {
                valToIncr = "Dexterity";
                yield "Schemes, poisons, intrigue - such pretty words and sophisticated methods " +
                        "to describe a coward's work";
            }
            default -> "";
        };
        incrementStatWithSass(valToIncr, 2, sassRemark);
        System.out.println(">> Perhaps my judgement was made in haste. Let me posit a real question");
        System.out.println(">> Imagine yourself an executioner. You stand next to the condemned upon the gallows.");
        System.out.println(">> Your leg has just kicked the stool from beneath their feet");
        System.out.println(">> You suddenly see the high judge approach, shouting \"Stop the execution!\"");
        System.out.println(">> The condemned is already swinging, but they're innocent!");
        System.out.println(">> It is imperative that you save them. What do you do?");
        ans = StoryDisplayer.awaitChoiceInputFromOptions(new String[]{
                "Wrap yourself around their legs to hold them up",
                "Produce a knife and throw it at the rope to cut it",
                "Prop up the chair for them to stand back on"});
        valToIncr = switch (ans) {
            case 0 -> "Strength";
            case 1 -> "Dexterity";
            case 2 -> "Intellect";
            default -> valToIncr;
        };
        if (playerAtts.get(valToIncr) == 12)
            sassRemark = "Perhaps your earlier answer was truly in earnest";
        else {
            sassRemark = "And here I thought you'd go with a different approach. How quaint";
        }
        incrementStatWithSass(valToIncr, 3, sassRemark);

        System.out.println(">> Now, then. Tell me about what you value; what guides you; what you respect");
        ans = StoryDisplayer.awaitChoiceInputFromOptions(new String[]{
                "A silver tongue", "A quiet approach", "Insight into the world beyond",
                "Persevering where others may fall", "Spotting what others might miss"
        });
        sassRemark = switch (ans) {
            case 0 -> {
                valToIncr = "Diplomacy";
                secondaryIncr = "Intellect";
                yield "No pick can open hearts and penetrate minds - words can, as well as actual doors";
            }
            case 1 -> {
                valToIncr = "Subterfuge";
                secondaryIncr = "Dexterity";
                yield "All it takes is a single moment of someone lowering their guard just enough. " +
                        "You know how to spot that moment well";
            }
            case 2 -> {
                valToIncr = "Arcana";
                secondaryIncr = "Intellect";
                yield "Some think that there is no more to this world than meets the eye. " +
                        "You know this not to be case";
            }
            case 3 -> {
                valToIncr = "Willpower";
                secondaryIncr = "Strength";
                yield "Where there is enough will, flesh and mind can withstand nigh anything. " +
                        "Push your thresholds. Become indestructible";
            }
            case 4 -> {
                valToIncr = "Keen Eye";
                secondaryIncr = "Dexterity";
                yield "Although used by most, few hone their sight to the extent you have. " +
                        "You know exactly where to look to find what was not meant to be seen";
            }
            default -> "Oh dear, something terrible has happened";
        };
        incrementStatWithSass(valToIncr, 3, sassRemark);
        playerAtts.put(secondaryIncr, playerAtts.get(secondaryIncr) + 1);
        System.out.println(">> And now, pick a trinket that you would like to be buried with");
        ans = StoryDisplayer.awaitChoiceInputFromOptions(new String[]{
                "A coin", "An opulent ring", "A silver needle", "A feather", "A candle"
        });
        sassRemark = switch (ans) {
            case 0 -> {
                valToIncr = "Arcana";
                yield "The ferryman's toll must be paid";
            }
            case 1 -> {
                valToIncr = "Keen Eye";
                yield "Those who remain should remember you by how keen your eye was for riches";
            }
            case 2 -> {
                valToIncr = "Subterfuge";
                yield "He himself might his quietus make with a bare bodkin. Or someone else's. " +
                        "A trinket befitting of a rogue to be buried with";
            }
            case 3 -> {
                valToIncr = "Diplomacy";
                yield "So that you might find peace in death as you had sought to bring it amongst the living " +
                        "through diplomacy";
            }
            case 4 -> {
                valToIncr = "Willpower";
                yield "Should you wake from this slumber, you will have a source of light at the ready " +
                        "to persevere and march on once more";
            }
            default -> "Oh dear, something terrible has happened";
        };
        incrementStatWithSass(valToIncr, 1, sassRemark);
        System.out.println(">> What a curious soul you are, " + playerName +
                ". I do look forward to seeing you navigate what lies ahead.");
        System.out.println(">> But, for now, do take a look at what you've wound up with");
        System.out.println(">> Oh, and do feel free to adjust before you embark on your journey!");
        preciseStatPicker(0, 0, playerAttsOld);
    }
    public static void incrementStatWithSass(String statToIncr, int valToIncrBy, String sassRemark) {
        playerAtts.put(statToIncr, playerAtts.get(statToIncr) + valToIncrBy);
        System.out.println(">> " + sassRemark);
    }
    public static void preciseStatPicker(int majorPoints, int minorPoints, Map<String, Integer> playerAttsOld) {
        if (playerAttsOld == null)
            playerAttsOld = new LinkedHashMap<>(playerAtts);
        System.out.println(">> Your stats are as follows:");
        int printCtr = 1;
        Map<Integer, String> tempStatIDMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> curStat : playerAtts.entrySet()) {
            tempStatIDMap.put(printCtr++, curStat.getKey());
        }

        int numOptions = printCtr - 1; // it now stores numOptions
        int maxIDofMajor = 2; // str, dex, int
        int[] curChoices = new int[2];
        String curChoicePicked = "";
        int curChoiceLevel = 0;
        boolean usingMajorPoints = false;

        while (true) {
            spamStatDescriptions(majorPoints, minorPoints);
            curChoices = StoryDisplayer.awaitChoiceInputWithIncrement(numOptions, "done");

            if (curChoices[0] == -1) {
                if (minorPoints == 0 && majorPoints == 0)
                    return;
                continue;
            }
            curChoicePicked = tempStatIDMap.get(curChoices[0]);
            curChoiceLevel = playerAtts.get(curChoicePicked);
            usingMajorPoints = curChoices[0] < 3;

            if (curChoices[1] == 1) {
                if ((majorPoints < 1 && usingMajorPoints) || (minorPoints < 1 && !usingMajorPoints))
                    continue;
                playerAtts.put(curChoicePicked, curChoiceLevel + 1);
                if (usingMajorPoints)
                    majorPoints--;
                else
                    minorPoints--;
            } else {
                if (playerAttsOld.get(curChoicePicked) >= playerAtts.get(curChoicePicked))
                    continue;
                // if player attempts to decrement below what they already had, continues
                playerAtts.put(curChoicePicked, curChoiceLevel - 1);
                if (usingMajorPoints)
                    majorPoints++;
                else
                    minorPoints++;
            }
        }
    }

    public static void spamStatDescriptions(int majorPoints, int minorPoints) {
        System.out.println("\n>> You have [" + majorPoints + "] major trait points, and [" + minorPoints + "] minor trait points");
        int printCtr = 1;
        for (Map.Entry<String, Integer> curStat : playerAtts.entrySet()) {
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

    public static boolean statComparer(int compareAgainst, String statName) {
        boolean shouldBeGreater = compareAgainst >= 0;
        int statValInQuestion = playerAtts.containsKey(statName) ?
                playerAtts.get(statName) : playerBaseVals.get(statName);
        if (shouldBeGreater) {
            return statValInQuestion > compareAgainst;
        }
        return statValInQuestion < compareAgainst;
    }
}
