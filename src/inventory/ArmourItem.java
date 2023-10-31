package inventory;

import java.util.Map;

public class ArmourItem extends Item {
    private String armourSlot = "";
    private int armourBonus = -1;

    public String getArmourSlot() {
        return armourSlot;
    }

    public void setArmourSlot(String armourSlot) {
        this.armourSlot = armourSlot;
    }

    public int getArmourBonus() {
        return armourBonus;
    }

    public void setArmourBonus(int armourBonus) {
        this.armourBonus = armourBonus;
    }

    public ArmourItem(ArmourItem item) {
        super(item.getItemID(), item.getName(), item.getDescription(), "Armour", item.getBuyValue(),
                item.getStatBoons(), item.getStatRequirements());
        this.armourSlot = item.getArmourSlot();
        this.armourBonus = item.getArmourBonus();
    }
    public ArmourItem(int itemID, String name, String description, int itemValue, Map<String, Integer> statBoons,
                      Map<String, Integer> statRequirements, String armourSlot, int armourBonus) {
        super(itemID, name, description, "Armour", itemValue, statBoons, statRequirements);
        this.armourSlot = armourSlot;
        this.armourBonus = armourBonus;
    }
}
