package foeTypes;

import combat.CombatUtils;
import combat.Foe;

public class Automaton extends Foe {
    public Automaton(String specificName, boolean matchPlayerLv, int relativeLvOffset,
                  int absLvOffset, int curHealth, int maxHealth, int curMana,
                  int maxMana, int xpYield, int strength, int dexterity, int intellect,
                  int armour, int goldDrop, int numAttacksPerTurn, int specialAttackChance) {
        super("Automaton " + specificName, 0, curHealth, maxHealth,
                curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop,
                numAttacksPerTurn, specialAttackChance);
        super.setLevel(CombatUtils.genEnemyLevel(matchPlayerLv, relativeLvOffset, absLvOffset));
        if (maxHealth <= 0) {
            super.generateMaxHealth(20, 2, 7);
        }
        if (xpYield <= 0) {
            super.setXpYield(Foe.genXPYield(super.getLevel()));
        }
        if (strength <= 0) {
            super.generateStrength(7, 2., 2, 1.5);
        }
        if (dexterity <= 0) {
            super.generateDexterity(2, 3.5, 1, 2.5);
        }
        if (intellect <= 0) {
            super.generateIntellect(3, 4., 0, -1);
        }
        if (armour <= 0) {
            super.generateArmour(6, 2., 1.5, 1.8);
        }
        if (goldDrop <= 0) {
            super.generateGoldDrop(30, 6, 10);
        }
        if (numAttacksPerTurn <= 0) {
            super.setNumAttacksPerTurn(1);
        }
        if (specialAttackChance <= 0) {
            super.setSpecialAttackChance(20);
        }
    }
    @Override
    public String specialAttackPreProc() {
        super.setArmour(super.getArmour() * 2);
        return this.getName() + " reassembles its plates into a defensive stance for the next turn!";
    }
    @Override
    public void specialAttackPostProc() {
        super.setArmour(super.getArmour() / 2);
    }
}
