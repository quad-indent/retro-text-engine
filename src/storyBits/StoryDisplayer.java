package storyBits;

import combat.*;
import foeTypes.*;
import inventory.Inventory;
import player.PlayerClass;

import java.util.*;
import audio.ClipPlayer;

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

    public static void storyLoop(ArrayList<StoryBlock> storyObj, int beginAt) {
        setCurIndex(beginAt);
        StoryBlock curObj = storyObj.get(getCurIndex());
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
        while (getCurIndex() < storyObj.size()) {
            System.out.println(">> " + curObj.getPromptText());
            ClipPlayer.playTune(curObj.getTuneToPlay());
            List<String> currentChoicez = curObj.getChoices();
            printChoiceOptions(currentChoicez, !Inventory.isMalformedInv(),
                    !Inventory.isMalformedEq());
            pureChoiceLen = getChoiceOptions(currentChoicez, false, false).length;

            rawChoicePicked = awaitChoiceInput(
                    pureChoiceLen + choiceAdditions); // +2 since allowing inv and eq view
            if (rawChoicePicked >= pureChoiceLen) {
                if (rawChoicePicked == pureChoiceLen)
                    Inventory.displayInventoryOrEq(Inventory.eqCats.INVENTORY, false);
                else
                    Inventory.displayEquipment();
                continue;
            }
            nextChoice = curObj.getChoiceDestinationAtChoiceStr(currentChoicez.get(rawChoicePicked));
            if (nextChoice == -1)
                return;
            setCurIndex(nextChoice);

            if (curObj.getCombatantAtChoice(currentChoicez.get(rawChoicePicked)) != null) {
                Foe currentFoe = getFoe(curObj, rawChoicePicked);
                try {
                    PlayerClass.incrementXP(CombatUtils.combatLoop(currentFoe));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (!curObj.isStatCheckAtChoiceStr(currentChoicez.get(rawChoicePicked))) {
                String relevantStat = curObj.getRelevantStat().get(rawChoicePicked);
                if (relevantStat.equals("curXP")) {
                    PlayerClass.incrementXP(curObj.getStatVal().get(rawChoicePicked));
                } else {
                    PlayerClass.incrementPlayerStat(relevantStat,
                            curObj.getStatVal().get(rawChoicePicked));
                }
            }
            curObj = storyObj.get(getCurIndex());
            PlayerClass.saveCharacter(getCurIndex());
            if (PlayerClass.checkForDeath(true)) {
                return;
            }
        }
    }

    private static Foe getFoe(StoryBlock curObj, int rawChoicePicked) {
        String[] combatantInfo = curObj.getCombatantInfo().get(rawChoicePicked);
        Foe currentFoe;
        switch (combatantInfo[0].toLowerCase()) {
            case "goblin":
                currentFoe = new Goblin(combatantInfo[1], true, 0, 0,
                        0, 0, 0, 0, 0,
                        0, 0, 0, 0);
                break;
            default:
                currentFoe = new Foe("Errornimus", 1, 0,
                        0, 0, 0, 0,
                        0, 0, 0, 0);
        }
        return currentFoe;
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
            System.out.println(">> [" + choiceLen++ + "] " + choice);
        }
        if (offerInventory)
            System.out.println(">> [" + choiceLen++ + "] view inventory");
        if (offerEquipment)
            System.out.println(">> [" + choiceLen + "] view equipment");
    }

    public static int awaitChoiceInput(int highestOptionVal) {
        int[] options = genOptionsArr(highestOptionVal);
        inputVerificator checker = (input, arr) -> {
            return Arrays.stream(arr).anyMatch(e -> e == input);
        };
        while (true) {
            String playerInput = getRawPlayerInput();
//            if (highestOptionVal == -1)
//                return -1;
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

            System.out.println(">> Are you happy with " + playerInput + "? Y/n");
            String confirmString = scannerObj.nextLine();
            if (confirmString.equalsIgnoreCase("y"))
                return playerInput;
        }
    }

    public static int awaitChoiceInputFromOptions(String[] optionz) {
        ListIterator<String> listOptionz = Arrays.asList(optionz).listIterator();
        while (listOptionz.hasNext()) {
            System.out.println(">> [" + (listOptionz.nextIndex() + 1) + "] " + listOptionz.next());
        }
        return awaitChoiceInput(optionz.length);
    }
    public static int[] awaitChoiceInputWithIncrement(int highestOptionVal, String breakWord) {
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
                continue;
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
        System.out.println(combatant.getName() + " (Level " + combatant.getLevel() + ")");
        System.out.println(genHealthBar(combatant, -1));
        System.out.println(combatant.getCurHealth() + " / " + combatant.getMaxHealth() + " HP\t\tSTR: " +
                combatant.getStrength());
        System.out.println(combatant.getcurMana() + " / " + combatant.getMaxMana() + " Mana\t\tDEX: " +
                combatant.getDexterity());
        System.out.println("Armour: " + combatant.getArmour() + "\t\tINT: " + combatant.getIntellect());
        System.out.println("\n>>>>>>>>>> VS <<<<<<<<<<\n");
        System.out.println(PlayerClass.getPlayerName() + " (Level " + PlayerClass.getPlayerStat("playerLevel") +
                ", " + PlayerClass.getPlayerStat("curXP") + " / " + PlayerClass.getPlayerStat("neededXP") +
                ")");
        System.out.println(genHealthBar(null, -1));
        System.out.println(PlayerClass.getPlayerStat("curHealth") + " / " +
                PlayerClass.getPlayerStat("maxHealth") + " HP\t\tSTR: " +
                PlayerClass.getPlayerStat("Strength"));
        System.out.println(PlayerClass.getPlayerStat("curMana") + " / " +
                PlayerClass.getPlayerStat("maxMana") + " Mana\t\tDEX: " +
                PlayerClass.getPlayerStat("Dexterity"));
        System.out.println("Armour: " + PlayerClass.getPlayerStat("Armour") + "\t\tINT: " +
                PlayerClass.getPlayerStat("Intellect"));
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
