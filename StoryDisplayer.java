import java.awt.desktop.PreferencesEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

interface inputVerificator
{
    Boolean checkInput(int i, int[] x);
}
public class StoryDisplayer {
    private final Scanner scannerObj = new Scanner(System.in);
    public StoryDisplayer()
    {

    }
    public void storyLoop(ArrayList<StoryBlock> storyObj, int beginAt)
    {
        int curIndex = beginAt;
        StoryBlock curObj = storyObj.get(curIndex);
        int nextChoice = -1;
        while (curIndex < storyObj.size())
        {
            curObj = storyObj.get(curIndex);
            System.out.println(">> " + curObj.getPromptText());
            printChoiceOptions(curObj.getChoices());
            nextChoice = curObj.getChoiceDestinationAtID(awaitChoiceInput(getChoiceOptions(curObj.getChoices()).length));
            if (nextChoice == -1)
                return;
            // curObj = curObj.getChoiceDestinationAtID(nextChoice);
            curIndex = nextChoice;
        }
    }
    public int[] getChoiceOptions(List<String> choicez)
    {
        int choiceLen = choicez.size();
        int[] options = new int[choiceLen];
        for (int i=0; i<choiceLen; i++)
            options[i] = i+1;
        return options;
    }
    public void printChoiceOptions(List<String> choicez)
    {
        int choiceLen = 1;
        for (String choice: choicez)
        {
            System.out.println(">> [" + choiceLen++ + "] " + choice);
        }
    }
    public int awaitChoiceInput(int highestOptionVal)
    {
        int[] options = genOptionsArr(highestOptionVal);
        inputVerificator checker = (input, arr) -> {
            return Arrays.stream(arr).anyMatch(e -> e == input);
        };
        while (true)
        {
            String playerInput = getRawPlayerInput();
//            if (highestOptionVal == -1)
//                return -1;
            int parsedInput = -1;
            try {
                parsedInput = Integer.parseInt(playerInput);
            }
            catch (NumberFormatException e) {
                continue;
            }
            if (checker.checkInput(parsedInput, options))
                return Integer.parseInt(playerInput) - 1;
        }
    }
    public String awaitChoiceInput(boolean canBeEmpty, boolean promptConfirmation)
    {
        while (true)
        {
            String playerInput = getRawPlayerInput();

            // Here, else-ifs are redundant as each check either continues the loop or returns
            // My skipping of "else" is a conscious and personal choice
            // that does not affect runtime
            if (playerInput.isEmpty()  && !canBeEmpty)
                continue;
            if (!promptConfirmation)
                return playerInput;

            System.out.println(">> Are you happy with " + playerInput + "? Y/n");
            String confirmString = scannerObj.nextLine();
            if (confirmString.equalsIgnoreCase("y"))
                return playerInput;
        }
    }
    public int[] awaitChoiceInputWithIncrement(int highestOptionVal, String breakWord)
    {
        int[] options = genOptionsArr(highestOptionVal);
        String playerInput = "";
        int lastCharID = -1;
        while (true)
        {
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
                int statVal = lastChar == '+' ? 1 : -1;
                return new int[]{statChoicer, statVal};
            }
            catch (NumberFormatException e) {
                continue;
            }
        }
    }
    public int[] genOptionsArr(int highestOptionVal)
    {
        int[] options = new int[highestOptionVal];
        for (int i=0; i<highestOptionVal; i++)
            options[i] = i+1;
        return options;
    }
    public String getRawPlayerInput()
    {
        String playerInput = scannerObj.nextLine();
        return removeWhiteSpace(playerInput);
    }
    public String removeWhiteSpace(String input)
    {
        return input.trim();
    }
}
