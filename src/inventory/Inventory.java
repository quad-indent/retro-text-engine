package inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import player.PlayerClass;

public class Inventory {
    private static final int MAX_CONSOLE_CHARS = 140;
    private static int armourSlots = 5;
    private static int trinketSlots = 2;
    private static int neckSlots = 1;
    private static int weaponSlots = 2;
    private Map<String, ArmourItem> equippedArmour = new LinkedHashMap<>();
    private WeaponItem[] equippedWeapons = new WeaponItem[weaponSlots];
    private Item[] equippedTrinkets = new Item[trinketSlots];
    private Item[] equppedNecks = new Item[neckSlots];
    private int currentGold = 0;

    public static int getMaxColWidth(int columnsAccepted, int marginSpace) {
        return (MAX_CONSOLE_CHARS - (columnsAccepted - 1) * marginSpace) / columnsAccepted;
    }
    public static void displaySideBySide(List<List<String>> textz, int columnzToUse, int marginSpace) {
        // The list of lists here is assumed to be UN-SPLIT texts
        // so for instance a single element will look like ["Revolver", "1-2 damage bonus (Dexterity scaling)",
        // "A formidable weapon crafted by an expert gunsmith", "+1 Dexterity"]
        StringBuilder lineStr = new StringBuilder();
        List<String> tempList = new ArrayList<>();
        tempList.add(" ");
        while (textz.size() % columnzToUse > 0) {
            textz.add(tempList);
        }

        List<List<String>> loopList = new ArrayList<>();
        List<List<String>> updatedList = new ArrayList<>();
        List<String> tempForUpdatedList = new ArrayList<>();
        int longestListInLoop = -1;
        while (true) {
            loopList.clear();
            for (int i = 0; i < columnzToUse; i++) {
                loopList.add(textz.remove(0));
            }
            longestListInLoop = -1;
            for (List<String> curEntry : loopList) {
                // iterates over current batch
                // takes the whole entry consisting of several strings
                for (String curLine : curEntry) {
                    // takes each line of above entry
                    for (String curSplit : stringWrapper(curLine, columnzToUse, marginSpace)) {
                        // takes each line of entry, wrapped
                        tempForUpdatedList.add(padString(curSplit, columnzToUse, marginSpace));
                        // appends temp list with padded string
                    }
                }
                if (tempForUpdatedList.size() > longestListInLoop)
                    longestListInLoop = tempForUpdatedList.size();
                updatedList.add(new ArrayList<>(tempForUpdatedList));
                tempForUpdatedList.clear();
            }
            // ending up with expertly-prepared updatedList, ready for crafting the display
            for (int entryListIndexer = 0; entryListIndexer < longestListInLoop; entryListIndexer++) {
                // every index of all current listings to be iterated here, i.e., rows for the console
                for (int entryBatchIndexer = 0; entryBatchIndexer < updatedList.size(); entryBatchIndexer++) {
                    // every column element here
                    if (updatedList.get(entryBatchIndexer).size() <= entryListIndexer) {
                        lineStr.append(padString(" ", columnzToUse, marginSpace));
                        continue;
                    }
                    lineStr.append(updatedList.get(entryBatchIndexer).get(entryListIndexer));
                }
                System.out.println(lineStr.toString());
                lineStr = new StringBuilder();
            }
            updatedList.clear();
            System.out.println();
            if (textz.isEmpty())
                return;
        }
    }
    public static String padString(String originalStr, int columnsAccepted, int marginSpace) {
        int maxWidth = getMaxColWidth(columnsAccepted, marginSpace);
        StringBuilder originalStrBuilder = new StringBuilder(originalStr);
        originalStrBuilder.append(" ".repeat(Math.max(0, maxWidth + marginSpace - originalStrBuilder.length())));
        return originalStrBuilder.toString();
    }
    public static List<String> stringWrapper(String strToSplit, int columnsAccepted, int marginSpace) {
        // If opting to display text in several columns,
        // split whatever's being displayed so that it's wrapped nicely
        // into its allotted column

        if (columnsAccepted <= 0)
            columnsAccepted = 2;
        else if (columnsAccepted == 1) {
            List<String> returnal = new ArrayList<>();
            returnal.add(strToSplit);
            return returnal;
        }
        if (marginSpace < 0)
            marginSpace = 5;
        int maxWidth = getMaxColWidth(columnsAccepted, marginSpace);
        // max char length of a single line as per provided limitationz

        List<String> returnalList = new ArrayList<>();
        StringBuilder loopStr = new StringBuilder();
        for (String word : strToSplit.split(" ")) {
            if (loopStr.length() + word.length() > maxWidth) {
                returnalList.add(loopStr.toString());
                loopStr = new StringBuilder(word);
                continue;
            }
            loopStr.append(word).append(" ");
        }
        if (returnalList.isEmpty() || !returnalList.get(returnalList.size()-1).contentEquals(loopStr)) {
            returnalList.add(loopStr.toString());
        }
        return returnalList;
    }
    public void displayInventory() {
        System.out.println(">> " + PlayerClass.getPlayerName());
        System.out.println(">> Level " + PlayerClass.getPlayerStat("curLevel") + " (" + PlayerClass.getPlayerStat("xp") +
                " / " + PlayerClass.getPlayerStat("nextXP") + " XP)");

    }
}
