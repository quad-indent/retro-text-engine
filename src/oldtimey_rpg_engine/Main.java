package oldtimey_rpg_engine;

import oldtimey_rpg_engine.player.PlayerClass;
import oldtimey_rpg_engine.storyBits.StoryBlockMaster;
import oldtimey_rpg_engine.storyBits.StoryDisplayer;

public class Main {
    public static void main(String[] args) {
        StoryBlockMaster bard = new StoryBlockMaster(null);
        int playerStoryPage = PlayerClass.initPlayer(null);
        StoryDisplayer.storyLoop(bard.getStoryObj(), playerStoryPage);
    }
}