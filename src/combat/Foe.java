package combat;

import foeTypes.Automaton;
import foeTypes.Goblin;
import player.PlayerClass;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Foe {
    public static final Map<String, Class<? extends Foe>> foeMap;
    private String name;
    private int level;
    private int curHealth;
    private int maxHealth;
    private int curMana;
    private int maxMana;
    private int xpYield;
    private int strength;
    private int dexterity;
    private int intellect;
    private int armour;
    private int numAttacksPerTurn;
    private int specialAttackChance;
    private int goldDrop;
    public int getSpecialAttackChance() {
        return specialAttackChance;
    }
    public void setSpecialAttackChance(int specialAttackChance) {
        this.specialAttackChance = specialAttackChance;
    }
    public int getNumAttacksPerTurn() {
        return numAttacksPerTurn;
    }
    public void setNumAttacksPerTurn(int numAttacksPerTurn) {
        this.numAttacksPerTurn = numAttacksPerTurn;
    }
    public int getGoldDrop() {
        return goldDrop;
    }
    public void setGoldDrop(int goldDrop) {
        this.goldDrop = goldDrop;
    }
    public void generateGoldDrop(int baseVal, int lowRandomLvMul, int highRandomLvMul) {
        this.setGoldDrop(baseVal + CombatUtils.genRandomNum(getLevel() * lowRandomLvMul, getLevel() * highRandomLvMul));
    }
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getcurMana() {
        return curMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getXpYield() {
        return xpYield;
    }

    public void setXpYield(int xpYield) {
        this.xpYield = xpYield;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void generateStrength(int baseVal, int levelDivisor, int randomLow, int randomHighLevelDivisor) {
        this.setStrength(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(randomLow, getLevel() / randomHighLevelDivisor));
    }
    public void generateStrength(int baseVal, double levelDivisor, int randomLow, double randomHighLevelDivisor) {
        this.setStrength(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(randomLow, (int)Math.round(getLevel() / randomHighLevelDivisor)));
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    public void generateDexterity(int baseVal, int levelDivisor, int randomLow, int randomHighLevelDivisor) {
        this.setDexterity(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(randomLow, getLevel() / randomHighLevelDivisor));
    }
    public void generateDexterity(int baseVal, double levelDivisor, int randomLow, double randomHighLevelDivisor) {
        this.setDexterity(baseVal + genLevelDivisorVal(levelDivisor) +
                        CombatUtils.genRandomNum(randomLow, (int)Math.round(getLevel() / randomHighLevelDivisor)));
    }
    public int getIntellect() {
        return intellect;
    }

    public void setIntellect(int intellect) {
        this.intellect = intellect;
    }
    public void generateIntellect(int baseVal, int levelDivisor, int randomLow, int randomHighLevelDivisor) {
        this.setIntellect(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(randomLow, getLevel() / randomHighLevelDivisor));
    }
    public void generateIntellect(int baseVal, double levelDivisor, int randomLow, double randomHighLevelDivisor) {
        this.setIntellect(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(randomLow, (int)Math.round(getLevel() / randomHighLevelDivisor)));
    }
    public int getArmour() {
        return armour;
    }
    public void setArmour(int armour) {
        this.armour = armour;
    }
    public void generateArmour(int baseVal, int levelDivisor, int randomLowLvDivisor, int randomHighLevelMul) {
        this.setArmour(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(genLevelDivisorVal(randomLowLvDivisor), getLevel() * randomHighLevelMul));
    }
    public void generateArmour(int baseVal, double levelDivisor, double randomLowLvDivisor, double randomHighLevelMul) {
        this.setArmour(baseVal + genLevelDivisorVal(levelDivisor) +
                CombatUtils.genRandomNum(genLevelDivisorVal(randomLowLvDivisor), (int)Math.round(getLevel() * randomHighLevelMul)));
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setCurHealth(int curHealth) {
        this.curHealth = curHealth;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void generateMaxHealth(int baseVal, int levelMulLow, int levelMulHigh) {
        this.setMaxHealth(baseVal + getLevel() * CombatUtils.genRandomNum(levelMulLow, levelMulHigh));
        this.setCurHealth(getMaxHealth());
    }
    public int getCurMana() {
        return curMana;
    }
    public void setCurMana(int curMana) {
        this.curMana = curMana;
    }
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    public boolean isDead() { return curHealth <= 0; }
    public void increaseHealth(int increaseBy) {
        this.curHealth += increaseBy;
        if (this.curHealth > this.maxHealth) {
            this.curHealth = this.maxHealth;
        } else {
            this.curHealth = Math.max(0, this.curHealth);
        }
    }
    static {
        foeMap = new LinkedHashMap<>();
        foeMap.put("goblin", Goblin.class);
        foeMap.put("automaton", Automaton.class);
    }
    public Foe(String name, int level, int curHealth, int maxHealth, int mana, int maxMana,
               int xpYield, int strength, int dexterity, int intellect, int armour, int goldDrop,
               int numAttacksPerTurn, int specialAttackChance) {
        this.name = name;
        this.level = level;
        this.curHealth = curHealth;
        this.maxHealth = maxHealth;
        this.curMana = mana;
        this.maxMana = maxMana;
        this.xpYield = xpYield;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intellect = intellect;
        this.armour = armour;
        this.goldDrop = goldDrop;
        this.numAttacksPerTurn = numAttacksPerTurn;
        this.specialAttackChance = specialAttackChance;
    }
    public abstract String specialAttackPreProc();
    public abstract void specialAttackPostProc();
    protected int genLevelDivisorVal(int levelDivisor) {
        return levelDivisor == 0 ? 0 : getLevel() / levelDivisor;
    }
    protected int genLevelDivisorVal(double levelDivisor) {
        return levelDivisor == 0 ? 0 : (int)Math.round(getLevel() / levelDivisor);
    }
    public Map<String, Integer> launchAttack() {
        // attackType = 0 for quick, 1 for normal, 2 for strong
        // isCrit = 0 for non-crit, 1 for crit
        // attackHit = 0 or 1
        // damageOut = damage after having applied player's armour
        Map<String, Integer> attackInfo = new LinkedHashMap<String, Integer>();
        int attackType = genAttackType();
        attackInfo.put("isCrit", 0);
        attackInfo.put("isHit", 0);
        attackInfo.put("damageOut", 0);
        attackInfo.put("attackType", attackType);
        attackInfo.put("damageBlocked", 0);
        if (!CombatUtils.rollForHit(getDexterity(), PlayerClass.getPlayerStat("Dexterity"),
                getStrength(), PlayerClass.getPlayerStat("Strength"), attackType)) {
            return attackInfo;
        } else {
            boolean isCrit = CombatUtils.rollForCrit(getDexterity(),
                    PlayerClass.getPlayerStat("Dexterity"), attackType);
            int dmgRaw = CombatUtils.calcDamage(getDexterity(), PlayerClass.getPlayerStat("Dexterity"),
                    getStrength(), PlayerClass.getPlayerStat("Strength"), attackType, isCrit,
                    false, false);
            attackInfo.put("damageBlocked", CombatUtils.calcDamageShieldBlocked(dmgRaw));
            dmgRaw -= attackInfo.get("damageBlocked");
            dmgRaw = CombatUtils.calcDamageAfterArmour(dmgRaw, PlayerClass.getPlayerStat("armour"));
            attackInfo.put("damageOut", dmgRaw);
            attackInfo.put("isCrit", isCrit ? 1 : 0);
            attackInfo.put("isHit", 1);
        }
        return attackInfo;
    }
    public static int genXPYield(int selfLevel) {
        double xpModifier = (5 + PlayerClass.getPlayerStat("playerLevel") * 0.5 -
                (PlayerClass.getPlayerStat("playerLevel") - selfLevel) * 0.6);
        xpModifier = Math.max(0.2, xpModifier);
        return (int)((double) PlayerClass.getPlayerStat("neededXP") / xpModifier);
    }
    public int genAttackType() {
        int[] weightz = new int[]{getDexterity(), CombatUtils
                .getMean(new int[]{getStrength(), getDexterity(), getIntellect()}),
                getStrength()};
        int rngRes = CombatUtils.genRandomNum(0, Arrays.stream(weightz).sum());
        int attackType = 2;
        for (; attackType>=0; attackType--) {
            rngRes -= weightz[attackType];
            if (rngRes <= 0)
                break;
        }
        return attackType;
    }
}
