import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CombatUtils {
    public static int genRandomNum(int minOut, int maxOut) {
        if (minOut >= maxOut)
            return minOut;
        return ThreadLocalRandom.current().nextInt(minOut, maxOut + 1);
    }

    public static int getMean(int[] numz) {
        return (int)Arrays.stream(numz).average().getAsDouble();
    }

    public static int getChanceToHit(int thisDex, int otherDex, int thisStr, int otherStr, int attackType) {
        // high strength - high chance is atk type 2
        // high dex - high chance at atk type 0
        // atk type 1 - both str and dex taken into cons
        boolean isDisadvantagedDex = thisDex < otherDex;
        boolean isDisadvantagedStr = thisStr < otherStr;
        int absDexDiff = Math.abs(thisDex - otherDex);
        int absStrDiff = Math.abs(thisStr - otherStr);
        switch (attackType) {
            case 0 -> {
                if (!isDisadvantagedDex) {
                    return trimValue(75 + 5 * absDexDiff, 0, 100);
                } else {
                    return trimValue(75 - 4 * absDexDiff, 0, 100);
                }
            }
            case 1 -> {
                int betterofTwoDIff = 0;
                boolean overwhelmingAdvantage = !(isDisadvantagedDex || isDisadvantagedStr);
                boolean overwhelmingDisadvantage = isDisadvantagedDex && isDisadvantagedStr;
                if (overwhelmingDisadvantage)
                    betterofTwoDIff = Math.min(absStrDiff, absDexDiff);
                else if (!isDisadvantagedDex && isDisadvantagedStr)
                    betterofTwoDIff = absDexDiff;
                else if (isDisadvantagedDex)
                    betterofTwoDIff = absStrDiff;
                else
                    betterofTwoDIff = Math.max(absStrDiff, absDexDiff);
                if (overwhelmingDisadvantage)
                    return trimValue(40 - 3 * betterofTwoDIff, 0, 100);
                if (overwhelmingAdvantage)
                    return trimValue(50 + 8 * betterofTwoDIff, 0, 100);
                return trimValue(50 + 4 * betterofTwoDIff, 0, 100);
            }
            case 2 -> {
                if (!isDisadvantagedStr) {
                    return trimValue(55 + 7 * absDexDiff, 0, 100);
                } else {
                    return trimValue(55 - 2 * absDexDiff, 0, 100);
                }
            }
        }
        return 1;
    }
    public static int getChanceToCrit(int thisDex, int otherDex, int attackType) {
        int statDiff = Math.abs(thisDex - otherDex);
        int critChance = 0;
        if (thisDex < otherDex) {
            critChance = 20 - statDiff * 3;
        } else {
            critChance = 20 + statDiff * 6;
        }
        if (attackType == 0) {
            // Quick attack
            critChance = (int)((double) critChance * 1.4);
        } else if (attackType == 2) {
            // Strong attack
            critChance = (int)((double) critChance * 0.25);
        }
        return trimValue(critChance, 0, 100);
    }

    public static boolean rollForHit(int thisDex, int otherDex,
                                     int thisStr, int otherStr, int attackType) {
        return genRandomNum(0, 100) <= getChanceToHit(thisDex, otherDex, thisStr, otherStr, attackType);
    }

    public static boolean rollForCrit(int thisDex, int otherDex, int attackType) {
        return genRandomNum(0, 100) <= getChanceToCrit(thisDex, otherDex, attackType);
    }
    public static int calcDamageAfterArmour(int hitVal, int armourVal) {
        int hitAmount = (int)(hitVal * (30. / (30. + armourVal)));
        return hitAmount > 0 ? hitAmount : 1;
    }

    public static int calcDamage(int thisDex, int otherDex, int thisStr, int otherStr,
                                     int attackType, boolean isCrit,
                                     boolean returnMinDmg, boolean returnMaxDmg) {
        boolean isDisadvantagedDex = thisDex < otherDex;
        boolean isDisadvantagedStr = thisStr < otherStr;
        double dmgMultiplier = isCrit ? 2.5 : 1.;
        int minDmg = -1;
        int maxDmg = -1;
        switch (attackType) {
            case 0 -> {
                if (!isDisadvantagedDex) {
                    minDmg = thisDex / 3;
                    maxDmg = thisDex / 2;
                } else {
                    minDmg = thisDex / 4;
                    maxDmg = thisDex / 3;
                }
            }
            case 1 -> {
                int betterofTwoStat = 0;
                boolean overwhelmingAdvantage = !(isDisadvantagedDex || isDisadvantagedStr);
                boolean overwhelmingDisadvantage = isDisadvantagedDex && isDisadvantagedStr;
                if (overwhelmingDisadvantage)
                    betterofTwoStat = Math.min(thisStr, thisDex);
                else if (!isDisadvantagedDex && isDisadvantagedStr)
                    betterofTwoStat = thisDex;
                else if (isDisadvantagedDex)
                    betterofTwoStat = thisStr;
                else
                    betterofTwoStat = Math.max(thisStr, thisDex);
                if (overwhelmingDisadvantage) {
                    minDmg = betterofTwoStat / 5;
                    maxDmg = betterofTwoStat / 4;
                } else if (overwhelmingAdvantage) {
                    minDmg = betterofTwoStat / 2;
                    maxDmg = betterofTwoStat;
                } else {
                    minDmg = betterofTwoStat / 3;
                    maxDmg = betterofTwoStat / 2;
                }
            }
            case 2 -> {
                if (!isDisadvantagedStr) {
                    minDmg = thisStr / 3;
                    maxDmg = thisStr / 2;
                } else {
                    minDmg = thisStr / 4;
                    maxDmg = thisStr / 3;
                }
            }
        }
        if (returnMinDmg)
            return minDmg;
        if (returnMaxDmg)
            return maxDmg;
        return (int)(genRandomNum(minDmg, maxDmg) * dmgMultiplier);
    }
    public static <T> void decreaseHealth(T obj, int byHowMuch) {
        if (!(obj instanceof Foe)) {
            PlayerClass.incrementPlayerStat("curHealth", -byHowMuch);
        } else {
            ((Foe)obj).increaseHealth(-byHowMuch);
        }
    }
    public static int trimValue(int curValue, int lowestAcceptable, int highestAcceptable) {
        if (curValue < lowestAcceptable)
            return lowestAcceptable;
        return Math.min(curValue, highestAcceptable);
    }

    public static String[] genAttackChoices(Foe combatant) {
        // thisDex and thisStr will always be the player's, as the enemy
        // doesn't need to see its options
        String[] choicez = new String[3];
        int[] minHitVals = new int[3];
        int[] maxHitVals = new int[3];
        int[] critChances = new int[3];
        int[] hitChances = new int[3];
        int thisDex = PlayerClass.getPlayerStat("Dexterity");
        int otherDex = combatant.getDexterity();
        int thisStr = PlayerClass.getPlayerStat("Strength");
        int otherStr = combatant.getStrength();
        int thisMinDmg;
        int thisMaxDmg;
        for (int i = 0; i < 3; i++) {
            thisMinDmg = calcDamageAfterArmour(calcDamage(thisDex, otherDex, thisStr, otherStr, i,
                    false, true, false), combatant.getArmour());
            thisMaxDmg = calcDamageAfterArmour(calcDamage(thisDex, otherDex, thisStr, otherStr, i,
                    false, false, true), combatant.getArmour());
            if (thisMaxDmg == 0) {
                thisMinDmg++;
                thisMaxDmg++;
            }
            minHitVals[i] = thisMinDmg;
            maxHitVals[i] = thisMaxDmg;
            critChances[i] = getChanceToCrit(thisDex, otherDex, i);
            hitChances[i] = getChanceToHit(thisDex, otherDex, thisStr, otherStr, i);
        }
        String[] typez = new String[]{"Quick", "Normal", "Heavy"};
        for (int i = 0; i < 3; i++) {
            choicez[i] = typez[i] + " attack: " + minHitVals[i] + "-" + maxHitVals[i] +
                    "; hit chance: " + hitChances[i] + "%; crit chance: " + critChances[i] + "%";
        }
        return choicez;
    }

    public static int genEnemyLevel(boolean matchLevel, int relativeOffset, int absoluteOffset) {
        int playerLevel = PlayerClass.getPlayerStat("playerLevel");
        if (matchLevel) {
            return playerLevel;
        } else if (relativeOffset != 0) {
            return playerLevel + relativeOffset;
        }
        return absoluteOffset;
    }
    public static int combatLoop(Foe combatant) {
        int atkChoice = -1;
        int thisDex = PlayerClass.getPlayerStat("Dexterity");
        int otherDex = combatant.getDexterity();
        int thisStr = PlayerClass.getPlayerStat("Strength");
        int otherStr = combatant.getStrength();
        boolean isAHit = false;
        boolean isACrit = false;
        int dmgDealt = -1;
        Map<String, Integer> foeMap = new LinkedHashMap<>();
        String enemyAtkFlavour = "";
        while (true) {
            StoryDisplayer.displayCombatants(combatant);
            System.out.println("\n>> How do you proceed?");
            atkChoice = StoryDisplayer.awaitChoiceInputFromOptions(genAttackChoices(combatant));
            isAHit = rollForHit(thisDex, otherDex, thisStr, otherStr, atkChoice);
            if (isAHit) {
                isACrit = rollForCrit(thisDex, otherDex, atkChoice);

                dmgDealt = calcDamageAfterArmour(calcDamage(thisDex, otherDex,
                        thisStr, otherStr, atkChoice, isACrit, false, false),
                        combatant.getArmour());
                combatant.increaseHealth(-dmgDealt);
                if (isACrit) {
                    System.out.println(">> You deal a dizzying critical blow to " + combatant.getName() +
                            " for " + dmgDealt + "!");
                } else {
                    System.out.println(">> You strike " + combatant.getName() + " for " + dmgDealt + "!");
                }
            } else {
                System.out.println(">> By luck, or by skill, " + combatant.getName() +
                        " evades your attack!");
            }
            StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
            foeMap.putAll(combatant.launchAttack());
            // attackType = 0 for quick, 1 for normal, 2 for strong
            // isCrit = 0 for non-crit, 1 for crit
            // attackHit = 0 or 1
            // damageOut = damage after having applied player's armour
            if (combatant.isDead())
                break;
            isAHit = foeMap.get("isHit") == 1;
            if (isAHit) {
                isACrit = foeMap.get("isCrit") == 1;
                enemyAtkFlavour = isACrit ? " fiercely" : "";
                enemyAtkFlavour += switch (foeMap.get("attackType")) {
                    case 0:
                        yield " strikes you with a quick jab ";
                    case 1:
                        yield " attacks you ";
                    case 2:
                        yield " launches a mighty swing at you ";
                    default:
                        yield " does something that results in an ERROR! ";
                };
                System.out.println(">> " + combatant.getName() + enemyAtkFlavour + "for " +
                        foeMap.get("damageOut") + " damage!");
                PlayerClass.incrementHealth(-foeMap.get("damageOut"));
                if (PlayerClass.checkForDeath(true)) {
                    return -1;
                }
            } else {
                System.out.println(">> You dodge " + combatant.getName() + "'s attack!");
            }
            StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
        }
        System.out.println(">> " + combatant.getName() + " lies dead before you. You have gained " +
                combatant.getXpYield() + "XP and now have " +
                (PlayerClass.getPlayerStat("curXP") + combatant.getXpYield()) +
                " out of " + PlayerClass.getPlayerStat("neededXP") +
                "XP needed to reach level " + (PlayerClass.getPlayerStat("playerLevel") + 1));
        if (combatant.getXpYield() + PlayerClass.getPlayerStat("curXP") >=
            PlayerClass.getPlayerStat("neededXP")){
            System.out.println(">> You level up!");
        }
        StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
        return combatant.getXpYield();
    }
}
