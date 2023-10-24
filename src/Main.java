// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
// import StoryBlockHandler;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
//        System.out.print("Hello and welcome!");
//        String a = "cock";
//        String b = "bollocks";
//        System.out.println(Collections.disjoint(a.toCharArray().asl, b.toCharArray()));
//        return;

        StoryBlockMaster bard = new StoryBlockMaster(null);
        // bard.showMeSomeStories();

        // StoryDisplayer storyDisp = new StoryDisplayer();

        PlayerClass.initPlayer(null); // character = new PlayerClass(null);

        StoryDisplayer.storyLoop(bard.getStoryObj(), 0);
//        List<String> myStory = StoryParser.parseFile(null);
//        for(String storyBit : myStory)
//        {
//            System.out.println(storyBit);
//        }
//
//        //StoryBlock cuntie = new StoryBlock();
//        // cuntie.returnShit();
//
//        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
//        for (int i = 1; i <= 5; i++) {
//
//            // Press Shift+F9 to start debugging your code. We have set one breakpoint
//            // for you, but you can always add more by pressing Ctrl+F8.
//            System.out.println("FUCKING i = " + i);
//        }
    }
}