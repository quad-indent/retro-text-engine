import java.util.LinkedHashMap;
import java.util.Map;

public class Goblin extends Foe {
    public Goblin(String specificName, boolean matchPlayerLv, int relativeLvOffset,
                  int absLvOffset, int curHealth, int maxHealth, int curMana,
                  int maxMana, int xpYield, int strength, int dexterity, int intellect,
                  int armour) {
        super("Goblin " + specificName, 0, curHealth, maxHealth,
                curMana, maxMana, xpYield, strength, dexterity, intellect, armour);
        super.setLevel(CombatUtils.genEnemyLevel(matchPlayerLv, relativeLvOffset, absLvOffset));
        if (maxHealth == 0) {
            super.setMaxHealth(6 + super.getLevel() * CombatUtils.genRandomNum(1, 3));
            super.setCurHealth(super.getMaxHealth());
        }
        if (xpYield == 0) {
            super.setXpYield(Foe.genXPYield(super.getLevel()));
        }
        if (strength == 0) {
            super.setStrength(7 + super.getLevel() / 3 +
                    CombatUtils.genRandomNum(1, super.getLevel() / 2));
        }
        if (dexterity == 0) {
            super.setDexterity(9 + super.getLevel() / 2 +
                    CombatUtils.genRandomNum(2, super.getLevel() / 2));
        }
        if (intellect == 0) {
            super.setIntellect(3 + super.getLevel() / 4);
        }
        if (armour == 0) {
            super.setArmour(2 + CombatUtils.
                    genRandomNum(super.getLevel() / 2, (int)((double)super.getLevel() * 1.5)));
        }
    }

    @Override
    public void specialAttack() {


    }
}
