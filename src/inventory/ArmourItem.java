package inventory;

import java.util.Map;

public class ArmourItem extends Item {
    private String armourSlot = "";
    private int armourBonus = -1;

    public ArmourItem(int itemID, String name, String description, Map<String, Integer> statBoons,
                      Map<String, Integer> statRequirements, String armourSlot, int armourBonus) {
        super(itemID, name, description, "Armour", statBoons, statRequirements);
        this.armourSlot = armourSlot;
        this.armourBonus = armourBonus;
    }
}
