import java.util.*;

interface inputVerificator {
    Boolean checkInput(int i, int[] x);
}

public class StoryDisplayer {
    private static final Scanner scannerObj = new Scanner(System.in);
    private static final int HP_BAR_LEN = 16;
    public static void storyLoop(ArrayList<StoryBlock> storyObj, int beginAt) {
        int curIndex = beginAt;
        StoryBlock curObj = storyObj.get(curIndex);
        int nextChoice;
        int rawChoicePicked;
        while (curIndex < storyObj.size()) {
            System.out.println(">> " + curObj.getPromptText());
            printChoiceOptions(curObj.getChoices());
            rawChoicePicked = awaitChoiceInput(getChoiceOptions(curObj.getChoices()).length);
            nextChoice = curObj.getChoiceDestinationAtID(rawChoicePicked);
            if (nextChoice == -1)
                return;
            curIndex = nextChoice;

            if (curObj.getCombatantInfo().get(rawChoicePicked) != null) {
                Foe currentFoe = getFoe(curObj, rawChoicePicked);
                PlayerClass.incrementXP(CombatUtils.combatLoop(currentFoe));
            }
            if (!curObj.isStatCheckAtChoiceID(rawChoicePicked)) {
                PlayerClass.incrementPlayerStat(curObj.getRelevantStat().get(rawChoicePicked),
                        curObj.getStatVal().get(rawChoicePicked));
            }
            curObj = storyObj.get(curIndex);
            PlayerClass.saveCharacter(curIndex);
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

    public static int[] getChoiceOptions(List<String> choicez) {
        int choiceLen = choicez.size();
        int[] options = new int[choiceLen];
        for (int i = 0; i < choiceLen; i++)
            options[i] = i + 1;
        return options;
    }

    public static void printChoiceOptions(List<String> choicez) {
        int choiceLen = 1;
        for (String choice : choicez) {
            System.out.println(">> [" + choiceLen++ + "] " + choice);
        }
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

    public static <T> String genHealthBar(T character) {
        int curHealth;
        int maxHealth;
        double scale;
        if (!(character instanceof Foe)) {
            curHealth = PlayerClass.getPlayerStat("curHealth");
            maxHealth = PlayerClass.getPlayerStat("maxHealth");
        } else {
            curHealth = ((Foe) character).getCurHealth();
            maxHealth = ((Foe) character).getMaxHealth();
        }
        StringBuilder healthBar = new StringBuilder("[");
        scale = ((double) HP_BAR_LEN / maxHealth);
        scale *= curHealth;
        for (int i = 0; i < HP_BAR_LEN; i++) {
            healthBar.append(i < scale ? "=" : " ");
        }
        healthBar.append("]");
        return healthBar.toString();
    }
    public static void displayCombatants(Foe combatant) {
        System.out.println(combatant.getName() + " (Level " + combatant.getLevel() + ")");
        System.out.println(genHealthBar(combatant));
        System.out.println(combatant.getCurHealth() + " / " + combatant.getMaxHealth() + " HP\t\tSTR: " +
                combatant.getStrength());
        System.out.println(combatant.getcurMana() + " / " + combatant.getMaxMana() + " Mana\t\tDEX: " +
                combatant.getDexterity());
        System.out.println("Armour: " + combatant.getArmour() + "\t\tINT: " + combatant.getIntellect());
        System.out.println("\n>>>>>>>>>> VS <<<<<<<<<<\n");
        System.out.println(PlayerClass.getPlayerName() + " (Level " + PlayerClass.getPlayerStat("playerLevel") +
                ", " + PlayerClass.getPlayerStat("curXP") + " / " + PlayerClass.getPlayerStat("neededXP") +
                ")");
        System.out.println(genHealthBar(null));
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
