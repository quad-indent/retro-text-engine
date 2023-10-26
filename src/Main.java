import player.PlayerClass;
import storyBits.StoryBlockMaster;
import storyBits.StoryDisplayer;

public class Main {
    public static void main(String[] args) {
        StoryBlockMaster bard = new StoryBlockMaster(null);
        int playerStoryPage = PlayerClass.initPlayer(null);
        StoryDisplayer.storyLoop(bard.getStoryObj(), playerStoryPage);
    }
}