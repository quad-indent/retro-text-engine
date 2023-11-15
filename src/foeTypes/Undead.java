package foeTypes;

import combat.CombatUtils;
import combat.Foe;
import player.PlayerClass;

public class Undead extends Foe {
    private int oldPlayerArmour;
    public int getOldPlayerArmour() {
        return oldPlayerArmour;
    }
    public void setOldPlayerArmour(int oldPlayerArmour) {
        this.oldPlayerArmour = oldPlayerArmour;
    }
    public Undead(String specificName, boolean matchPlayerLv, int relativeLvOffset,
                  int absLvOffset, int curHealth, int maxHealth, int curMana,
                  int maxMana, int xpYield, int strength, int dexterity, int intellect,
                  int armour, int goldDrop, int numAttacksPerTurn, int specialAttackChance) {
        super("Undead " + specificName, 0, curHealth, maxHealth,
                curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop,
                numAttacksPerTurn, specialAttackChance);
        super.setLevel(CombatUtils.genEnemyLevel(matchPlayerLv, relativeLvOffset, absLvOffset));
        if (maxHealth <= 0) {
            super.generateMaxHealth(10, 3, 5);
        }
        if (xpYield <= 0) {
            super.setXpYield(Foe.genXPYield(super.getLevel()));
        }
        if (strength <= 0) {
            super.generateStrength(13, 1.5, 3, 1.5);
        }
        if (dexterity <= 0) {
            super.generateDexterity(7, 2., 1, 3.);
        }
        if (intellect <= 0) {
            super.generateIntellect(1, 5., 0, -1);
        }
        if (armour <= 0) {
            super.generateArmour(5, 2, 1.5, 1.5);
        }
        if (goldDrop <= 0) {
            super.generateGoldDrop(5, 4, 5);
        }
        if (numAttacksPerTurn <= 0) {
            super.setNumAttacksPerTurn(2);
        }
        if (specialAttackChance <= 0) {
            super.setSpecialAttackChance(25);
        }
    }
    @Override
    public String specialAttackPreProc() {
        setOldPlayerArmour(PlayerClass.getArmour());
        PlayerClass.setArmour((int)Math.round(PlayerClass.getArmour() * 0.75));
        return this.getName() + " shreds your armour, reducing it from " + getOldPlayerArmour() + " to " +
                PlayerClass.getArmour() + ", before launching an attack!";
    }
    @Override
    public void specialAttackPostProc() {
        PlayerClass.setArmour(getOldPlayerArmour());
    }
}
