package inventory;

import java.util.LinkedHashMap;
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

    public Item(int itemID, String name, String description, String itemType, Map<String, Integer> statBoons,
                Map<String, Integer> statRequirements) {
        this.itemID = itemID;
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.statBoons = statBoons;
        this.statRequirements = statRequirements;
    }
}
