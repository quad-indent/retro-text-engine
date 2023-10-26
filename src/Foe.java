import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Foe {
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

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntellect() {
        return intellect;
    }

    public void setIntellect(int intellect) {
        this.intellect = intellect;
    }

    public int getArmour() {
        return armour;
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

    public int getCurMana() {
        return curMana;
    }

    public void setCurMana(int curMana) {
        this.curMana = curMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public void setArmour(int armour) {
        this.armour = armour;
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

    public Foe(String name, int level, int curHealth, int maxHealth, int mana, int maxMana,
               int xpYield, int strength, int dexterity, int intellect, int armour) {
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
    }

    public void specialAttack() {

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
        if (!CombatUtils.rollForHit(dexterity, PlayerClass.getPlayerStat("Dexterity"),
                strength, PlayerClass.getPlayerStat("Strength"), attackType)) {
            return attackInfo;
        } else {
            boolean isCrit = CombatUtils.rollForCrit(dexterity,
                    PlayerClass.getPlayerStat("Dexterity"), attackType);
            int dmgRaw = CombatUtils.calcDamage(dexterity, PlayerClass.getPlayerStat("Dexterity"),
                    strength, PlayerClass.getPlayerStat("Strength"), attackType, isCrit,
                    false, false);
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
        return (int)((double)PlayerClass.getPlayerStat("neededXP") / xpModifier);
    }
    public int genAttackType() {
        int[] weightz = new int[]{dexterity, CombatUtils.getMean(new int[]{strength, dexterity, intellect}),
                strength};
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
