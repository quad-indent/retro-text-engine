package combat;

import player.PlayerClass;
import player.PlayerKeywordz;
import storyBits.GlobalConf;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base Foe class. Contains:<br>
 * * name:String<br>
 * * level:int<br>
 * * curHealth:int<br>
 * * maxHealth:int<br>
 * * curMana:int<br>
 * * maxMana:int<br>
 * * xpYield:int<br>
 * * strength:int<br>
 * * dexterity:int<br>
 * * armour:int<br>
 * * numAttacksPerTurn:int<br>
 * * specialAttackChance:int<br>
 * * goldDrop:int<br>
 * * specialAttackMsg:String<br>
 * * combatMessagez:String[]
 */
public abstract class Foe {
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
    private String specialAttackMsg;
    private String[] combatMessagez;

    public String[] getCombatMessagez() {
        return combatMessagez;
    }

    private Map<String, String> messageKeysMap = new LinkedHashMap<>();

    public Map<String, String> getMessageKeysMap() {
        return messageKeysMap;
    }
    public String getSpecialAttackMsg() {
        return specialAttackMsg;
    }

    public void setSpecialAttackMsg(String specialAttackMsg) {
        this.specialAttackMsg = specialAttackMsg;
    }

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
    public Foe (Map<String, String> argz) {
        this.combatMessagez = new String[6];
        this.name = argz.get("name");
        this.level = Integer.parseInt(argz.get("level"));
        this.curHealth = Integer.parseInt(argz.get("health"));
        this.maxHealth = Integer.parseInt(argz.get("health"));
        this.curMana = Integer.parseInt(argz.get("mana"));
        this.maxMana = Integer.parseInt(argz.get("mana"));
        this.xpYield = argz.get("experience yield").equalsIgnoreCase("default") ?
                Foe.genXPYield(this.level) :
                Integer.parseInt(argz.get("experience yield"));
        this.strength = Integer.parseInt(argz.get(PlayerKeywordz.getStrengthName().toLowerCase()));
        this.dexterity = Integer.parseInt(argz.get(PlayerKeywordz.getDexterityName().toLowerCase()));
        this.intellect = Integer.parseInt(argz.get(PlayerKeywordz.getIntellectName().toLowerCase()));
        this.armour = Integer.parseInt(argz.get("armour"));
        this.goldDrop = Integer.parseInt(argz.get(PlayerKeywordz.getCurrencyName().toLowerCase() + " drop"));
        this.numAttacksPerTurn = Integer.parseInt(argz.get("numAttacksPerTurn"));
        this.specialAttackChance = Integer.parseInt(argz.get("specialAttackChance"));
        this.specialAttackMsg = argz.getOrDefault("specialAttackMessage", "");
        this.combatMessagez[0] = argz.get("quickAttackMessage");
        this.combatMessagez[1] = argz.get("normalAttackMessage");
        this.combatMessagez[2] = argz.get("strongAttackMessage");
        this.combatMessagez[3] = argz.get("crit_quickAttackMessage");
        this.combatMessagez[4] = argz.get("crit_normalAttackMessage");
        this.combatMessagez[5] = argz.get("crit_strongAttackMessage");
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

    /**
     * Copies another foe's stats. Useful for MultiTalented child
     */
    public void mimicAnother(Foe another) {
        this.name = another.getName();
        this.level = another.getLevel();
        this.curHealth = another.getCurHealth();
        this.maxHealth = another.getMaxHealth();
        this.curMana = another.getCurMana();
        this.maxMana = another.getMaxMana();
        this.xpYield = another.getXpYield();
        this.strength = another.getStrength();
        this.dexterity = another.getDexterity();
        this.intellect = another.getIntellect();
        this.armour = another.getArmour();
        this.goldDrop = another.getGoldDrop();
        this.numAttacksPerTurn = another.getNumAttacksPerTurn();
        this.specialAttackChance = another.getSpecialAttackChance();
    }

    /**
     * To be used to buff its stats as part of special attack, e.g. to raise num of attacks per turn to 3 from 1
     */
    public abstract String specialAttackPreProc() throws Exception;

    /**
     * To be used to bring foe to its "default" state after special attack has happened. This method is called
     * in combatloop right after player's attack and right before the foe's turn
     */
    public abstract void specialAttackPostProc();
    protected int genLevelDivisorVal(int levelDivisor) {
        return levelDivisor == 0 ? 0 : getLevel() / levelDivisor;
    }
    protected int genLevelDivisorVal(double levelDivisor) {
        return levelDivisor == 0 ? 0 : (int)Math.round(getLevel() / levelDivisor);
    }
    public String parseSpecialAtkMsg() {
        String thisMsg = getSpecialAttackMsg();
        thisMsg = thisMsg.replaceAll("foeName", this.getName());
        for (Map.Entry<String, String> curEntry: getMessageKeysMap().entrySet()) {
            thisMsg = thisMsg.replaceAll(curEntry.getKey(), curEntry.getValue());
        }
        return thisMsg;
    }

    /**
     *
     * @return a map of valz:<br>
     * <ul>
     *  <li>"isCrit": 0: non-crit; 1: crit</li>
     *  <li>"isHit"</b>: 0: miss; 1: hit</li>
     *  <li>"damageOut": final mitigated damage, first by shieldz then by armour</li>
     *  <li>"damageBlocked": how much damage was absorbed by shieldz</li>
     *  <li>"attackType": 0: quick; 1: normal; 2: special</li>
     * </ul>
     */
    public Map<String, Integer> launchAttack() throws Exception {
        // attackType = 0 for quick, 1 for normal, 2 for strong
        // isCrit = 0 for non-crit, 1 for crit
        // attackHit = 0 or 1
        // damageOut = damage after having applied player's armour
        Map<String, Integer> attackInfo = new LinkedHashMap<>();
        int attackType = genAttackType();
        attackInfo.put("isCrit", 0);
        attackInfo.put("isHit", 0);
        attackInfo.put("damageOut", 0);
        attackInfo.put("attackType", attackType);
        attackInfo.put("damageBlocked", 0);
        if (!CombatUtils.rollForHit(getDexterity(), PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()),
                getStrength(), PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName()), attackType)) {
            return attackInfo;
        } else {
            boolean isCrit = CombatUtils.rollForCrit(getDexterity(),
                    PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()), attackType);
            int dmgRaw = CombatUtils.calcDamage(getDexterity(), PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()),
                    getStrength(), PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName()), attackType, isCrit,
                    false, false);
            attackInfo.put("damageBlocked", CombatUtils.calcDamageShieldBlocked());
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

    /**
     * Returns randomised enemy attack type that favours its highest stat
     */
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
