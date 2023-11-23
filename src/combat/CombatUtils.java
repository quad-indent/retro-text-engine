package combat;

import inventory.Inventory;
import inventory.WeaponItem;
import player.PlayerKeywordz;
import storyBits.GlobalConf;
import storyBits.StoryDisplayer;
import player.PlayerClass;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
public class CombatUtils {

    public static int calcDamageShieldBlocked() {
        if (Arrays.stream(Inventory.getEquippedWeapons()).filter(Objects::nonNull).noneMatch(WeaponItem::isShield)) {
            return 0;
        }
        int minBlock = Arrays.stream(Inventory.getEquippedWeapons())
                .filter(it -> it != null && it.isShield()).mapToInt(WeaponItem::getMinDmg).sum();
        int maxBlock = Arrays.stream(Inventory.getEquippedWeapons())
                .filter(it -> it != null && it.isShield()).mapToInt(WeaponItem::getMaxDmg).sum();
        return genRandomNum(minBlock, maxBlock);
    }
    public static int scaleWepDmg(int dmgVal, String scalingStat) {
        int valToScale = PlayerClass.getPlayerStat(scalingStat);
        double returnal = dmgVal * Math.log10(10 + 8 * (valToScale - 10));
        return (int) Math.round(returnal);
    }
    public static int scaleWepDmg(int dmgVal, WeaponItem wep) {
        return scaleWepDmg(dmgVal, wep.getScalingStat());
    }
    public static int genRandomNum(int minOut, int maxOut) {
        if (minOut >= maxOut)
            return minOut;
        return ThreadLocalRandom.current().nextInt(minOut, maxOut + 1);
    }
    public static int genRandomNumWeighted(int minOut, int maxOut, int[] qWeightz) throws AssertionError, Exception {
        // the weightz are expected to be {chance for lowest quartile of range, chance for 2nd, 3rd, and top quartile}
        // thirds or halves can be used as well, and regardless they need to sum up to 100
        if (qWeightz.length > 4) {
            GlobalConf.issueLog("Up to four values for the weight quarters " +
                    "expected, got " + qWeightz.length + "!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
        } else if (Arrays.stream(qWeightz).sum() != 100) {
            GlobalConf.issueLog("The weights need to sum to 100! " +
                    "(exclamation point, not factorial)", GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        if (minOut == maxOut)
            return minOut;
        if (maxOut - minOut == 1) {
            if (qWeightz.length < 3) {
                GlobalConf.issueLog("Mismatch of qWeightz!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
            }
            // 2 possibilities
            if (qWeightz[2] > 0 && qWeightz[3] > 0) {
                qWeightz = new int[]{qWeightz[0] + qWeightz[1], qWeightz[2] + qWeightz[3]};
            } else {
                qWeightz = new int[]{qWeightz[0], qWeightz[1]};
            }
        } else if (maxOut - minOut == 2) {
            if (qWeightz.length < 4) {
                GlobalConf.issueLog("Mismatch of qWeightz!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
            }
            // 3 possibilities
            if (qWeightz[3] > 0) {
                double tempie = (double) qWeightz[3] / 3;
                for (int i = 0; i < 3; i++) {
                    qWeightz[i] += (int) tempie;
                }
                int tempCtr = 0;
                while (Arrays.stream(qWeightz).sum() != 100) {
                    if (Arrays.stream(qWeightz).sum() > 100)
                        qWeightz[tempCtr++]--;
                    else
                        qWeightz[tempCtr++]++;
                    if (tempCtr > 2)
                        tempCtr = 0;
                }
            }
            qWeightz = new int[]{qWeightz[0], qWeightz[1], qWeightz[3]};
        }
        if (qWeightz.length == 0) {
            GlobalConf.issueLog("qWeightz empty!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        int qRoll = genRandomNum(0, 100);
        int qPicked = qWeightz.length-1;
        for (; qPicked >= 0; qPicked--) {
            qRoll -= qWeightz[qPicked];
            if (qRoll <= 0)
                break;
        }
        int outRange = maxOut - minOut;
        double qRange = (double) outRange / qWeightz.length;
        // qRange = Math.max(qRange, 1);
        int rMin = (int) (minOut + qPicked * qRange);
        int rMax = (int) (minOut + qPicked * qRange + qRange);
        int resultar = genRandomNum(rMin, rMax);
        return Math.min(resultar, rMax);
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
        int hitAmount = (int)Math.round(hitVal * (30. / (30. + armourVal)));
        return hitAmount > 0 ? hitAmount : 1;
    }

    public static int calcDamage(int thisDex, int otherDex, int thisStr, int otherStr,
                                     int attackType, boolean isCrit,
                                     boolean returnMinDmg, boolean returnMaxDmg) throws Exception {
        // 0 - quick
        // 1 - normal
        // 2 - strong
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

        int minDmgBoon = Inventory.getEquippedWeaponBoonz(true, true);
        int maxDmgBoon = Inventory.getEquippedWeaponBoonz(false, true);

        if (returnMinDmg)
            return minDmg + minDmgBoon;
        if (returnMaxDmg)
            return maxDmg + maxDmgBoon;
        int[] rollWeightz;
        rollWeightz = switch (attackType) {
            case 0:
                yield new int[]{45, 15, 5, 35};
            case 1:
                yield new int[]{28, 24, 20, 28};
            case 2:
                yield new int[]{10, 30, 45, 15};
            default:
                yield new int[]{25, 25, 25, 25};
        };
        // hidden chances of whether dmg output will be closer to lowest or highest possibility
        int boonRoll = genRandomNumWeighted(minDmg + minDmgBoon, maxDmg + maxDmgBoon, rollWeightz);
        return (int)(boonRoll * dmgMultiplier);
    }
    public static <T> void decreaseHealth(T obj, int byHowMuch) throws Exception {
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

    public static String[] genAttackChoices(Foe combatant) throws Exception {
        // thisDex and thisStr will always be the player's, as the enemy
        // doesn't need to see its options
        String[] choicez = new String[3];
        int[] minHitVals = new int[3];
        int[] maxHitVals = new int[3];
        int[] critChances = new int[3];
        int[] hitChances = new int[3];
        int thisDex = PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName());
        int otherDex = combatant.getDexterity();
        int thisStr = PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName());
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
    public static int combatLoop(Foe combatant) throws Exception {
        int atkChoice = -1;
        int thisDex = PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName());
        int otherDex = combatant.getDexterity();
        int thisStr = PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName());
        int otherStr = combatant.getStrength();
        boolean isAHit = false;
        boolean isEnemySpecialAttacking = false;
        Map<String, Integer> foeMap = new LinkedHashMap<>();
        while (true) {
            StoryDisplayer.displayCombatants(combatant);
            System.out.println("\n" + GlobalConf.getStoryTextPrefix() + "How do you proceed?");
            atkChoice = StoryDisplayer.awaitChoiceInputFromOptions(genAttackChoices(combatant));
            isAHit = rollForHit(thisDex, otherDex, thisStr, otherStr, atkChoice);
            if (isAHit) {
                processHit(combatant, thisDex, otherDex, atkChoice, thisStr, otherStr);
            } else {
                System.out.println(GlobalConf.getStoryTextPrefix() + "By luck, or by skill, " + combatant.getName() +
                        " evades your attack!");
            }
            StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
            if (combatant.isDead())
                break;
            if (isEnemySpecialAttacking) {
                // If special attack debuffs player str/dex/int,
                // or raises combatant's armour, let player's attack go thru first
                // no harm either if special attack is purely offensive
                combatant.specialAttackPostProc();
                isEnemySpecialAttacking = false;
            }
            if (genRandomNum(1, 100) <= combatant.getSpecialAttackChance()) {
                isEnemySpecialAttacking = true;
                System.out.println(GlobalConf.getStoryTextPrefix() + combatant.specialAttackPreProc());
            }
            for (int i = 0; i < combatant.getNumAttacksPerTurn(); i++) {
                foeMap.putAll(combatant.launchAttack());
                // attackType = 0 for quick, 1 for normal, 2 for strong
                // isCrit = 0 for non-crit, 1 for crit
                // attackHit = 0 or 1
                // damageOut = damage after having applied player's armour
                isAHit = foeMap.get("isHit") == 1;
                if (isAHit) {
                    if (processEnemyHit(foeMap, combatant) == -1) {
                        return -1;
                    }
                } else {
                    System.out.println(GlobalConf.getStoryTextPrefix() + "You dodge " +
                            combatant.getName() + "'s attack!");
                }
            }
            StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
        }
        System.out.println(GlobalConf.getStoryTextPrefix() + combatant.getName() +
                " lies dead before you. You have gained " +
                combatant.getXpYield() + " " + PlayerKeywordz.getxPAbbr() + " and now have " +
                (PlayerClass.getPlayerStat("curXP") + combatant.getXpYield()) +
                " out of " + PlayerClass.getPlayerStat("neededXP") + " " + PlayerKeywordz.getxPAbbr() +
                " needed to reach " + PlayerKeywordz.getLevelName().toLowerCase() + " " +
                (PlayerClass.getPlayerStat("playerLevel") + 1));
        System.out.println(GlobalConf.getStoryTextPrefix() +
                combatant.getName() + " dropped " + combatant.getGoldDrop() + " " +
                PlayerKeywordz.getCurrencyName().toLowerCase() + "!");
        PlayerClass.incrementPlayerCurrency(combatant.getGoldDrop(), true);
        PlayerClass.refillHealthAndMana();
        if (combatant.getXpYield() + PlayerClass.getPlayerStat("curXP") >=
            PlayerClass.getPlayerStat("neededXP")){
            System.out.println(GlobalConf.getStoryTextPrefix() +
                    "You level up!");
        }
        StoryDisplayer.awaitChoiceInputFromOptions(new String[]{"Continue"});
        return combatant.getXpYield();
    }
    public static void processHit(Foe combatant, int thisDex, int otherDex, int atkChoice,
                                  int thisStr, int otherStr) throws Exception {
        boolean isACrit = rollForCrit(thisDex, otherDex, atkChoice);

        int dmgDealt = calcDamageAfterArmour(calcDamage(thisDex, otherDex,
                        thisStr, otherStr, atkChoice, isACrit, false, false),
                combatant.getArmour());
        combatant.increaseHealth(-dmgDealt);
        if (isACrit) {
            System.out.println(GlobalConf.getStoryTextPrefix() +
                    "You deal a dizzying critical blow to " + combatant.getName() +
                    " for " + dmgDealt + "!");
        } else {
            System.out.println(GlobalConf.getStoryTextPrefix() +
                    "You strike " + combatant.getName() + " for " + dmgDealt + "!");
        }
    }
    public static int processEnemyHit(Map<String, Integer> foeMap, Foe combatant) throws Exception {
        boolean isACrit = foeMap.get("isCrit") == 1;
        String enemyAtkFlavour = combatant.getCombatMessagez()[foeMap.get("attackType") + (isACrit ? 3 : 0)];
        enemyAtkFlavour = enemyAtkFlavour.replaceAll("foeName", combatant.getName());
        enemyAtkFlavour = enemyAtkFlavour.replaceAll("damageOut", String.valueOf(foeMap.get("damageOut")));
        String dmgText = GlobalConf.getStoryTextPrefix() + enemyAtkFlavour;
        if (foeMap.get("damageBlocked") != 0) {
            dmgText += " (" + foeMap.get("damageBlocked") + " blocked)";
        }
        System.out.println(dmgText);
        PlayerClass.incrementHealth(-foeMap.get("damageOut"));
        if (PlayerClass.checkForDeath(false)) {
            return -1;
        }
        return 0;
    }
}
