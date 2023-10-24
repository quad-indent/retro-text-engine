import java.util.*;

interface inputVerificator {
    Boolean checkInput(int i, int[] x);
}

public class StoryDisplayer {
    private static final Scanner scannerObj = new Scanner(System.in);
    public static void storyLoop(ArrayList<StoryBlock> storyObj, int beginAt) {
        int curIndex = beginAt;
        StoryBlock curObj = storyObj.get(curIndex);
        int nextChoice = -1;
        int rawChoicePicked = -1;
        while (curIndex < storyObj.size()) {
            System.out.println(">> " + curObj.getPromptText());
            printChoiceOptions(curObj.getChoices());
            rawChoicePicked = awaitChoiceInput(getChoiceOptions(curObj.getChoices()).length);
            nextChoice = curObj.getChoiceDestinationAtID(rawChoicePicked);
            if (nextChoice == -1)
                return;
            // curObj = curObj.getChoiceDestinationAtID(nextChoice);
            curIndex = nextChoice;
            curObj = storyObj.get(curIndex);
            if (curObj.getStatVals().get(rawChoicePicked) == 0) {
                continue;
            }
            PlayerClass.incrementPlayerStat(curObj.getRelevantStat().get(rawChoicePicked),
                    curObj.getStatVals().get(rawChoicePicked));
            if (PlayerClass.checkForDeath(true)) {
                return;
            }
        }
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
            int parsedInput = -1;
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
        int[] options = genOptionsArr(highestOptionVal);
        String playerInput = "";
        int lastCharID = -1;
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
