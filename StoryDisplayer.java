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
            System.out.println(curObj.getPromptText());
            printChoiceOptions(curObj.getChoices());
            nextChoice = curObj.getChoiceDestinationAtID(awaitChoiceInput(getChoiceOptions(curObj.getChoices())));
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
    public int awaitChoiceInput(int[] options)
    {
        inputVerificator checker = (input, arr) -> {
            return Arrays.stream(arr).anyMatch(e -> e == input);
        };
        while (true)
        {
            String playerInput = scannerObj.nextLine();
            playerInput = removeWhiteSpace(playerInput);
            if (options == null)
                return -1;
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
    public String removeWhiteSpace(String input)
    {
        return input.replaceAll("\\s+", " ");
    }
}
