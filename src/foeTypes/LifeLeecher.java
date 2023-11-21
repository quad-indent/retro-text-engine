package foeTypes;

import combat.CombatUtils;
import combat.Foe;
import player.PlayerClass;
import player.PlayerKeywordz;
import storyBits.GlobalConf;

import java.util.LinkedHashMap;
import java.util.Map;

public class LifeLeecher extends Foe {
    private int lifeLeechPercentage;

    public int getLifeLeechPercentage() {
        return lifeLeechPercentage;
    }

    public void setLifeLeechPercentage(int lifeLeechPercentage) {
        this.lifeLeechPercentage = lifeLeechPercentage;
    }

    public LifeLeecher(Map<String, String> argz) {
        super(argz);
        this.setLifeLeechPercentage(Integer.parseInt(argz.get("lifeLeechPercentage")));
    }
    @Override
    public String specialAttackPreProc() throws Exception {
        int suckedDamageMin = CombatUtils.calcDamage(getDexterity(),
                PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()),
                getStrength(), PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName()), 1, false,
                true, false);
        int suckedDamageMax = CombatUtils.calcDamage(getDexterity(),
                PlayerClass.getPlayerStat(PlayerKeywordz.getDexterityName()),
                getStrength(), PlayerClass.getPlayerStat(PlayerKeywordz.getStrengthName()), 1, false,
                false, true);
        int finalSuck = (int) Math.round(CombatUtils.genRandomNum(suckedDamageMin, suckedDamageMax) *
                getLifeLeechPercentage() / 100.);
        increaseHealth(finalSuck);
        super.getMessageKeysMap().put("lifeLeeched", String.valueOf(finalSuck));
        return super.parseSpecialAtkMsg();
    }
    @Override
    public void specialAttackPostProc() {

    }
}
