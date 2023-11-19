package foeTypes;

import combat.CombatUtils;
import combat.Foe;
public class Goblin extends Foe {
    public Goblin(String specificName, boolean matchPlayerLv, int relativeLvOffset,
                  int absLvOffset, int curHealth, int maxHealth, int curMana,
                  int maxMana, int xpYield, int strength, int dexterity, int intellect,
                  int armour, int goldDrop, int numAttacksPerTurn, int specialAttackChance) {
        super("Goblin " + specificName, 0, curHealth, maxHealth,
                curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop,
                numAttacksPerTurn, specialAttackChance);
    }
    public void runCheckz() {
        if (super.getLevel() <= 0) {
            super.setLevel(CombatUtils.genEnemyLevel(true, 0, 0));
        }
        if (super.getMaxHealth() <= 0) {
            super.generateMaxHealth(6, 1, 3);
        }
        if (super.getXpYield() <= 0) {
            super.setXpYield(Foe.genXPYield(super.getLevel()));
        }
        if (super.getStrength() <= 0) {
            super.generateStrength(7, 3., 1, 2.);
        }
        if (super.getDexterity() <= 0) {
            super.generateDexterity(9, 2., 2, 2.);
        }
        if (super.getIntellect() <= 0) {
            super.generateIntellect(3, 4., 0, -1);
        }
        if (super.getArmour() <= 0) {
            super.generateArmour(2, 0, 2., 1.5);
        }
        if (super.getGoldDrop() <= 0) {
            super.generateGoldDrop(0, 4, 6);
        }
        if (super.getNumAttacksPerTurn() <= 0) {
            super.setNumAttacksPerTurn(1);
        }
        if (super.getSpecialAttackChance() <= 0) {
            super.setSpecialAttackChance(45);
        }
    }
    @Override
    public String specialAttackPreProc() {
        super.setNumAttacksPerTurn(2);
        return this.getName() + " winds up for a quick flurry of two attacks!";
    }
    @Override
    public void specialAttackPostProc() {
        super.setNumAttacksPerTurn(1);
    }
}
