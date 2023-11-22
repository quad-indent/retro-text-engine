package storyBits;

import combat.*;
import inventory.Inventory;
import inventory.InventoryCache;
import inventory.Item;
import player.PlayerClass;

import java.io.File;
import java.util.*;
import audio.ClipPlayer;
import player.PlayerKeywordz;

interface inputVerificator {
    Boolean checkInput(int i, int[] x);
}

public class StoryDisplayer {
    private static final Scanner scannerObj = new Scanner(System.in);
    private static final int HP_BAR_LEN = 16;
    private static int curIndex = -1;

    public static int getCurIndex() {
        return curIndex;
    }

    public static void setCurIndex(int curIndex) {
        StoryDisplayer.curIndex = curIndex;
    }
    public static String replaceTxtWithKeywordz(String raw) {
        return raw.replaceAll("\\^PLAYERNAME\\^", PlayerClass.getPlayerName());
    }
    public static void storyLoop(ArrayList<StoryBlock> storyObj, int beginAt, boolean suppressInvAndEq) throws Exception {
        setCurIndex(beginAt);
        StoryBlock curObj = storyObj.get(getCurIndex()).refineCurStoryBlock();
        int nextChoice;
        int rawChoicePicked;
        int pureChoiceLen = -1;
        int choiceAdditions = 0;
        if (!Inventory.isMalformedEq()) {
            choiceAdditions++;
        }
        if (!Inventory.isMalformedInv()) {
            choiceAdditions++;
        }
        if (suppressInvAndEq) {
            choiceAdditions = 0;
        }
        while (getCurIndex() < storyObj.size()) {
            for (String str: Inventory.simpleStringWrapper(curObj.getPromptText(), -1, true)) {
                System.out.println(replaceTxtWithKeywordz(str));
            }
            ClipPlayer.playTune(curObj.getTuneToPlay());
            List<String> currentChoicez = curObj.getChoices();
            printChoiceOptions(currentChoicez, !(Inventory.isMalformedInv() || suppressInvAndEq),
                    !(Inventory.isMalformedEq() || suppressInvAndEq));
            pureChoiceLen = getChoiceOptions(currentChoicez, false, false).length;

            rawChoicePicked = awaitChoiceInput(
                    pureChoiceLen + choiceAdditions); // +2 since allowing inv and eq view
            if (rawChoicePicked >= pureChoiceLen && rawChoicePicked < pureChoiceLen + choiceAdditions) {
                if (rawChoicePicked == pureChoiceLen)
                    Inventory.displayInventoryOrEq(Inventory.eqCats.INVENTORY, false, "");
                else
                    Inventory.displayEquipment();
                continue;
            }
            nextChoice = curObj.getChoiceDestinationAtID(rawChoicePicked); // getChoiceDestinationAtChoiceStr(currentChoicez.get(rawChoicePicked));
            if (nextChoice == -1)
                return;
            if (curObj.getEphemeralChoices().get(rawChoicePicked)) {
                // tempObj.popChoice(rawChoicePicked);
                storyObj.get(getCurIndex()).popChoice(currentChoicez.get(rawChoicePicked));
            }
            setCurIndex(nextChoice);

            if (curObj.getCombatantInfo().get(rawChoicePicked) != null) { // getCombatantAtChoice(currentChoicez.get(rawChoicePicked)) != null) {
                // is combat
                Foe currentFoe = getFoe(curObj, rawChoicePicked);
                try {
                    PlayerClass.incrementXP(CombatUtils.combatLoop(currentFoe));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (!curObj.isStatCheckAtChoiceStr(currentChoicez.get(rawChoicePicked))) {
                // is stat incrementing
                String relevantStat = curObj.getRelevantStat().get(rawChoicePicked);
                if (relevantStat.equals("curXP")) {
                    PlayerClass.incrementXP(curObj.getStatVal().get(rawChoicePicked));
                } else if (relevantStat.equalsIgnoreCase("item")) {
                    Inventory.addItemToInventory(Item.smartItemInit(curObj.getStatVal().get(rawChoicePicked)));
                } else if (!relevantStat.matches("^[0-9]\\d*$")) {
                    // if it's not numeric, i.e., if it's not an item
                    PlayerClass.incrementPlayerStat(relevantStat,
                            curObj.getStatVal().get(rawChoicePicked));
                } else {
                    // if is an item requirement (which has been passed, else the choice wouldn't be displayed
                    Inventory.removeInventoryItem(Integer.parseInt(relevantStat), false);
                }
            }
            curObj = storyObj.get(getCurIndex()).refineCurStoryBlock();
            PlayerClass.saveCharacter(getCurIndex());
            if (PlayerClass.checkForDeath(true)) {
                File playerFile = new File(PlayerClass.DEFAULT_PLAYER_FILE_NAME);
                playerFile.delete();
                return;
            }
        }
    }

    private static Foe getFoe(StoryBlock curObj, int rawChoicePicked) throws Exception {
        String[] combatantInfo = curObj.getCombatantInfo().get(rawChoicePicked);
        return FoeParser.parseFoe(combatantInfo[0], combatantInfo[1]);
    }

    public static int[] getChoiceOptions(List<String> choicez, boolean hasInventoryChoice,
                                         boolean hasEquipmentChoice) {
        int choiceLen = choicez.size();
        if (hasInventoryChoice)
            choiceLen++;
        if (hasEquipmentChoice)
            choiceLen++;
        int[] options = new int[choiceLen];
        for (int i = 0; i < choiceLen; i++)
            options[i] = i + 1;
        return options;
    }

    public static void printChoiceOptions(List<String> choicez, boolean offerInventory, boolean offerEquipment) {
        int choiceLen = 1;
        for (String choice : choicez) {
            System.out.println(GlobalConf.getPromtTextPrefix() + "[" + choiceLen++ + "] " + choice);
        }
        if (offerInventory)
            System.out.println(GlobalConf.getPromtTextPrefix() + "[" + choiceLen++ + "] view inventory");
        if (offerEquipment)
            System.out.println(GlobalConf.getPromtTextPrefix() + "[" + choiceLen + "] view equipment");
    }

    public static int awaitChoiceInput(int highestOptionVal) {
        int[] options = genOptionsArr(highestOptionVal);
        inputVerificator checker = (input, arr) -> Arrays.stream(arr).anyMatch(e -> e == input);
        while (true) {
            String playerInput = getRawPlayerInput();
            int parsedInput;
            try {
                parsedInput = Integer.parseInt(playerInput);
            } catch (NumberFormatException e) {
                continue;
            }
            if (checker.checkInput(parsedInput, options))
                return Integer.parseInt(playerInput) - 1;
        }
    }

    public static String awaitChoiceInput(boolean canBeEmpty, boolean promptConfirmation) {
        while (true) {
            String playerInput = getRawPlayerInput();

            // Here, else-ifs are redundant as each check either continues the loop or returns
            // My skipping of "else" is a conscious and personal choice
            // that does not affect runtime
            if (playerInput.isEmpty() && !canBeEmpty)
                continue;
            if (!promptConfirmation)
                return playerInput;

            System.out.println(GlobalConf.getStoryTextPrefix() + "Are you happy with " + playerInput + "? Y/n");
            String confirmString = scannerObj.nextLine();
            if (confirmString.equalsIgnoreCase("y"))
                return playerInput;
        }
    }

    public static int awaitChoiceInputFromOptions(String[] optionz) {
        ListIterator<String> listOptionz = Arrays.asList(optionz).listIterator();
        while (listOptionz.hasNext()) {
            System.out.println(GlobalConf.getPromtTextPrefix() +
                    "[" + (listOptionz.nextIndex() + 1) + "] " + listOptionz.next());
        }
        return awaitChoiceInput(optionz.length);
    }
    public static int[] awaitChoiceInputWithIncrement(int highestOptionVal, String breakWord) throws Exception {
        // int[] options = genOptionsArr(highestOptionVal);
        String playerInput;
        int lastCharID;
        while (true) {
            playerInput = getRawPlayerInput().replaceAll(" ", "");
            if (playerInput.replaceAll("[\\[\\]]+", "").equalsIgnoreCase(breakWord))
                return new int[]{-1, -1};
            if (playerInput.length() < 2)
                continue;
            lastCharID = playerInput.length() - 1;
            char lastChar = playerInput.charAt(lastCharID);
            if (!(lastChar == '-' || lastChar == '+'))
                continue;
            try {
                int statChoicer = Integer.parseInt(playerInput.substring(0, lastCharID));
                if (statChoicer > highestOptionVal)
                    continue;
                int statVal = lastChar == '+' ? 1 : -1;
                return new int[]{statChoicer, statVal};
            } catch (NumberFormatException e) {
                GlobalConf.issueLog(e.getMessage(), GlobalConf.SEVERITY_LEVEL_WARNING, false);
            }
        }
    }

    public static <T> String genHealthBar(T character, int healthBarLen) {
        int curHealth;
        int maxHealth;
        if (healthBarLen < 0) {
            healthBarLen = HP_BAR_LEN;
        }
        double scale;
        if (!(character instanceof Foe)) {
            curHealth = PlayerClass.getPlayerStat("curHealth");
            maxHealth = PlayerClass.getPlayerStat("maxHealth");
        } else {
            curHealth = ((Foe) character).getCurHealth();
            maxHealth = ((Foe) character).getMaxHealth();
        }
        StringBuilder healthBar = new StringBuilder("[");
        scale = ((double) healthBarLen / maxHealth);
        scale *= curHealth;
        for (int i = 0; i < healthBarLen; i++) {
            healthBar.append(i < scale ? "=" : " ");
        }
        healthBar.append("]");
        return healthBar.toString();
    }
    public static void displayCombatants(Foe combatant) {
        System.out.println(combatant.getName() + " (" + PlayerKeywordz.getLevelName() + " " + combatant.getLevel() + ")");
        System.out.println(genHealthBar(combatant, -1));
        System.out.println(combatant.getCurHealth() + " / " + combatant.getMaxHealth() + " " +
                PlayerKeywordz.getHealthName() + "\t\t" + PlayerKeywordz.getStrAbbr() + ": " +
                combatant.getStrength());
        System.out.println(combatant.getcurMana() + " / " + combatant.getMaxMana() + " " +
                PlayerKeywordz.getManaName() + "\t\t" + PlayerKeywordz.getDexAbbr() + ": " +
                combatant.getDexterity());
        System.out.println("Armour: " + combatant.getArmour() + "\t\t" +
                PlayerKeywordz.getIntAbbr() + ": " + combatant.getIntellect());
        System.out.println("\n>>>>>>>>>> VS <<<<<<<<<<\n");
        System.out.println(PlayerClass.getPlayerName() + " (" + PlayerKeywordz.getLevelName() + " " +
                PlayerClass.getPlayerStat("playerLevel") +
                ", " + PlayerClass.getPlayerStat("curXP") + " / " + PlayerClass.getPlayerStat("neededXP") +
                ")");
        System.out.println(genHealthBar(null, -1));
        System.out.println(PlayerClass.getPlayerStat("curHealth") + " / " +
                PlayerClass.getPlayerStat("maxHealth") + " " + PlayerKeywordz.getHealthName() + "\t\t" +
                PlayerKeywordz.getStrAbbr() + ": " +
                PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName()));
        System.out.println(PlayerClass.getPlayerStat("curMana") + " / " +
                PlayerClass.getPlayerStat("maxMana") + " " + PlayerKeywordz.getManaName() + "\t\t" +
                PlayerKeywordz.getDexAbbr() + ": " +
                PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()));
        System.out.println("Armour: " + PlayerClass.getPlayerStat("Armour") + "\t\t" +
                PlayerKeywordz.getIntAbbr() + ": " +
                PlayerClass.getPlayerStat(PlayerKeywordz.getIntellectName()));
    }

    public static int[] genOptionsArr(int highestOptionVal) {
        int[] options = new int[highestOptionVal];
        for (int i = 0; i < highestOptionVal; i++)
            options[i] = i + 1;
        return options;
    }

    public static String getRawPlayerInput() {
        String playerInput = scannerObj.nextLine();
        return removeWhiteSpace(playerInput);
    }

    public static String removeWhiteSpace(String input) {
        return input.trim();
    }
}
