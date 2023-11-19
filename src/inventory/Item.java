package inventory;

import storyBits.GlobalConf;
import storyBits.StoryBlockMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Item {
    private String name = "";
    private String description = "";
    private String itemType = "";
    private int buyValue = -1;
    private int itemID = -1;
    private Map<String, Integer> statRequirements = new LinkedHashMap<>();
    private Map<String, Integer> statBoons = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Integer> getStatRequirements() {
        return statRequirements;
    }

    public void setStatRequirements(Map<String, Integer> statRequirements) {
        this.statRequirements = statRequirements;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(int buyValue) {
        this.buyValue = buyValue;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public Map<String, Integer> getStatBoons() {
        return statBoons;
    }

    public void setStatBoons(Map<String, Integer> statBoons) {
        this.statBoons = statBoons;
    }

    public Item(int itemID, String name, String description, String itemType, int itemValue,
                Map<String, Integer> statBoons, Map<String, Integer> statRequirements) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.itemType = itemType.toLowerCase();
        this.buyValue = itemValue;
        if (statBoons != null) {
            this.statBoons = statBoons;
        }
        if (statRequirements != null) {
            this.statRequirements = statRequirements;
        }
    }

    public static Item smartItemInit(int itemID) throws Exception {
        return (Item) smartItemInit(itemID, InventoryCache.getItem(itemID));
    }
    public static Object smartItemInit(int itemID, List<String> argz) throws Exception {
        if (argz.size() < 4) {
            GlobalConf.issueLog("Misconfigured data! Check your configs/itemTable.txt file",
                    GlobalConf.SEVERITY_LEVEL_WARNING, false);
        }
        String itemType = argz.get(itEnum.I_TYPE.val);
        if (!(itemType.equals("weapon") || itemType.equals("armour") || itemType.equals("trinket") ||
                itemType.equals("neck") || itemType.equals("junk") || itemType.equals("quest"))) {
            GlobalConf.issueLog("Misconfigured data! Check your configs/itemTable.txt file",
                    GlobalConf.SEVERITY_LEVEL_WARNING, false);
        }
        String itemName = argz.get(itEnum.I_NAME.val);
        String itemDesc = argz.get(itEnum.I_DESC.val);
        int itemSellsFor = Integer.parseInt(argz.get(itEnum.I_SELLSFOR.val));

        switch (itemType) {
            case "trinket", "neck", "junk", "quest":
                return new Item(itemID, itemName, itemDesc, itemType, itemSellsFor,
                        assembleStatBoonz(argz), assembleStatReqz(argz));
            case "weapon":
                boolean is1H = argz.get(itEnum.I_1H.val).equals("1H");
                int minDmg = Integer.parseInt(argz.get(itEnum.I_MINDMG.val));
                int maxDmg = Integer.parseInt(argz.get(itEnum.I_MAXDMG.val));
                String scalingStat = argz.get(itEnum.I_SCALESTAT.val);
                boolean isShield = argz.get(itEnum.I_ISSHIELD.val).equalsIgnoreCase("shield");
                return new WeaponItem(itemID, itemName, itemDesc, itemSellsFor, assembleStatBoonz(argz),
                        assembleStatReqz(argz), minDmg, maxDmg, is1H, isShield, scalingStat);
            case "armour":
                String armourSlot = argz.get(itEnum.I_ARMOURSLOT.val);
                int armourBonus = Integer.parseInt(argz.get(itEnum.I_ARMOURVAL.val));
                return new ArmourItem(itemID, itemName, itemDesc, itemSellsFor, assembleStatBoonz(argz),
                        assembleStatReqz(argz), armourSlot, armourBonus);
        }
        return null;
    }

    public static Map<String, Integer> assembleStatBoonz(List<String> rawStatz) {
        Map<String, Integer> returnal = new LinkedHashMap<>();
        for (String stat : rawStatz) {
            if (StoryBlockMaster.stringContainsAny(stat, new char[]{'+', '-'})) {
                String[] splittie = stat.split("[+-]");
                assert splittie.length == 2 : "Misconfigured item named " + rawStatz.get(1) +
                        " in configs/itemTable.txt found!";
                boolean isPositive = stat.contains("+");
                returnal.put(splittie[0], isPositive ?
                        Integer.parseInt(splittie[1]) : Integer.parseInt(splittie[1]) * -1);
            }
        }
        return returnal;
    }
    public static Map<String, Integer> assembleStatReqz(List<String> rawStatz) {
        Map<String, Integer> returnal = new LinkedHashMap<>();
        for (String stat : rawStatz) {
            if (StoryBlockMaster.stringContainsAny(stat, new char[]{'>', '<'})) {
                String[] splittie = stat.split("[><]");
                assert splittie.length == 2 : "Misconfigured item named " + rawStatz.get(1) +
                        " in configs/itemTable.txt found!";
                boolean isPositive = stat.contains(">");
                returnal.put(splittie[0], isPositive ?
                        Integer.parseInt(splittie[1]) : Integer.parseInt(splittie[1]) * -1);
            }
        }
        return returnal;
    }

    @Override
    public boolean equals(Object o2) {
        if (!(o2 instanceof Item)) {
            return false;
        }
        return ((Item) o2).getItemID() == this.getItemID();
    }
}
