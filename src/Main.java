import combat.CombatUtils;
import combat.FoeParser;
import player.PlayerClass;
import player.PlayerKeywordz;
import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryBlockMaster;
import storyBits.StoryDisplayer;

import inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void oldDisplayTest() {
        // leaving this for...idk what reason
        List<List<String>> stringz = new ArrayList<>();
        List<String> tempie = new ArrayList<>();
        tempie.add("A warm gun");
        tempie.add("Deals a lot of damage!");
        tempie.add("And look how pretty it is");
        stringz.add(new ArrayList<>(tempie));
        tempie.clear();
        tempie.add("I don't even know what");
        tempie.add("Is that a bow?");
        stringz.add(new ArrayList<>(tempie));
        tempie.clear();
        tempie.add("A knife");
        tempie.add("2-3 damage bonus");
        tempie.add("+1 Dexterity");
        tempie.add("Father's");
        stringz.add(new ArrayList<>(tempie));
        tempie.clear();
        Inventory.displaySideBySide(stringz, 5, 5, true);
    }

    public static void main(String[] args) throws Exception {
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
        StoryDisplayer.storyLoop(bard.getStoryObj(), playerStoryPage, false);
    }
}

//Only one thing to be done
//Bestow items upon player in the story ting