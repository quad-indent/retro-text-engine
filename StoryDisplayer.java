import java.util.Arrays;
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
    public int awaitChoiceInput(int[] options)
    {
        inputVerificator checker = (input, arr) -> {
            return Arrays.stream(arr).anyMatch(e -> e == input);
        };
        while (true)
        {
            String playerInput = scannerObj.nextLine();
            playerInput = removeWhiteSpace(playerInput);
            int parsedInput = -1;
            try {
                parsedInput = Integer.parseInt(playerInput);
            }
            catch (NumberFormatException e) {
                continue;
            }
            if (checker.checkInput(parsedInput, options))
                return Integer.parseInt(playerInput);
        }
    }

    public String removeWhiteSpace(String input)
    {
        return input.replaceAll("\\s+", " ");
    }

}
