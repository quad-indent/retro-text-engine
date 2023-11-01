package inventory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import player.PlayerClass;
import storyBits.StoryDisplayer;

public class Inventory {
    private static final int MAX_CONSOLE_CHARS = 140;
    private static int armourSlots = 5;
    private static int trinketSlots = 2;
    private static int neckSlots = 1;
    private static int weaponSlots = 2;
    private static Map<String, ArmourItem> equippedArmour = new LinkedHashMap<>();
    private static WeaponItem[] equippedWeapons = new WeaponItem[weaponSlots];
    private static Item[] equippedTrinkets = new Item[trinketSlots];
    private static Item[] equppedNecks = new Item[neckSlots];
    private static int currentGold = 0;
    private static List<Item> inventorySpace = new ArrayList<>();

    public static List<Item> getInventorySpace() {
        return inventorySpace;
    }

    public static void setInventorySpace(List<Item> tempInventorySpace) {
        inventorySpace = tempInventorySpace;
    }

    public static Item getInventoryItemByID(int ID) {
        for (Item it : getInventorySpace()) {
            if (it.getItemID() == ID) {
                return it;
            }
        }
        return null;
    }

    public static int removeInventoryItem(int itemID, boolean removeAllInstances) {
        List<Item> inventorySpaceCopy = new ArrayList<>(getInventorySpace());
        for (Item it : inventorySpaceCopy) {
            if (it.getItemID() == itemID) {
                getInventorySpace().remove(it);
                if (!removeAllInstances) {
                    return 0; // success
                }
            }
        }
        return 1; // no items removed
    }

    public static int removeInventoryItem(Item itemToRemove) {
        return getInventorySpace().remove(itemToRemove) ? 0 : 1;
    }

    public static int getMaxColWidth(int columnsAccepted, int marginSpace) {
        return (MAX_CONSOLE_CHARS - (columnsAccepted - 1) * marginSpace) / columnsAccepted;
    }

    public static int getArmourSlots() {
        return armourSlots;
    }

    public static void setArmourSlots(int armourSlots) {
        Inventory.armourSlots = armourSlots;
    }

    public static int getTrinketSlots() {
        return trinketSlots;
    }

    public static void setTrinketSlots(int trinketSlots) {
        Inventory.trinketSlots = trinketSlots;
    }

    public static int getNeckSlots() {
        return neckSlots;
    }

    public static void setNeckSlots(int neckSlots) {
        Inventory.neckSlots = neckSlots;
    }

    public static int getWeaponSlots() {
        return weaponSlots;
    }

    public static void setWeaponSlots(int tempWeaponSlots) {
        weaponSlots = tempWeaponSlots;
    }

    public static Map<String, ArmourItem> getEquippedArmour() {
        return equippedArmour;
    }

    public static void setEquippedArmour(Map<String, ArmourItem> tempEquippedArmour) {
        equippedArmour = tempEquippedArmour;
    }

    public static WeaponItem[] getEquippedWeapons() {
        return equippedWeapons;
    }

    public static void setEquippedWeapons(WeaponItem[] tempEquippedWeapons) {
        equippedWeapons = tempEquippedWeapons;
    }

    public static Item[] getEquippedTrinkets() {
        return equippedTrinkets;
    }

    public static void setEquippedTrinkets(Item[] tempEquippedTrinkets) {
        equippedTrinkets = tempEquippedTrinkets;
    }

    public static Item[] getEquppedNecks() {
        return equppedNecks;
    }

    public static void setEquppedNecks(Item[] tempEquppedNecks) {
        equppedNecks = tempEquppedNecks;
    }

    public static int getCurrentGold() {
        return currentGold;
    }

    public static void setCurrentGold(int tempCurrentGold) {
        currentGold = tempCurrentGold;
    }
    public static void initInventory(String inventoryConfigFile) {
        if (inventoryConfigFile == null) {
            inventoryConfigFile = "inventoryConfig.txt";
        }
        try {
            FileReader confReader = new FileReader(inventoryConfigFile);
            Scanner confScanner = new Scanner(confReader);
            List<String> linez = new ArrayList<>();
            int tempArmourSlotz = 0;
            while (confScanner.hasNextLine())
                linez.add(confScanner.nextLine());
            assert linez.size() >= 3;
            assert linez.get(linez.size()-1).contains("=");
            int equalSignLinez = 0;
            for (String line : linez) {
                if (line.contains("="))
                    equalSignLinez++;
            }
            assert equalSignLinez == 3;
            for (String line : linez) {
                if (!line.contains("=")) {
                    tempArmourSlotz++;
                    getEquippedArmour().put(line, null);
                } else break;
            }

            Purifier parsey = (a) -> Integer.parseInt(StoryDisplayer.removeWhiteSpace(a.split("=")
                    [a.split("=").length - 1]));
            // splits string in twain and then removes whitespace. Casts last element to int and returns that
            setTrinketSlots(parsey.purify(linez.get(linez.size()-3)));
            setNeckSlots(parsey.purify(linez.get(linez.size()-2)));
            setWeaponSlots(parsey.purify(linez.get(linez.size()-1)));
            setArmourSlots(tempArmourSlotz);
            setEquippedTrinkets(new Item[getTrinketSlots()]);
            setEquippedWeapons(new WeaponItem[getWeaponSlots()]);
            setEquppedNecks(new Item[getNeckSlots()]);
        } catch (FileNotFoundException e) {
            System.out.println("Error! " + inventoryConfigFile + " not found!");
            return;
        } catch (AssertionError e) {
            System.out.println("Error! " + inventoryConfigFile + " misconfigured!\n" +
                    "Needs to include the following (at the end, each as a new line):\n" +
                    "trinketSLots = VALUE\nneckSlots = VALUE\nweaponSlots = VALUE");
        }
    }

    interface Purifier {
        int purify(String a);
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
                for (List<String> strings : updatedList) {
                    // every column element here
                    if (strings.size() <= entryListIndexer) {
                        lineStr.append(padString(" ", columnzToUse, marginSpace));
                        continue;
                    }
                    lineStr.append(strings.get(entryListIndexer));
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
    public static void displayInventory() {
        System.out.println(">> " + PlayerClass.getPlayerName());
        System.out.println(">> Level " + PlayerClass.getPlayerStat("curLevel") + " (" + PlayerClass.getPlayerStat("xp") +
                " / " + PlayerClass.getPlayerStat("nextXP") + " XP)");

    }

    public static boolean hasEquipmentInSlot(String equipmentType, int slotID, String slotName) {
        return switch (equipmentType.toLowerCase()) {
            case "weapon", "wep" -> {
                if (slotID >= getWeaponSlots())
                    yield true;
                yield getEquippedWeapons()[slotID] != null;
            }
            case "armour", "armor" -> {
                if (!getEquippedArmour().containsKey(slotName))
                    yield true;
                yield getEquippedArmour().get(slotName) != null;
            }
            case "trinket", "trinkets" -> {
                if (slotID >= getTrinketSlots())
                    yield true;
                yield getEquippedTrinkets()[slotID] != null;
            }
            case "neck", "necks" -> {
                if (slotID >= getNeckSlots())
                    yield true;
                yield getEquppedNecks()[slotID] != null;
            }
            default -> true;
        };
    }

    public static int getAvailableEquipmentID(String equipmentType, int ignoreThisID) {
        return switch (equipmentType.toLowerCase()) {
            case "weapon", "wep" -> {
                for (int i = 0; i < getWeaponSlots(); i++) {
                    if (i == ignoreThisID)
                        continue;
                    if (getEquippedWeapons()[i] == null) {
                        yield i;
                    }
                }
                yield -1;
            }
            case "trinket", "trinkets" -> {
                for (int i = 0; i < getTrinketSlots(); i++) {
                    if (getEquippedTrinkets()[i] == null)
                        yield i;
                }
                yield -1;
            }
            case "neck", "necks" -> {
                for (int i = 0; i < getNeckSlots(); i++) {
                    if (getEquppedNecks()[i] == null)
                        yield i;
                }
                yield -1;
            }
            default -> -1;
        };
    }

    public static int equipTrinketOrNeck(Item itemToEquip, int eqSlot) {
        if (!PlayerClass.playerPassesReq(itemToEquip.getStatRequirements())) {
            return 4; // reqs not passed
        }
        String itemType = itemToEquip.getItemType();
        Item[] itemUsed = itemType.equals("neck") ? getEquppedNecks() : getEquippedTrinkets();
        if (eqSlot == -1) {
            for (int i = 0; i < itemUsed.length; i++) {
                if (itemUsed[i] == null) {
                    eqSlot = i;
                    break;
                }
            }
            if (eqSlot == -1)
                return 2; // no slots available
        } else if (eqSlot >= itemUsed.length) {
            return 3; // no such slot
        } else if (itemUsed[eqSlot] != null) {
            return 2;
        }

        if (itemType.equals("neck"))
            getEquppedNecks()[eqSlot] = itemToEquip;
        else
            getEquippedTrinkets()[eqSlot] = itemToEquip;
        PlayerClass.incrementPlayerStat(itemToEquip.getStatBoons(), false);
        return 0;
    }
    public static int equipArmour(ArmourItem armourToEquip) {
        if (!PlayerClass.playerPassesReq(armourToEquip.getStatRequirements())) {
            return 4; // reqs not passed
        }
        if (!getEquippedArmour().containsKey(armourToEquip.getArmourSlot())) {
            return 3; // no such slot
        }
        if (getEquippedArmour().get(armourToEquip.getArmourSlot()) != null) {
            return 1; // armour already there
        }
        getEquippedArmour().put(armourToEquip.getArmourSlot(), armourToEquip);
        PlayerClass.incrementPlayerStat(armourToEquip.getStatBoons(), false);
        return 0;
    }
    public static int equipWeapon(WeaponItem weaponToEquip, int eqSlot) {
        if (!PlayerClass.playerPassesReq(weaponToEquip.getStatRequirements())) {
            return 4; // reqs not passed
        }
        int foundEqSlot1 = -1;
        int foundEqSlot2 = -1;
        if (eqSlot != -1) {
            if (getEquippedWeapons()[eqSlot] != null)
                return 1; // weapon already equipped there
            getEquippedWeapons()[eqSlot] = weaponToEquip;
            return 0;
        }
        foundEqSlot1 = getAvailableEquipmentID("weapon", -1);
        if (foundEqSlot1 == -1)
            return 2; // no slots available
        if (!weaponToEquip.isIs1H()) {
            foundEqSlot2 = getAvailableEquipmentID("weapon", foundEqSlot1);
            if (foundEqSlot2 == -1)
                return 2; // no slots available again
            getEquippedWeapons()[foundEqSlot2] = weaponToEquip;
        }
        getEquippedWeapons()[foundEqSlot1] = weaponToEquip;
        PlayerClass.incrementPlayerStat(weaponToEquip.getStatBoons(), false);
        return 0; // success
    }
    public static Item unequipTrinketOrNeck(int slotIdx, boolean unequipTrinket) {
        Item[] itemUsed = unequipTrinket ? getEquippedTrinkets() : getEquppedNecks();
        if (slotIdx >= itemUsed.length) {
            return null;
        } else if (itemUsed[slotIdx] == null) {
            return null;
        }
        Item itemCopy = itemUsed[slotIdx];
        if (unequipTrinket)
            getEquippedTrinkets()[slotIdx] = null;
        else
            getEquppedNecks()[slotIdx] = null;
        PlayerClass.incrementPlayerStat(itemCopy.getStatBoons(), true);
        return itemCopy;
    }
    public static ArmourItem unequipArmour(String armourSlot) {
        if (!getEquippedArmour().containsKey(armourSlot)) {
            return null; // no such slot
        }
        if (getEquippedArmour().get(armourSlot) == null) {
            return null; // nothing to unequip
        }
        ArmourItem armourCopy = new ArmourItem(getEquippedArmour().get(armourSlot));
        getEquippedArmour().put(armourSlot, null);
        PlayerClass.incrementPlayerStat(armourCopy.getStatBoons(), true);
        return armourCopy;
    }
    public static WeaponItem unequipWeapon(int weaponIDToUnequip) {
        if (getWeaponSlots() <= weaponIDToUnequip || getEquippedWeapons()[weaponIDToUnequip] == null) {
            return null; // failure
        }
        WeaponItem weaponCopy = new WeaponItem(getEquippedWeapons()[weaponIDToUnequip]);
        if (!getEquippedWeapons()[weaponIDToUnequip].isIs1H()) {
            // if it's 2H, find the other slot and purge it
            for (int i = 0; i < getWeaponSlots(); i++) {
                if (i == weaponIDToUnequip)
                    continue;
                if (getEquippedWeapons()[i].equals(weaponCopy)) {
                    getEquippedWeapons()[i] = null;
                    break;
                }
            }
        }
        getEquippedWeapons()[weaponIDToUnequip] = null;
        PlayerClass.incrementPlayerStat(weaponCopy.getStatBoons(), true);
        return weaponCopy;
    }

    public static void initInventoryFromSave(Scanner playerScanner) {
        // this gets called once PlayerClass' parsing of character data is done
        // and picks where it left off to initialise inventory
        // idx 0 contains what's currently equipped
        // idx 1 what's in the inventory, the last value is the current gold amount
        String[] itemIDLinez = new String[2];
        try {
            for (int i = 0; i < 2; i++) {
                itemIDLinez[i] = playerScanner.nextLine();
            }
        } catch (NoSuchElementException e) {
            System.out.println("Inventory data not found! Aborting inventory loading. . .");
            return;
        }
        try {
            for (String curStrID : itemIDLinez[0].split(" ")) {
                int curID = Integer.parseInt(curStrID);
                List<String> curItemSpecs = InventoryCache.getItem(curID);
                switch (curItemSpecs.get(0).toLowerCase()) {
                    case "weapon":
                        equipWeapon(((WeaponItem) Item.smartItemInit(curID, curItemSpecs)), -1);
                        break;
                    case "armour", "armor":
                        equipArmour(((ArmourItem) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs))));
                        break;
                    case "trinket", "neck":
                        equipTrinketOrNeck(((Item) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs))), -1);
                        break;
                    default:
                        throw new Exception();
                }
            }
        } catch (Exception e) {
            System.out.println("Malformed equipment data in save file! Aborting . . .");
            return;
        }
        try {
            String[] idsToIterate = itemIDLinez[1].split(" ");
            for (int i = 0; i < idsToIterate.length; i++) {
                int curID = Integer.parseInt(idsToIterate[i]);
                if (i == idsToIterate.length - 1) {
                    setCurrentGold(curID);
                    break;
                }
                List<String> curItemSpecs = InventoryCache.getItem(curID);
                switch (curItemSpecs.get(0).toLowerCase()) {
                    case "weapon":
                        Inventory.getInventorySpace().add(((WeaponItem) Item.smartItemInit(curID, curItemSpecs)));
                        break;
                    case "armour", "armor":
                        Inventory.getInventorySpace()
                                .add(((ArmourItem) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs))));
                        break;
                    case "trinket", "neck", "junk", "quest":
                        Inventory.getInventorySpace()
                                .add((Item) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs)));
                        break;
                    default:
                        throw new Exception();
                }
            }
        } catch (Exception e) {
            System.out.println("Malformed inventory data in save file! Aborting . . .");
            return;
        }
    }
}

// todo: maybe run equipment req pass checks over whole equipment when unequipping
// (if a stat bestowed by the unequipped item fails to fulfill criteria for other items to be equipped)
// todo: include item/gold checks