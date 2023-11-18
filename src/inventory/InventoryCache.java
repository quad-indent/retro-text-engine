package inventory;

import java.util.*;

import storyBits.FileParser;
import storyBits.GlobalConf;

public class InventoryCache {
    private static Map<Integer, List<String>> itemsCache = new LinkedHashMap<>();

    public static Map<Integer, List<String>> getItemsCache() {
        return itemsCache;
    }

    public static void setItemsCache(Map<Integer, List<String>> itemsCache) {
        InventoryCache.itemsCache = itemsCache;
    }

    public static List<String> getItem(int ID) {
        return getItemsCache().get(ID);
    }
    public static void addItemToCache(int itemID, List<String> itemStatz) {
        getItemsCache().put(itemID, itemStatz);
    }
    public static List<String> getItem(String itemName) {
        for (Map.Entry<Integer, List<String>> item : getItemsCache().entrySet()) {
            List<String> curVal = item.getValue();
            if (curVal.get(1).equals(itemName)) {
                // item name is at index 1 of list
                return curVal;
            }
        }
        return null;
    }
    public static void processItemCache(String fileName) throws Exception {
        if (fileName == null || fileName.isEmpty()) {
            fileName = FileParser.joinConfigFolder("itemTable.txt");
        }
        GlobalConf.issueLog("Attempting to parse " + fileName + ". . .", GlobalConf.SEVERITY_LEVEL_INFO);
        List<String> itemEntries = FileParser.parseFile(fileName, "[", false);
        List<List<String>> splitItemEntries = new ArrayList<>();
        assert itemEntries != null;
        for (String itemEntry : itemEntries) {
            ArrayList<String> splits = new ArrayList<String>(List.of(itemEntry.split("]")));
            splits.replaceAll(String::trim);
            splits.replaceAll(e -> e.replaceAll("\\[", ""));
            addItemToCache(Integer.parseInt(splits.remove(0)), splits);
        }
    }
}
