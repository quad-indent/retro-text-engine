import player.PlayerClass;
import storyBits.StoryBlockMaster;
import storyBits.StoryDisplayer;

import inventory.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Inventory.initInventory(null);
        InventoryCache.parseFile(null);
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

        Inventory.displaySideBySide(stringz, 5, 5);
        StoryBlockMaster bard = new StoryBlockMaster(null);
        int playerStoryPage = PlayerClass.initPlayer(null);
        StoryDisplayer.storyLoop(bard.getStoryObj(), playerStoryPage);
    }
}