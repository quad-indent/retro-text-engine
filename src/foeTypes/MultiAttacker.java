package foeTypes;

import combat.Foe;

import java.util.Map;

public class MultiAttacker extends Foe {
    private int defaultNumAttackzPerTurn = 1;

    public int getDefaultNumAttackzPerTurn() {
        return defaultNumAttackzPerTurn;
    }

    public void setDefaultNumAttackzPerTurn(int defaultNumAttackzPerTurn) {
        this.defaultNumAttackzPerTurn = defaultNumAttackzPerTurn;
    }

    private int multiAttackNum = -1;

    public int getMultiAttackNum() {
        return multiAttackNum;
    }

    public void setMultiAttackNum(int multiAttackNum) {
        this.multiAttackNum = multiAttackNum;
    }

    public MultiAttacker(Map<String, String> argz) {
        super(argz);
        this.setMultiAttackNum(Integer.parseInt(argz.get("attacksWhenMultiAttacking")));
        this.setDefaultNumAttackzPerTurn(Integer.parseInt(argz.get("numAttacksPerTurn")));
    }
    @Override
    public String specialAttackPreProc() {
        super.setNumAttacksPerTurn(getMultiAttackNum());
        return super.parseSpecialAtkMsg();
    }
    @Override
    public void specialAttackPostProc() {
        super.setNumAttacksPerTurn(getDefaultNumAttackzPerTurn());
    }
}
