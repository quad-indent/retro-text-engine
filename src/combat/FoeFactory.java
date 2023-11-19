package combat;

import foeTypes.Automaton;
import foeTypes.Goblin;
import foeTypes.Undead;
import foeTypes.foeArchetypes.BasicFoe;
import foeTypes.foeArchetypes.MultiAttacker;
import storyBits.GlobalConf;

import java.util.Map;

public class FoeFactory {
    public static Foe retrieveFoe(Map<String, String> foeParamz) {
        return switch (foeParamz.get("specialAttackType").toLowerCase()) {
            case "multiAttacker" -> new MultiAttacker(foeParamz);
            default -> new BasicFoe(foeParamz);
        };
    }
    public static Foe retrieveFoe(String foeType, String foeName, boolean matchPlayerLv, int relativeLvOffset,
                                  int absLvOffset, int curHealth, int maxHealth, int curMana,
                                  int maxMana, int xpYield, int strength, int dexterity, int intellect,
                                  int armour, int goldDrop, int numAttacksPerTurn, int specialAttackChance) {
        return switch (foeType.toLowerCase()) {
            case "goblin" -> new Goblin(foeName, matchPlayerLv, relativeLvOffset, absLvOffset, curHealth, maxHealth,
                curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop, numAttacksPerTurn,
                specialAttackChance);
            case "automaton" ->
                new Automaton(foeName, matchPlayerLv, relativeLvOffset, absLvOffset, curHealth, maxHealth,
                        curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop, numAttacksPerTurn,
                        specialAttackChance);
            case "undead" ->
                new Undead(foeName, matchPlayerLv, relativeLvOffset, absLvOffset, curHealth, maxHealth,
                        curMana, maxMana, xpYield, strength, dexterity, intellect, armour, goldDrop, numAttacksPerTurn,
                        specialAttackChance);
            default -> new Goblin("Errornimus", true, 0,
                    0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0);
        };
    }
}
