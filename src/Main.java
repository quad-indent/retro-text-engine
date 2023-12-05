import guiPackage.GUIClass;
import player.PlayerClass;
import player.PlayerKeywordz;
import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryBlockMaster;
import storyBits.StoryDisplayer;

import inventory.*;

import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        GUIClass myGUI = new GUIClass();
        GlobalConf.initGlobalConf(
                FileParser.prettifyParsedPlayerSheetConfig(Objects.requireNonNull(
                        FileParser.parseFile(FileParser.joinConfigFolder("PlayerSheetConfig.txt"),
                                "[", false))));
        if (!GlobalConf.isMinimalConfig()) {
            Inventory.initInventory(null);
            InventoryCache.processItemCache(null);
        }
        StoryBlockMaster bard = new StoryBlockMaster(null);
        PlayerKeywordz.initAllNamez(null);
        int playerStoryPage = PlayerClass.initPlayer(null);
        StoryBlockMaster.massPopEphemeralz(StoryBlockMaster.getPoppedEphemeralz(), bard.getStoryObj());
        StoryDisplayer.storyLoop(bard.getStoryObj(), playerStoryPage, false);
    }
}