package player;

import inventory.ArmourItem;
import inventory.Inventory;
import inventory.Item;
import inventory.WeaponItem;
import storyBits.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class PlayerClass {
    private static final String DEFAULT_PLAYER_FILE_NAME = "playerSheet.storyData"; // pitiful obfuscation of format, marginally better than nothing
    private static String desiredSaveDest = "";
    private static Map<String, Integer> playerBaseVals = new LinkedHashMap<>();
    private static Map<String, Integer> playerAtts = new LinkedHashMap<>();
    private static String playerName = "";
    private static int armour = 0;
    public static String getDesiredSaveDest() {
        return desiredSaveDest;
    }

    public static void setDesiredSaveDest(String desiredSaveDest) {
        PlayerClass.desiredSaveDest = desiredSaveDest;
    }

    public static void setPlayerName(String playerName) {
        PlayerClass.playerName = playerName;
    }
    public static String getPlayerName() { return playerName; }

    public static int getArmour() {
        return armour;
    }

    public static void setArmour(int armour) {
        PlayerClass.armour = armour;
    }

    public static Map<String, Integer> getPlayerBaseVals() {
        return playerBaseVals;
    }

    public static void setPlayerBaseVals(Map<String, Integer> playerBaseVals) {
        PlayerClass.playerBaseVals = playerBaseVals;
    }

    public static Map<String, Integer> getPlayerAtts() {
        return playerAtts;
    }

    public static void setPlayerAtts(Map<String, Integer> playerAtts) {
        PlayerClass.playerAtts = playerAtts;
    }

    public static int getPlayerStat(String stat) {
        stat = switch (stat.toLowerCase()) {
            case "curlevel", "level", "lvl", "lv":
                yield "playerLevel";
            case "xpneeded", "nextxp":
                yield "neededXP";
            case "xp", "exp", "experience":
                yield "curXP";
            case "gold", "money", "currency":
                yield "gold";
            default:
                yield stat;
        };
        // cba to remember
        if (stat.equalsIgnoreCase("gold")) { return Inventory.getCurrentGold(); }
        if (stat.equalsIgnoreCase("Armour")) { return getArmour(); }
        return getPlayerBaseVals().containsKey(stat) ?
                getPlayerBaseVals().get(stat) : getPlayerAtts().get(stat);
    }

    public static void incrementHealth(int incrementBy) {
        getPlayerBaseVals().put("curHealth", getPlayerBaseVals().get("curHealth") + incrementBy);
        if (getPlayerBaseVals().get("curHealth") > getPlayerBaseVals().get("maxHealth")) {
            getPlayerBaseVals().put("curHealth", getPlayerBaseVals().get("curHealth"));
        } else if (getPlayerBaseVals().get("curHealth") < 0){
            getPlayerBaseVals().put("curHealth", 0);
        }
    }
    public static void incrementXP(int xpIncr) throws Exception {
        getPlayerBaseVals().put("curXP", getPlayerBaseVals().get("curXP") + xpIncr);
        int majorPoints = 0;
        int minorPoints = 0;
        while (getPlayerBaseVals().get("curXP") >= getPlayerBaseVals().get("neededXP")) {
            // if levels up
            getPlayerBaseVals().put("curXP", getPlayerBaseVals().get("curXP") - getPlayerBaseVals().get("neededXP"));
            getPlayerBaseVals().put("playerLevel", getPlayerBaseVals().get("playerLevel") + 1);
            getPlayerBaseVals().put("neededXP", LevelEnums.XPArray[getPlayerBaseVals().get("playerLevel") + 1]);
            majorPoints++;
            if (getPlayerBaseVals().get("playerLevel") % 5 == 0) {
                minorPoints++;
                majorPoints++;
            }
            if (getPlayerBaseVals().get("playerLevel") % 6 == 0) {
                minorPoints++;
            }
            if (getPlayerBaseVals().get("playerLevel") % 10 == 0) {
                minorPoints += 2;
                majorPoints += 2;
            }
        }
        if (minorPoints > 0 || majorPoints > 0)
            preciseStatPicker(majorPoints, minorPoints, null, true);
    }

    public static boolean playerPassesReq(String statAndReqCombo) throws Exception {
        // again, cuz lazy
        if (!StoryBlockMaster.stringContainsAny(statAndReqCombo, new char[]{'>', '<'})) {
            GlobalConf.issueLog("Error trying to check requirement! Faulty instruction " +
                    "received: " + statAndReqCombo, GlobalConf.SEVERITY_LEVEL_ERROR, false);
            return false;
        }
        int isPositive = StoryBlockMaster.stringContainsAny(statAndReqCombo, new char[]{'>'}) ? 1 : -1;
        String[] statz = statAndReqCombo.split((isPositive == 1 ? ">" : "<"));
        if (statz.length != 2) {
            GlobalConf.issueLog("Error trying to check requirement! Faulty instruction " +
                    "received: " + statAndReqCombo, GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        for (int i = 0; i < 2; i++) {
            statz[i] = StoryDisplayer.removeWhiteSpace(statz[i]);
        }
        int statInQuestion = getPlayerStat(statz[0]);
        return isPositive == 1 ? statInQuestion > Integer.parseInt(statz[1]) :
                statInQuestion < Integer.parseInt(statz[1]);
    }
    public static boolean playerPassesReq(Map<String, Integer> relevantReqz) {
        for (Map.Entry<String, Integer> curEntry : relevantReqz.entrySet()) {
            if (!playerPassesReq(curEntry.getKey(), curEntry.getValue()))
                return false;
        }
        return true;
    }
    public static boolean playerPassesReq(String relevantStat, int valRequired) {
        // if value negative, stat needs to be below abs val of it
        int statInQuestion = getPlayerStat(relevantStat);
        if (valRequired < 0) {
            return statInQuestion < valRequired * -1;
        }
        return statInQuestion > valRequired;
    }
    public static void incrementPlayerStat(String stat) throws Exception {
        // cuz lazy
        if (!StoryBlockMaster.stringContainsAny(stat, new char[]{'+', '-'})) {
            GlobalConf.issueLog("Error trying to increment stat! Faulty instruction " +
                    "received: " + stat, GlobalConf.SEVERITY_LEVEL_ERROR, false);
            return;
        }
        int isPositive = StoryBlockMaster.stringContainsAny(stat, new char[]{'+'}) ? 1 : -1;
        String[] statz = stat.split((isPositive == 1 ? "+" : "-"));
        if (statz.length != 2) {
            GlobalConf.issueLog("Error trying to increment stat! Faulty instruction " +
                    "received: " + stat, GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        for (int i = 0; i < 2; i++) {
            statz[i] = StoryDisplayer.removeWhiteSpace(statz[i]);
        }
        incrementPlayerStat(statz[0], Integer.parseInt(statz[1]) * isPositive);
    }
    public static void incrementPlayerStat(Map<String, Integer> statzInQuestion, boolean isUnequipping) throws Exception {
        for (Map.Entry<String, Integer> curEntry : statzInQuestion.entrySet()) {
            incrementPlayerStat(curEntry.getKey(),
                    (isUnequipping ? curEntry.getValue() * -1 : curEntry.getValue()));
        }
    }
    public static void refillHealthAndMana() throws Exception {
        incrementPlayerStat("curHealth", Integer.MAX_VALUE);
        incrementPlayerStat("curMana", Integer.MAX_VALUE);
    }
    public static void incrementPlayerCurrency(int byHowMuch, boolean procKeenEye) {
        if (procKeenEye) {
            byHowMuch *= (int) Math.ceil(1. + ((double)getPlayerStat(PlayerKeywordz.getKeenEyeName()) * 0.7));
        }
        Inventory.setCurrentGold(Inventory.getCurrentGold() + byHowMuch);
    }
    public static void incrementPlayerStat(String stat, int byHowMuch) throws Exception {
        if (byHowMuch == 0)
            return;
        if (stat.equalsIgnoreCase("armour")) {
            setArmour(getArmour() + byHowMuch);
            return;
        }
        if (byHowMuch == Integer.MAX_VALUE || byHowMuch == Integer.MIN_VALUE ||
            stat.equalsIgnoreCase("curhealth") ||
                stat.equalsIgnoreCase("curmana")) {
            handleHealthAndManaIncr(byHowMuch, stat);
        } else {
            if (getPlayerBaseVals().containsKey(stat)) {
                if (stat.equalsIgnoreCase(PlayerKeywordz.getStrengthName())) {
                    getPlayerBaseVals().put("maxHealth", getPlayerBaseVals().get("maxHealth") + 5);
                    incrementPlayerStat("curHealth", 5);
                }
                getPlayerBaseVals().put(stat, getPlayerBaseVals().get(stat) + byHowMuch);
                return;
            } else if (getPlayerAtts().containsKey(stat)) {
                getPlayerAtts().put(stat, getPlayerAtts().get(stat) + byHowMuch);
            } else {
                GlobalConf.issueLog("No such stat exists in the player class!", GlobalConf.SEVERITY_LEVEL_ERROR,
                        true);
            }
        }
    }
    private static void handleHealthAndManaIncr(int byHowMuch, String stat) {
        if (byHowMuch == Integer.MIN_VALUE) {
            switch (stat.toLowerCase()) {
                case ("curhealth") -> {
                    getPlayerBaseVals().put("curHealth", 0);
                }
                case ("curmana") -> {
                    getPlayerBaseVals().put("curMana", 0);
                }
                default -> {
                    return;
                }
            }
        } else if (byHowMuch == Integer.MAX_VALUE) {
            switch (stat.toLowerCase()) {
                case ("curhealth") -> {
                    getPlayerBaseVals().put("curHealth", getPlayerBaseVals().get("maxHealth"));
                }
                case ("curmana") -> {
                    getPlayerBaseVals().put("curMana", getPlayerBaseVals().get("maxMana"));
                }
                default -> {
                    return;
                }
            }
        } else {
            getPlayerBaseVals().put(stat, getPlayerBaseVals().get(stat) + byHowMuch);
            if (getPlayerBaseVals().get(stat) > getPlayerBaseVals().
                    get(stat.replace("cur", "max"))) {
                getPlayerBaseVals().put(stat, getPlayerBaseVals().get(stat.replace("cur", "max")));
            }
        }
    }
    public static int initPlayer(String characterSheetPath) throws Exception {
        if (characterSheetPath == null)
            characterSheetPath = DEFAULT_PLAYER_FILE_NAME;
        setDesiredSaveDest(characterSheetPath);
        File playerFile = new File(getDesiredSaveDest());
        int playerStoryPage = 0;
        if (!playerFile.isFile()) {
            characterCreator();
            saveCharacter(0);
        } else {
            playerInitWithSaucyStatz();
            playerStoryPage = loadCharacter();
        }
        return playerStoryPage;
    }
    public static void playerInitWithSaucyStatz() {
        getPlayerBaseVals().put("playerLevel", 1);
        getPlayerBaseVals().put("curXP", 0);
        getPlayerBaseVals().put("neededXP", LevelEnums.XPArray[2]);
        getPlayerBaseVals().put("maxHealth", 50);
        getPlayerBaseVals().put("curHealth", 50);
        getPlayerBaseVals().put("maxMana", 20);
        getPlayerBaseVals().put("curMana", 20);
        getPlayerAtts().put(PlayerKeywordz.getStrengthName(), 10);
        getPlayerAtts().put(PlayerKeywordz.getDexterityName(), 10);
        getPlayerAtts().put(PlayerKeywordz.getIntellectName(), 10);
        for (String curName: PlayerKeywordz.getMinorStats()) {
            if (curName == null) {
                break;
            }
            getPlayerAtts().put(curName, 0);
        }
    }
    public static void defaultPlayerInit() {
        getPlayerBaseVals().put("playerLevel", 1);
        getPlayerBaseVals().put("curXP", 0);
        getPlayerBaseVals().put("neededXP", LevelEnums.XPArray[2]);
        getPlayerBaseVals().put("maxHealth", 50);
        getPlayerBaseVals().put("curHealth", 50);
        getPlayerBaseVals().put("maxMana", 20);
        getPlayerBaseVals().put("curMana", 20);
        getPlayerAtts().put("Strength", 10);
        getPlayerAtts().put("Dexterity", 10);
        getPlayerAtts().put("Intellect", 10);
        getPlayerAtts().put("Diplomacy", 0);
        getPlayerAtts().put("Subterfuge", 0);
        getPlayerAtts().put("Arcana", 0);
        getPlayerAtts().put("Willpower", 0);
        getPlayerAtts().put("Keen Eye", 0);
    }

    public static void characterCreator() throws Exception {
        playerInitWithSaucyStatz();
        System.out.println(">> Hello, and welcome to this story. I am your Narrator.\n>> I will always guide you through " +
                "this beginning, and who knows, maybe we will meet again down the line!");
        System.out.println(">> Tell me, what is your name?");
        setPlayerName(StoryDisplayer.awaitChoiceInput(false, true));
        if (GlobalConf.isMinimalConfig()) {
            System.out.println("I do look forward to seeing you navigate what lies ahead, " + getPlayerName() + ".");
            StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
            return;
        }
        System.out.println(">> Tell me about yourself, " + getPlayerName() + ". Would you prefer to have a little chat about yourself, " +
                "or simply provide me with the precise information about your strengths and weaknesses?");
        System.out.println(">> [1] - Let's do this properly\n>> [2] - Let's skip the small talk");
        int creatorChoice = StoryDisplayer.awaitChoiceInput(2);
        switch (creatorChoice) {
            case 0:
                storyStatPicker();
                break;
            case 1:
                preciseStatPicker(6, 4, null, false);
                break;
            default:
                break;
        }
        Inventory.populateInventoryFromSave(new String[]{"", ""});
    }
    public static int saveCharacter(int curPage) {
        try {
            FileWriter fileWriter = new FileWriter(getDesiredSaveDest());
            PrintWriter printWriter = getPrintWriter(fileWriter, curPage);
            printWriter.close();
            fileWriter.close();
            return 0;
        } catch (IOException e) {
            System.out.println("Error! Could not create player save file \"" + getDesiredSaveDest() + "\"");
            return 1;
        }
    }
    private static PrintWriter getPrintWriter(FileWriter fileWriter, int curPage) {
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(getPlayerName());
        if (GlobalConf.isMinimalConfig()) {
            printWriter.println(curPage);
            return printWriter;
        }
        printWriter.println(curPage); // starts at story index 0

        printWriter.println(getArmour());

        StringBuilder equipmentLine = new StringBuilder();
        for (Item i : Inventory.getEquippedNecks()) {
            if (i != null)
                equipmentLine.append(i.getItemID()).append(" ");
        }
        for (Item i : Inventory.getEquippedTrinkets()) {
            if (i != null) {
                equipmentLine.append(i.getItemID()).append(" ");
            }
        }
        Map<Integer, Boolean> twoHanders = new LinkedHashMap<>();
        // cause if using list, remove might interpret ID as index
        for (WeaponItem i : Inventory.getEquippedWeapons()) {
            if (i == null)
                continue;
            if (!i.isIs1H() && !twoHanders.containsKey(i.getItemID())) {
                twoHanders.put(i.getItemID(), true);
            } else if (!i.isIs1H() && twoHanders.containsKey(i.getItemID())) {
                twoHanders.remove(i.getItemID());
                continue;
            }
            equipmentLine.append(i.getItemID()).append(" ");
        }
        for (ArmourItem i : Inventory.getEquippedArmour().values()) {
            if (i == null)
                continue;
            equipmentLine.append(i.getItemID()).append(" ");
        }
        if (!equipmentLine.isEmpty()) {
            printWriter.println(equipmentLine.substring(0, equipmentLine.toString().length() - 1));
        } else {
            printWriter.println("");
        }
        // printWriter.println(equipmentLine.substring(0, equipmentLine.toString().length() - 1));
        equipmentLine = new StringBuilder();
        for (Item i : Inventory.getInventorySpace()) {
            equipmentLine.append(i.getItemID()).append(" ");
        }
        equipmentLine.append(Inventory.getCurrentGold());

        printWriter.println(equipmentLine);

        for (Integer baseLv : getPlayerBaseVals().values())
            printWriter.println(baseLv);

        for (Integer statLv : getPlayerAtts().values())
            printWriter.println(statLv);

        return printWriter;
    }

    public static int loadCharacter() throws Exception {
        int playerStoryPage = 0;
        int numLinezPresent = -1;
        try {
            Stream<String> fileStream = Files.lines(Paths.get(getDesiredSaveDest()));
            numLinezPresent = (int)fileStream.count();
            fileStream.close();
            if ((numLinezPresent != ReturnsAndDataEnums.FULL_CONF_LINES.val + PlayerKeywordz.getNumMinorStatz() &&
                    !GlobalConf.isMinimalConfig()) ||
                    (numLinezPresent != ReturnsAndDataEnums.MINMAL_CONF_LINES.val && GlobalConf.isMinimalConfig())) {
                GlobalConf.issueLog("Got malformed player sheet data! " +
                        "Expected " + (ReturnsAndDataEnums.FULL_CONF_LINES.val + PlayerKeywordz.getNumMinorStatz()) +
                        " lines of info; got " + numLinezPresent + " instead!",
                        GlobalConf.SEVERITY_LEVEL_ERROR, true);
            }
            GlobalConf.issueLog("Attempting to parse character sheet . . .", GlobalConf.SEVERITY_LEVEL_INFO);
            FileReader infoReader = new FileReader(getDesiredSaveDest());
            Scanner storyReader = new Scanner(infoReader);
            if (!GlobalConf.isMinimalConfig()) {
                setPlayerName(storyReader.nextLine());
                playerStoryPage = Integer.parseInt(storyReader.nextLine());
                setArmour(Integer.parseInt(storyReader.nextLine()));
                Inventory.populateInventoryFromSave(storyReader);
                getPlayerBaseVals().replaceAll((n, v) -> Integer.parseInt(storyReader.nextLine()));
                getPlayerAtts().replaceAll((n, v) -> Integer.parseInt(storyReader.nextLine()));
            } else {
                setPlayerName(storyReader.nextLine());
                playerStoryPage = Integer.parseInt(storyReader.nextLine());
            }
            storyReader.close();
            return playerStoryPage;
        } catch (IOException e) {
            GlobalConf.issueLog("Error! Could not open player save file \"" + getDesiredSaveDest() + "\"",
                    GlobalConf.SEVERITY_LEVEL_ERROR, false);
            return 1;
        }
    }

    public static void storyStatPicker() throws Exception { // 6 major, 4 minor points to assign
        Map<String, Integer> playerAttsOld = new LinkedHashMap<>(getPlayerAtts());
        StoryBlockMaster creatorBlock = new StoryBlockMaster(FileParser.joinConfigFolder("charCreatorPrompts.txt"));
        StoryDisplayer.storyLoop(creatorBlock.getStoryObj(), 0, true);
        preciseStatPicker(0, 0, playerAttsOld, false);
    }
    public static void incrementStatWithSass(String statToIncr, int valToIncrBy, String sassRemark) {
        getPlayerAtts().put(statToIncr, getPlayerAtts().get(statToIncr) + valToIncrBy);
        System.out.println(">> " + sassRemark);
    }
    public static void preciseStatPicker(int majorPoints, int minorPoints, Map<String, Integer> playerAttsOld,
                                         boolean isLevelUp) throws Exception {
        if (playerAttsOld == null)
            playerAttsOld = new LinkedHashMap<>(getPlayerAtts());
        if (isLevelUp) {
            incrementPlayerStat("curHealth", Integer.MAX_VALUE);
            incrementPlayerStat("curMana", Integer.MAX_VALUE);
            System.out.println(">> You advance from level " + (getPlayerStat("playerLevel") - 1) +
                    " to level " + getPlayerStat("playerLevel") + "!");
        }
        System.out.println(">> Your stats are as follows:");
        int printCtr = 1;
        Map<Integer, String> tempStatIDMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> curStat : getPlayerAtts().entrySet()) {
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
            curChoiceLevel = getPlayerAtts().get(curChoicePicked);
            usingMajorPoints = curChoices[0] < 3;

            if (curChoices[1] == 1) {
                if ((majorPoints < 1 && usingMajorPoints) || (minorPoints < 1 && !usingMajorPoints))
                    continue;
                getPlayerAtts().put(curChoicePicked, curChoiceLevel + 1);
                if (usingMajorPoints)
                    majorPoints--;
                else
                    minorPoints--;
            } else {
                if (playerAttsOld.get(curChoicePicked) >= getPlayerAtts().get(curChoicePicked))
                    continue;
                // if player attempts to decrement below what they already had, continues
                getPlayerAtts().put(curChoicePicked, curChoiceLevel - 1);
                if (usingMajorPoints)
                    majorPoints++;
                else
                    minorPoints++;
            }
        }
    }

    public static void spamStatDescriptions(int majorPoints, int minorPoints) {
        System.out.println("\n>> You have [" + majorPoints + "] major trait " +
                (majorPoints == 1 ? "point" : "points") + ", " +
                "and [" + minorPoints + "] minor trait " +
                (minorPoints == 1 ? "point" : "points"));
        int printCtr = 1;
        for (Map.Entry<String, Integer> curStat : getPlayerAtts().entrySet()) {
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
        int statValInQuestion = -1;
        if (getPlayerAtts().containsKey(statName)) {
            statValInQuestion = getPlayerAtts().get(statName);
        } else if (getPlayerBaseVals().containsKey(statName)) {
            statValInQuestion = getPlayerBaseVals().get(statName);
        } else {
            // if gold is the "stat" in question
            statValInQuestion = Inventory.getCurrentGold();
        }
        if (shouldBeGreater) {
            return statValInQuestion > compareAgainst;
        }
        return statValInQuestion < compareAgainst * -1;
    }

    public static boolean checkForDeath(boolean handleEulogy) {
        boolean isDead = getPlayerBaseVals().get("curHealth") <= 0;
        if (isDead) {
            if (handleEulogy) {
                displayEulogy();
            }
        }
        return isDead;
    }
    public static void displayEulogy() {
        System.out.println(">> And so ends the story of " + getPlayerName());
        System.out.println(">> There was so much more in store for you, you poor soul");
        System.out.println(">> May we meet again");
    }
}
