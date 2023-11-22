package inventory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import combat.CombatUtils;
import player.PlayerClass;
import player.PlayerKeywordz;
import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryDisplayer;

public class Inventory {
    private static final int MAX_CONSOLE_CHARS = 140;
    private static boolean malformedInv = true;
    private static boolean malformedEq = true;
    private static int armourSlots = 5;
    private static int trinketSlots = 2;
    private static int neckSlots = 1;
    private static int weaponSlots = 2;
    private static Map<String, ArmourItem> equippedArmour = new LinkedHashMap<>();
    private static WeaponItem[] equippedWeapons = new WeaponItem[weaponSlots];
    private static Item[] equippedTrinkets = new Item[trinketSlots];
    private static Item[] equippedNecks = new Item[neckSlots];
    private static int currentGold = 0;
    private static List<Item> inventorySpace = new ArrayList<>();

    public enum eqCats {
        INVENTORY,
        WEAPONZ,
        ARMOUR,
        TRINKETZ,
        NECKZ
    }

    public static eqCats strToEqCat(String tempCat) {
        return switch (tempCat.toLowerCase()) {
            case "armour", "armor":
                yield eqCats.ARMOUR;
            case "weapon", "weapons", "weaponz", "wep", "wepz", "weps":
                yield eqCats.WEAPONZ;
            case "trinket", "trinketz", "trinkets":
                yield eqCats.TRINKETZ;
            case "neck", "necklace", "necks", "neckz", "necklaces", "necklacez":
                yield eqCats.NECKZ;
            default:
                yield eqCats.INVENTORY;
        };
    }
    public static boolean isMalformedInv() {
        return malformedInv;
    }

    public static void setMalformedInv(boolean malformedInv) {
        Inventory.malformedInv = malformedInv;
    }

    public static boolean isMalformedEq() {
        return malformedEq;
    }

    public static void setMalformedEq(boolean malformedEq) {
        Inventory.malformedEq = malformedEq;
    }

    public static List<Item> getInventorySpace() {
        return inventorySpace;
    }

    public static void setInventorySpace(List<Item> tempInventorySpace) {
        inventorySpace = tempInventorySpace;
    }

    public static void addItemToInventory(Item itemToAdd) {
        getInventorySpace().add(itemToAdd);
    }
    public static Item getInventoryItemByItemID(int ID) {
        for (Item it : getInventorySpace()) {
            if (it.getItemID() == ID) {
                return it;
            }
        }
        return null;
    }

    public static Item removeInventoryItem(int itemID, boolean removeAllInstances) {
        List<Item> inventorySpaceCopy = new ArrayList<>(getInventorySpace());
        Item returnal;
        List<Item> filteredInv = inventorySpaceCopy.stream().filter(it -> it.getItemID() == itemID).toList();
        if (filteredInv.isEmpty()) {
            return null;
        }
        returnal = filteredInv.get(0);
        if (removeAllInstances) {
            getInventorySpace().removeIf(i -> i.getItemID() == itemID);
        } else {
            getInventorySpace().remove(filteredInv.get(0));
        }
        return returnal;
    }

    public static int removeInventoryItem(Item itemToRemove) {
        return getInventorySpace().remove(itemToRemove) ? 0 : 1;
    }

    public static Item removeInventoryItem(int invIdxOfInterest) {
            return getInventorySpace().remove(invIdxOfInterest);
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

    public static Item[] getEquippedNecks() {
        return equippedNecks;
    }

    public static void setEquippedNecks(Item[] tempEquippedNecks) {
        equippedNecks = tempEquippedNecks;
    }

    public static int getCurrentGold() {
        return currentGold;
    }

    public static void setCurrentGold(int tempCurrentGold) {
        currentGold = tempCurrentGold;
    }
    public static void initInventory(String inventoryConfigFile) throws Exception {
        if (inventoryConfigFile == null) {
            inventoryConfigFile = FileParser.joinConfigFolder("inventoryConfig.txt");
        }
        try {
            GlobalConf.issueLog("Attempting to parse inventory and equipment config at "
                            + inventoryConfigFile + ". . .",
                    GlobalConf.SEVERITY_LEVEL_INFO);
            FileReader confReader = new FileReader(inventoryConfigFile);
            Scanner confScanner = new Scanner(confReader);
            List<String> linez = new ArrayList<>();
            int tempArmourSlotz = 0;
            while (confScanner.hasNextLine())
                linez.add(confScanner.nextLine());
            confScanner.close();
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
            setEquippedNecks(new Item[getNeckSlots()]);
        } catch (FileNotFoundException e) {
            GlobalConf.issueLog("Error! " + inventoryConfigFile + " not found!", GlobalConf.SEVERITY_LEVEL_ERROR,
                    true);
        } catch (AssertionError e) {
            GlobalConf.issueLog("Error! " + inventoryConfigFile + " misconfigured!\n" +
                            "Needs to include the following (at the end, each as a new line):\n" +
                            "trinketSLots = VALUE\nneckSlots = VALUE\nweaponSlots = VALUE",
                    GlobalConf.SEVERITY_LEVEL_ERROR,
                    true);
        }
    }

    interface Purifier {
        int purify(String a);
    }

    public static void displaySideBySide(List<List<String>> textz, int columnzToUse, int marginSpace,
                                         boolean featureLilArrowz) {
        // The list of lists here is assumed to be UN-SPLIT texts
        // so for instance a single element will look like ["Revolver", "1-2 damage bonus (Dexterity scaling)",
        // "A formidable weapon crafted by an expert gunsmith", "+1 Dexterity"]
        if (textz.isEmpty()) {
            return;
        }
        StringBuilder lineStr = new StringBuilder();
        List<String> tempList = new ArrayList<>();
        tempList.add(" ");
        while (textz.size() % columnzToUse > 0) {
            textz.add(tempList);
        }

        List<List<String>> loopList = new ArrayList<>();
        List<List<String>> updatedList = new ArrayList<>();
        List<String> tempForUpdatedList = new ArrayList<>();
        int longestListInLoop;
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
                if (!featureLilArrowz)
                    System.out.println(lineStr);
                else
                    System.out.println(GlobalConf.getInvDispPrefix() + lineStr);
                lineStr = new StringBuilder();
            }
            if (!featureLilArrowz)
                System.out.println();
            else
                System.out.println(GlobalConf.getInvDispPrefix());
            updatedList.clear();
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
    public static List<String> simpleStringWrapper(String strToSplit, int maxWidth, boolean isStoryWrapping) {
        if (maxWidth == -1) {
            maxWidth = MAX_CONSOLE_CHARS - 3;
        }
        List<String> returnalList = new ArrayList<>();
        StringBuilder loopStr = new StringBuilder();
        loopStr.append((isStoryWrapping ? GlobalConf.getStoryTextPrefix() : GlobalConf.getInvDispPrefix()));
        for (String word : strToSplit.split(" ")) {
            if (loopStr.length() + word.length() > maxWidth) {
                returnalList.add(loopStr.toString());
                loopStr = new StringBuilder((isStoryWrapping ? GlobalConf.getStoryTextPrefix() :
                        GlobalConf.getInvDispPrefix()));
                loopStr.append(word).append(" ");
                continue;
            }
            loopStr.append(word).append(" ");
        }
        if (returnalList.isEmpty() || !returnalList.get(returnalList.size()-1).contentEquals(loopStr)) {
            returnalList.add(loopStr.toString());
        }
        return returnalList;
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

    public static boolean hasEquipmentInSlot(String equipmentType, int slotID, String slotName) {
        return switch (strToEqCat(equipmentType.toLowerCase())) {
            case WEAPONZ -> {
                if (slotID >= getWeaponSlots())
                    yield true;
                yield getEquippedWeapons()[slotID] != null;
            }
            case ARMOUR -> {
                if (!getEquippedArmour().containsKey(slotName))
                    yield true;
                yield getEquippedArmour().get(slotName) != null;
            }
            case TRINKETZ -> {
                if (slotID >= getTrinketSlots())
                    yield true;
                yield getEquippedTrinkets()[slotID] != null;
            }
            case NECKZ -> {
                if (slotID >= getNeckSlots())
                    yield true;
                yield getEquippedNecks()[slotID] != null;
            }
            default -> true;
        };
    }

    public static int getAvailableEquipmentID(String equipmentType, int ignoreThisID) {
        return switch (strToEqCat(equipmentType.toLowerCase())) {
            case WEAPONZ -> {
                for (int i = 0; i < getWeaponSlots(); i++) {
                    if (i == ignoreThisID)
                        continue;
                    if (getEquippedWeapons()[i] == null) {
                        yield i;
                    }
                }
                yield -1;
            }
            case TRINKETZ -> {
                for (int i = 0; i < getTrinketSlots(); i++) {
                    if (getEquippedTrinkets()[i] == null)
                        yield i;
                }
                yield -1;
            }
            case NECKZ -> {
                for (int i = 0; i < getNeckSlots(); i++) {
                    if (getEquippedNecks()[i] == null)
                        yield i;
                }
                yield -1;
            }
            default -> -1;
        };
    }

    public static int equipTrinketOrNeck(Item itemToEquip, int eqSlot, boolean procStatz) throws Exception {
        if (!PlayerClass.playerPassesReq(itemToEquip.getStatRequirements())) {
            return 4; // reqs not passed
        }
        String itemType = itemToEquip.getItemType();
        Item[] itemUsed = itemType.equals("neck") ? getEquippedNecks() : getEquippedTrinkets();
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
            getEquippedNecks()[eqSlot] = itemToEquip;
        else
            getEquippedTrinkets()[eqSlot] = itemToEquip;
        if (procStatz) {
            PlayerClass.incrementPlayerStat(itemToEquip.getStatBoons(), false);
            PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        }
        return 0;
    }
    public static int equipArmour(ArmourItem armourToEquip, boolean procStatz, boolean overrideReqs) throws Exception {
        if (!(PlayerClass.playerPassesReq(armourToEquip.getStatRequirements()) ||
                overrideReqs)) {
            return 4; // reqs not passed
        }
        if (!getEquippedArmour().containsKey(armourToEquip.getArmourSlot())) {
            return 3; // no such slot
        }
        if (getEquippedArmour().get(armourToEquip.getArmourSlot()) != null) {
            return 1; // armour already there
        }
        getEquippedArmour().put(armourToEquip.getArmourSlot(), armourToEquip);
        if (procStatz) {
            PlayerClass.incrementPlayerStat(armourToEquip.getStatBoons(), false);
            PlayerClass.incrementPlayerStat("Armour", armourToEquip.getArmourBonus());
            PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        }
        return 0;
    }
    public static int getEquippedWeaponBoonz(boolean returnMinDmg, boolean applyScaling) {
        int tally = 0;
        int idToSkip = -1;
        int curDmg = -1;
        for (WeaponItem equippedWep: getEquippedWeapons()) {
            if (equippedWep == null) {
                break;
            }
            if (!equippedWep.isIs1H()) {
                if (equippedWep.getItemID() == idToSkip) {
                    idToSkip = -1;
                    continue;
                }
                idToSkip = equippedWep.getItemID();
            }
            curDmg = returnMinDmg ? equippedWep.getMinDmg() : equippedWep.getMaxDmg();
            if (applyScaling) {
                curDmg = CombatUtils.scaleWepDmg(curDmg, equippedWep);
            }
            tally +=  curDmg;
        }
        return tally;
    }
    public static int equipWeapon(WeaponItem weaponToEquip, int eqSlot, boolean procStatz,
                                  boolean overrideRecs) throws Exception {
        if (!(PlayerClass.playerPassesReq(weaponToEquip.getStatRequirements()) ||
                overrideRecs)) {
            return 4; // reqs not passed
        }
        int foundEqSlot1;
        int foundEqSlot2;
        if (eqSlot != -1 && weaponToEquip.isIs1H()) {
            if (getEquippedWeapons()[eqSlot] != null)
                return 1; // weapon already equipped there
            getEquippedWeapons()[eqSlot] = weaponToEquip;
            return 0;
        } else if (eqSlot != -1) {
            foundEqSlot1 = eqSlot;
        } else {
            foundEqSlot1 = getAvailableEquipmentID("weapon", -1);
        }
        if (foundEqSlot1 == -1)
            return 2; // no slots available
        if (!weaponToEquip.isIs1H()) {
            foundEqSlot2 = getAvailableEquipmentID("weapon", foundEqSlot1);
            if (foundEqSlot2 == -1)
                return 2; // no slots available again
            getEquippedWeapons()[foundEqSlot2] = weaponToEquip;
        }
        getEquippedWeapons()[foundEqSlot1] = weaponToEquip;
        if (procStatz) {
            PlayerClass.incrementPlayerStat(weaponToEquip.getStatBoons(), false);
            PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        }
        return 0; // success
    }
    public static Item unequipTrinketOrNeck(int slotIdx, boolean unequipTrinket) throws Exception {
        Item[] itemUsed = unequipTrinket ? getEquippedTrinkets() : getEquippedNecks();
        if (slotIdx >= itemUsed.length) {
            return null;
        } else if (itemUsed[slotIdx] == null) {
            return null;
        }
        Item itemCopy = itemUsed[slotIdx];
        if (unequipTrinket)
            getEquippedTrinkets()[slotIdx] = null;
        else
            getEquippedNecks()[slotIdx] = null;
        PlayerClass.incrementPlayerStat(itemCopy.getStatBoons(), true);
        PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        return itemCopy;
    }
    public static ArmourItem unequipArmour(String armourSlot) throws Exception {
        if (!getEquippedArmour().containsKey(armourSlot)) {
            return null; // no such slot
        }
        if (getEquippedArmour().get(armourSlot) == null) {
            return null; // nothing to unequip
        }
        ArmourItem armourCopy = new ArmourItem(getEquippedArmour().get(armourSlot));
        getEquippedArmour().put(armourSlot, null);
        PlayerClass.incrementPlayerStat(armourCopy.getStatBoons(), true);
        PlayerClass.incrementPlayerStat("Armour", -armourCopy.getArmourBonus());
        PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        return armourCopy;
    }
    public static WeaponItem unequipWeapon(int weaponIDToUnequip) throws Exception {
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
        PlayerClass.saveCharacter(StoryDisplayer.getCurIndex());
        return weaponCopy;
    }

    public static void populateInventoryFromSave(String[] saveValz) {
        // this gets called once PlayerClass' parsing of character data is done
        // and picks where it left off to initialise inventory
        // idx 0 contains what's currently equipped
        // idx 1 what's in the inventory, the last value is the current gold amount
        String[] itemIDLinez = new String[2];
        try {
            for (int i = 0; i < 2; i++) {
                itemIDLinez[i] = saveValz[i];
                if (i == 0) {
                    setMalformedEq(false);
                } else {
                    setMalformedInv(false);
                }
            }
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            System.out.println("Inventory data not found! Aborting inventory loading. . .");
            return;
        }
        try {
            for (String curStrID : itemIDLinez[0].split(" ")) {
                // this is equipment
                int curID = Integer.parseInt(curStrID);
                List<String> curItemSpecs = InventoryCache.getItem(curID);
                switch (strToEqCat(curItemSpecs.get(0).toLowerCase())) {
                    case WEAPONZ:
                        equipWeapon(((WeaponItem) Objects
                                        .requireNonNull(Item.smartItemInit(curID, curItemSpecs))),
                                -1, false, true);
                        break;
                    case ARMOUR:
                        equipArmour((ArmourItem) Objects.requireNonNull(
                                Item.smartItemInit(curID, curItemSpecs)), false, true);
                        break;
                    case TRINKETZ, NECKZ:
                        equipTrinketOrNeck((Item) Objects.requireNonNull(Item.smartItemInit(curID,
                                        curItemSpecs)),
                                -1, false);
                        break;
                    default:
                        GlobalConf.issueLog("Malformed equipment data in save file! Aborting . . .",
                                GlobalConf.SEVERITY_LEVEL_ERROR,
                                true);
                }
            }
        } catch (Exception e) {
            return;
        }
        try {
            // and this is inv
            String[] idsToIterate = itemIDLinez[1].split(" ");
            for (int i = 0; i < idsToIterate.length; i++) {
                int curID = Integer.parseInt(idsToIterate[i]);
                if (i == idsToIterate.length - 1) {
                    setCurrentGold(curID);
                    break;
                }
                List<String> curItemSpecs = InventoryCache.getItem(curID);
                switch (strToEqCat(curItemSpecs.get(0).toLowerCase())) {
                    case WEAPONZ:
                        Inventory.getInventorySpace().add(((WeaponItem) Item.smartItemInit(curID, curItemSpecs)));
                        break;
                    case ARMOUR:
                        Inventory.getInventorySpace()
                                .add(((ArmourItem) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs))));
                        break;
                    case TRINKETZ, NECKZ, INVENTORY:
                        Inventory.getInventorySpace()
                                .add((Item) Objects.requireNonNull(Item.smartItemInit(curID, curItemSpecs)));
                        break;
                    default:
                        GlobalConf.issueLog("Malformed inventory data in save file! Aborting . . .",
                                GlobalConf.SEVERITY_LEVEL_ERROR, true);
                }
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
        }
    }
    public static void populateInventoryFromSave(Scanner playerScanner) {
        String []saveValz = new String[2];
        for (int i = 0; i < 2; i++) {
            saveValz[i] = playerScanner.nextLine();
        }
        populateInventoryFromSave(saveValz);
    }

    public static Item[] craftItemArray(eqCats eqCat, boolean isEquipping, String armourCat) {
        Item[] arrTempie = new Item[]{};
        if (isEquipping) {
            String stringifiedCat = (switch (eqCat) {
                case WEAPONZ -> "weapon";
                case ARMOUR -> "armour";
                case NECKZ -> "neck";
                case TRINKETZ -> "trinket";
                default -> "None";
            });
            if (eqCat != eqCats.ARMOUR) {
                return getInventorySpace().stream().filter(curInvIt ->
                        curInvIt.getItemType().equals(stringifiedCat) &&
                                PlayerClass.playerPassesReq(curInvIt.getStatRequirements())).toArray(Item[]::new);
            } else {
                return getInventorySpace().stream().filter(curInvIt ->
                        curInvIt.getItemType().equals(stringifiedCat) &&
                                ((ArmourItem)curInvIt).getArmourSlot().equals(armourCat) &&
                                PlayerClass.playerPassesReq(curInvIt.getStatRequirements())).toArray(Item[]::new);
            }
        }
        return (switch (eqCat) {
            case WEAPONZ:
                arrTempie = new Item[getWeaponSlots()];
                for (int i = 0; i < arrTempie.length; i++) {
                    if (getEquippedWeapons()[i] == null) {
                        arrTempie[i] = new Item(-1, "", "", "weapon",
                                0, null, null);
                    } else {
                        arrTempie[i] = getEquippedWeapons()[i];
                    }
                }
                yield arrTempie;
            case ARMOUR:
                arrTempie = new Item[getArmourSlots()];
                int ctr = 0;
                for (String key : getEquippedArmour().keySet()) {
                    if (getEquippedArmour().get(key) == null) {
                        arrTempie[ctr++] = new ArmourItem(-1, "", "", 0,
                                null, null, key, 0);
                    } else {
                        arrTempie[ctr++] = getEquippedArmour().get(key);
                    }
                }
                yield arrTempie;
            case NECKZ:
                arrTempie = new Item[getNeckSlots()];
                for (int i = 0; i < arrTempie.length; i++) {
                    if (getEquippedNecks()[i] == null) {
                        arrTempie[i] = new Item(-1, "", "", "neck",
                                0, null, null);
                    } else {
                        arrTempie[i] = getEquippedNecks()[i];
                    }
                }
                yield arrTempie;
            case TRINKETZ:
                arrTempie = new Item[getTrinketSlots()];
                for (int i = 0; i < arrTempie.length; i++) {
                    if (getEquippedTrinkets()[i] == null) {
                        arrTempie[i] = new Item(-1, "", "", "trinket",
                                0, null, null);
                    } else {
                        arrTempie[i] = getEquippedTrinkets()[i];
                    }
                }
                yield arrTempie;
            default:
                yield getInventorySpace().toArray(Item[]::new);
        });
    }
    public static List<List<String>> prepareItemSeriesDisplay(int startingInvID, int entriesToDisplay,
                                                              eqCats itemCat, Item[] provideItemz,
                                                              String armourCat) throws Exception {
        Item[] itemzToHandle = new Item[]{};
        String prependerFlavour;
        if (provideItemz != null) {
            itemzToHandle = provideItemz.clone();
        } else {
            itemzToHandle = craftItemArray(itemCat, false, armourCat);
        }
        prependerFlavour = (switch (itemCat) {
            case WEAPONZ:
                yield "Weapon #";
            case ARMOUR:
                yield "";
            case NECKZ:
                yield "Necklace #";
            case TRINKETZ:
                yield "Trinket #";
            default:
                yield "itemz";
        });
        if (startingInvID + entriesToDisplay >= itemzToHandle.length) {
            entriesToDisplay = itemzToHandle.length - startingInvID;
        }
        List<List<String>> preparedItemz = new ArrayList<>();
        List<String> tempie = new LinkedList<>();
        int dispRefCtr = 1;
        for (int i = startingInvID; i < startingInvID + entriesToDisplay; i++) {
            tempie.clear();
            if (prependerFlavour.equalsIgnoreCase("itemz")) {
                tempie.add("[" + dispRefCtr++ + "] " + getInventorySpace().get(i).getName());
                tempie.add(getInventorySpace().get(i).getDescription());
                tempie.add(getInventorySpace().get(i).getItemType());
            } else {
                if (itemCat != eqCats.ARMOUR) {
                    tempie.add("[" + prependerFlavour + (dispRefCtr++) + "]");
                } else {
                    tempie.add("[" + ((ArmourItem)itemzToHandle[i]).getArmourSlot() + "]");
                }
                tempie.addAll(Objects.requireNonNull(
                        inspectItem(itemzToHandle[i], false, true, i,
                        itemCat, armourCat)));
            }
            preparedItemz.add(new ArrayList<>(tempie));
        }
        return preparedItemz;
    }

    public static List<String> inspectionChoiceMaker(int itemID, boolean isEquipping) {
        List<String> optionz = new ArrayList<>();
        if (itemID != -1 && isEquipping) {
            optionz.add(GlobalConf.getInvDispPrefix() + "[1] Swap");
            optionz.add(GlobalConf.getInvDispPrefix() + "[2] Unequip");
        } else if (isEquipping) {
            optionz.add(GlobalConf.getInvDispPrefix() + "[1] Equip");
        }
        optionz.add(GlobalConf.getInvDispPrefix() + "[" + (optionz.size() + 1) + "] Return");
        return optionz;
    }

    public static void handleEquipping(eqCats itemCat, int itemToEquipInvIdx,
                                       int eqSlot, Item[] availables) throws Exception {
        switch (itemCat) {
            case WEAPONZ:
                Inventory.equipWeapon(((WeaponItem)
                                Objects.requireNonNull(Inventory.removeInventoryItem(
                                        availables[itemToEquipInvIdx].getItemID(), false))),
                        eqSlot, true, true);
                break;
            case ARMOUR:
                Inventory.equipArmour(((ArmourItem)
                                Objects.requireNonNull(Inventory.removeInventoryItem(
                                        availables[itemToEquipInvIdx].getItemID(), false))),
                        true, true);
                break;
            case TRINKETZ, NECKZ:
                Inventory.equipTrinketOrNeck(
                        Objects.requireNonNull(Inventory.removeInventoryItem(
                                availables[itemToEquipInvIdx].getItemID(), false)),
                        eqSlot, true);
                break;
        }
    }

    public static void handleUnequpping(eqCats itemCat, Item itemInQuestion, int equippedAtIndex) throws Exception {
        Item tempieItem = null;
        switch (itemCat) {
            case ARMOUR:
                assert itemInQuestion instanceof ArmourItem;
                tempieItem = unequipArmour(((ArmourItem)itemInQuestion).getArmourSlot());
                break;
            case WEAPONZ:
                tempieItem = unequipWeapon(equippedAtIndex);
                break;
            case TRINKETZ, NECKZ:
                tempieItem = unequipTrinketOrNeck(equippedAtIndex,
                        itemInQuestion.getItemType().toLowerCase().charAt(0) == 't');
                break;
            default:
                break;
        }
        addItemToInventory(tempieItem);
    }
    public static List<String> inspectItem(Item itemInQuestion, boolean includedLilArrowz,
                                           boolean returnList, int rawIndex,
                                           eqCats itemCat, String armourCat) throws Exception {
        List<String> itemDescFieldz = new ArrayList<>();
        List<String> eqOptionz = new ArrayList<>();
        if (itemInQuestion instanceof ArmourItem) {
            armourCat = ((ArmourItem)itemInQuestion).getArmourSlot();
        }
        if (itemInQuestion == null || itemInQuestion.getItemID() == -1) {
            itemDescFieldz.add("Empty");
            if (returnList)
                return  itemDescFieldz;
            for (String i : itemDescFieldz) {
                if (includedLilArrowz)
                    System.out.println(GlobalConf.getInvDispPrefix() + i);
                else
                    System.out.println(i);
            }
            eqOptionz = inspectionChoiceMaker(-1, itemCat != eqCats.INVENTORY);
            for (String option : eqOptionz) {
                System.out.println(option);
            }
            int exitChoice = -1;
            Item[] availables = craftItemArray(itemCat, true, armourCat);
            while (exitChoice != 1) {
                exitChoice = StoryDisplayer.awaitChoiceInput(eqOptionz.size() + 1);
                if (exitChoice == 0) {
                    int itemtoEquip = displayInventoryOrEq(itemCat, true, armourCat);
                    if (itemtoEquip == -1) {
                        return null;
                    }
                    handleEquipping(itemCat, itemtoEquip, rawIndex, availables);
                    return null;
                }
            }
            return null;

            // This is placeholder copy-pasted code for now,
            // if inspecting empty item that will mean that the player
            // is perusing equipment
            // in this case, implement a lil inventory viewer that displays
            // equippable items for this specific slot

        }
        itemDescFieldz.add(itemInQuestion.getName());
        itemDescFieldz.add(itemInQuestion.getDescription());
        if (itemInQuestion instanceof WeaponItem tempie) {
            if (!tempie.isShield()) {
                itemDescFieldz.set(0, itemDescFieldz.get(0) + " (Weapon)");
                itemDescFieldz.add("+" + tempie.getMinDmg() + "-" + tempie.getMaxDmg() + " damage");
            } else {
                itemDescFieldz.set(0, itemDescFieldz.get(0) + " (Shield)");
                itemDescFieldz.add(tempie.getMinDmg() + "-" + tempie.getMaxDmg() + " flat damage absorption");
            }
            itemDescFieldz.add((tempie.isIs1H() ? "One-handed" : "Two-handed"));
            itemDescFieldz.add(tempie.getScalingStat() + " scaling");

        } else if (itemInQuestion instanceof ArmourItem tempie) {
            itemDescFieldz.set(0, itemDescFieldz.get(0) + " (" + tempie.getArmourSlot() + " armour)");
            itemDescFieldz.add("+" + tempie.getArmourBonus() + " armour");
        } else {
            itemDescFieldz.set(0, itemDescFieldz.get(0) + " (" + itemInQuestion.getItemType() + ")");
        }

        if (!itemInQuestion.getStatRequirements().isEmpty()) {
            itemDescFieldz.add("Required stats:");
            for (Map.Entry<String, Integer> curReq : itemInQuestion.getStatRequirements().entrySet()) {
                itemDescFieldz.add(" " + curReq.getKey() + (curReq.getValue() >= 0 ? " > " : " < ") +
                        curReq.getValue() + " (yours: " + PlayerClass.getPlayerStat(curReq.getKey()) + ")");
            }
        }
        if (!itemInQuestion.getStatBoons().isEmpty()) {
            itemDescFieldz.add("Provides:");
            for (Map.Entry<String, Integer> curReq : itemInQuestion.getStatBoons().entrySet()) {
                itemDescFieldz.add(" " + curReq.getKey() + (curReq.getValue() >= 0 ? ": +" : ": -") +
                        curReq.getValue());
            }
        }
        itemDescFieldz.add((itemInQuestion.getBuyValue() >= 0 ?
                "Value: " + itemInQuestion.getBuyValue() + "g" :
                "Quest item"));
        if (returnList)
            return  itemDescFieldz;
        for (String i : itemDescFieldz) {
            if (includedLilArrowz)
                System.out.println(GlobalConf.getInvDispPrefix() + i);
            else
                System.out.println(i);
        }
        return new ArrayList<>();
    }

    public static int handleItemInspectionChoices(Item itemInQuestion, eqCats itemCat, int rawIndex,
                                                  boolean isEquipping) throws Exception {
        List<String> eqOptionz = inspectionChoiceMaker
                (itemInQuestion.getItemID(), isEquipping);
        // at this point it's NOT guaranteed that the options are:
        // 1 - swap, 2 - unequip, 3 - return
        for (String option : eqOptionz) {
            System.out.println(option);
        }
        int exitChoice;
        eqCats curCat = strToEqCat(itemInQuestion.getItemType());
        String armourCat = "";
        if (itemInQuestion instanceof ArmourItem) {
            armourCat = ((ArmourItem)itemInQuestion).getArmourSlot();
        }
        while (true) {
            exitChoice = StoryDisplayer.awaitChoiceInput(eqOptionz.size() + 1);
            if (exitChoice == eqOptionz.size() - 1) {
                return -1;
            }
            if (exitChoice == 1) {
                handleUnequpping(curCat, itemInQuestion, rawIndex);
                return 0;
            } else if (exitChoice == 0) { // swap
                int replacementPicked = displayInventoryOrEq(curCat, true, armourCat);
                if (replacementPicked == -1) {
                    return -1;
                }
                Item[] availables = craftItemArray(curCat, true, armourCat);
                handleUnequpping(curCat, itemInQuestion, rawIndex);
                handleEquipping(curCat, replacementPicked, rawIndex, availables);
                return 0;
            }
        }
    }
    public static int displayInventoryOrEq(eqCats catToDisp, boolean isEquipping,
                                           String armourType) throws Exception {
        int curChoice;
        int curStartIndex;
        int curInvPage = 1;
        int prevPageBind;
        int nextPageBind;
        int exitBind;
        int totalPages = getTotalPages(catToDisp);
        Item[] displayCopy = craftItemArray(catToDisp, isEquipping, armourType);
        String pagenamer = switch (catToDisp) {
            case WEAPONZ:
                yield "Weapons";
            case ARMOUR:
                yield "Armour";
            case TRINKETZ:
                yield "Trinkets";
            case NECKZ:
                yield "Necklaces";
            default:
                yield "Inventory";
        };
        if (isEquipping) {
            pagenamer = "Available " + pagenamer.toLowerCase();
        }
        List<String> optionz = new ArrayList<>();
        while (true) {
            displayCopy = craftItemArray(catToDisp, isEquipping, armourType);
            if (catToDisp == eqCats.INVENTORY && !isEquipping) {
                System.out.println(GlobalConf.getInvDispPrefix() + PlayerClass.getPlayerName());
                System.out.println(GlobalConf.getInvDispPrefix() + "Level " + PlayerClass.getPlayerStat("curLevel") +
                        " (" + PlayerClass.getPlayerStat("xp") +
                        " / " + PlayerClass.getPlayerStat("nextXP") + " XP)");
            }
            System.out.println(GlobalConf.getInvDispPrefix() + PlayerKeywordz.getCurrencyName() + ": " +
                    Inventory.getCurrentGold());
            System.out.println(GlobalConf.getInvDispPrefix() + pagenamer + " page " + curInvPage + " of " +
                    totalPages + "\n" + GlobalConf.getInvDispPrefix());
            optionz.clear();
            prevPageBind = -1;
            nextPageBind = -1;
            curStartIndex = (curInvPage - 1) * 6;
            displaySideBySide(prepareItemSeriesDisplay(curStartIndex, 6, catToDisp,
                            displayCopy, armourType), 3, 10, true);
            if (!isEquipping)
                System.out.println(GlobalConf.getInvDispPrefix() + "View:");
            else
                System.out.println(GlobalConf.getInvDispPrefix() + "Replace with:");
            for (int i = curStartIndex; i < curStartIndex + 6 && i < displayCopy.length; i++) {
                if (catToDisp == eqCats.INVENTORY) {
                    optionz.add(displayCopy[i].getName());
                } else if (catToDisp != eqCats.ARMOUR) {
                    optionz.add(displayCopy[i].getItemType() + " #" + (i + 1));
                } else {
                    if (displayCopy[i].getName().isEmpty()) {
                        optionz.add(((ArmourItem)displayCopy[i]).getArmourSlot());
                    } else {
                        optionz.add(((ArmourItem) displayCopy[i]).getName());
                    }
                }
            }
            if (curInvPage < totalPages) {
                nextPageBind = optionz.size();
                optionz.add("Next page");
            }
            if (curInvPage > 1) {
                prevPageBind = optionz.size();
                optionz.add("Previous page");
            }
            exitBind = optionz.size();
            optionz.add(((catToDisp == eqCats.INVENTORY && !isEquipping) ? "Exit inventory" : "Back"));
            curChoice = StoryDisplayer.awaitChoiceInputFromOptions(optionz.toArray(new String[0]));
            if (curChoice == nextPageBind) {
                curInvPage++;
                continue;
            }
            if (curChoice == prevPageBind) {
                curInvPage--;
                continue;
            }
            if (curChoice == exitBind)
                return -1;
            if (!isEquipping) {
                if (inspectItem(displayCopy[curStartIndex + curChoice], true, false,
                        curStartIndex + curChoice, catToDisp, armourType) == null) {
                    return 0;
                }
                handleItemInspectionChoices(displayCopy[curStartIndex + curChoice], catToDisp,
                        curStartIndex + curChoice, catToDisp != eqCats.INVENTORY);
            } else {
                return curStartIndex + curChoice;
            }
        }
    }

    private static int getTotalPages(eqCats catToDisp) {
        int totalPages = switch (catToDisp) {
            case WEAPONZ:
                yield getWeaponSlots();
            case ARMOUR:
                yield getArmourSlots();
            case TRINKETZ:
                yield getTrinketSlots();
            case NECKZ:
                yield getNeckSlots();
            default:
                yield getInventorySpace().size();
        };
        return totalPages / 6 + 1;
    }

    public static List<List<String>> getEquipmentToDisplay(eqCats sectionType, boolean inclIndexing) {
        List<List<String>> displayPreparer = new ArrayList<>();
        List<String> singleEntry = new ArrayList<>();
        return switch (sectionType) {
            case WEAPONZ:
                for (int curWepIdx = 0; curWepIdx < getWeaponSlots(); curWepIdx++) {
                    singleEntry.clear();
                    if (getEquippedWeapons()[curWepIdx] == null) {
                        singleEntry.add("Empty");
                        displayPreparer.add(new ArrayList<>(singleEntry));
                        continue;
                    }
                    WeaponItem tempie = getEquippedWeapons()[curWepIdx];
                    singleEntry.add((inclIndexing ? "[" + (curWepIdx+1) + "] " : "") +
                            tempie.getName() + (tempie.isShield() ? " (shield, " : " (weapon, ") +
                            (tempie.isIs1H() ? "1H)" : "2H)"));
                    if (!tempie.isShield()) {
                        singleEntry.add("+" + tempie.getMinDmg() + "-" + tempie.getMaxDmg() +
                                " damage (" + tempie.getScalingStat() + ")");
                    } else {
                        singleEntry.add(tempie.getMinDmg() + "-" + tempie.getMaxDmg() +
                                " damage absorption (" + tempie.getScalingStat() + ")");
                    }
                    for (Map.Entry<String, Integer> boonz : tempie.getStatBoons().entrySet()) {
                        singleEntry.add((boonz.getValue() >= 0 ? "+" : "") + boonz.getValue() + " " + boonz.getKey());
                    }
                    displayPreparer.add(new ArrayList<>(singleEntry));
                }
                yield displayPreparer;
            case ARMOUR:
                int indexor = 1;
                for (Map.Entry<String, ArmourItem> curArmour : getEquippedArmour().entrySet()) {
                    singleEntry.clear();
                    if (curArmour.getValue() == null) {
                        singleEntry.add(curArmour.getKey());
                        singleEntry.add("Empty");
                        displayPreparer.add(new ArrayList<>(singleEntry));
                        continue;
                    }
                    singleEntry.add((inclIndexing ? "[" + (indexor++) + "] " : "") + curArmour.getKey());
                    singleEntry.add(curArmour.getValue().getName());
                    singleEntry.add("+" + curArmour.getValue().getArmourBonus() + " armour");
                    for (Map.Entry<String, Integer> boonz : curArmour.getValue().getStatBoons().entrySet()) {
                        singleEntry.add((boonz.getValue() >= 0 ? "+" : "") + boonz.getValue() + " " + boonz.getKey());
                    }
                    displayPreparer.add(new ArrayList<>(singleEntry));
                }
                yield displayPreparer;
            case NECKZ, TRINKETZ:
                Item[] whicheverOne = (sectionType == eqCats.TRINKETZ) ? getEquippedTrinkets() : getEquippedNecks();
                for (int i = 0; i < whicheverOne.length; i++) {
                    singleEntry.clear();
                    if (whicheverOne[i] == null) {
                        singleEntry.add("Empty");
                        displayPreparer.add(new ArrayList<>(singleEntry));
                        continue;
                    }
                    singleEntry.add((inclIndexing ? "[" + (i+1) + "] " : "") + whicheverOne[i].getName());
                    for (Map.Entry<String, Integer> boonz : whicheverOne[i].getStatBoons().entrySet()) {
                        singleEntry.add((boonz.getValue() >= 0 ? "+" : "") + boonz.getValue() + " " + boonz.getKey());
                    }
                    displayPreparer.add(new ArrayList<>(singleEntry));
                }
                yield displayPreparer;
            default:
                yield null;
        };
    }
    public static void displayEquipment() throws Exception {
        int curChoice = -1;
        String[] optionz = new String[]{"Weapons", "Armour", "Neck", "Trinkets", "Return"};
        while (true) {
            System.out.println(GlobalConf.getInvDispPrefix() + PlayerClass.getPlayerName());
            System.out.println(GlobalConf.getInvDispPrefix() + "Level " + PlayerClass.getPlayerStat("curLevel") +
                    " (" + PlayerClass.getPlayerStat("xp") +
                    " / " + PlayerClass.getPlayerStat("nextXP") + " XP)");
            System.out.println(GlobalConf.getInvDispPrefix() +
                    PlayerClass.getPlayerStat("curHealth") + "/" + PlayerClass.getPlayerStat("maxHealth") + " HP");
            System.out.println(GlobalConf.getInvDispPrefix() + PlayerClass.getPlayerStat("Armour") + " Armour");
            System.out.println(GlobalConf.getInvDispPrefix() + PlayerKeywordz.getCurrencyName() + ": " +
                    Inventory.getCurrentGold());
            for (Map.Entry<String, Integer> curEntry : PlayerClass.getPlayerAtts().entrySet()) {
                System.out.println(GlobalConf.getInvDispPrefix() + curEntry.getKey() + ": " + curEntry.getValue());
            }
            System.out.println(GlobalConf.getInvDispPrefix());
            String[] ittie = new String[]{"Weapons", "Armour", "Neck", "Trinkets"};
            eqCats[] ittieValz = new eqCats[]{eqCats.WEAPONZ, eqCats.ARMOUR, eqCats.NECKZ, eqCats.TRINKETZ};
            for (int i = 0; i < ittie.length; i++) {
                System.out.println(GlobalConf.getInvDispPrefix() + "[" + (i + 1) + "] " + ittie[i] + ":");
                displaySideBySide(getEquipmentToDisplay(ittieValz[i], false),
                        5, 5, true);
            }
            System.out.println(GlobalConf.getInvDispPrefix() + "Inspect:");
            curChoice = StoryDisplayer.awaitChoiceInputFromOptions(optionz);
            if (curChoice == 4) {
                return;
            } else {
                inspectEquipmentSection(curChoice);
            }
        }
    }

    public static void inspectEquipmentSection(int eqSection) throws Exception {
        // weaponz - 0, armour - 1, neck - 2, trinketz - 3
        eqCats picked = switch (eqSection) {
            case 0 -> eqCats.WEAPONZ;
            case 1 -> eqCats.ARMOUR;
            case 2 -> eqCats.NECKZ;
            case 3 -> eqCats.TRINKETZ;
            default -> eqCats.INVENTORY;
        };
        // redundant but hopefully clearer
        displayInventoryOrEq(picked, false, "");
    }
}