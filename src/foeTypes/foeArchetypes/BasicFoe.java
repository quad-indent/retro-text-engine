package foeTypes.foeArchetypes;

import combat.Foe;

import java.util.Map;

public class BasicFoe extends Foe {
    public BasicFoe(Map<String, String> argz) {
        super(argz);
        super.setSpecialAttackChance(0); // just to be sure
    }
    @Override
    public String specialAttackPreProc() {
        return null;
    }
    @Override
    public void specialAttackPostProc() {

    }
}
