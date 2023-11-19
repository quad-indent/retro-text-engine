package foeTypes.foeArchetypes;

import combat.Foe;
import player.PlayerClass;

import java.util.Map;

public class ArmourBooster extends Foe {
    private int oldSelfArmour;
    private double armourBoostVal;

    public int getOldSelfArmour() {
        return oldSelfArmour;
    }

    public void setOldSelfArmour(int oldSelfArmour) {
        this.oldSelfArmour = oldSelfArmour;
    }

    public double getArmourBoostVal() {
        return armourBoostVal;
    }

    public void setArmourBoostVal(double armourBoostVal) {
        this.armourBoostVal = armourBoostVal;
    }

    public ArmourBooster(Map<String, String> argz) {
        super(argz);
        this.armourBoostVal = 100. * (Integer.parseInt(argz.get("armourBoostVal")) / 100.);
    }
    @Override
    public String specialAttackPreProc() {
        setOldSelfArmour(super.getArmour());
        super.setArmour((int)Math.round(super.getArmour() * getArmourBoostVal()));
        String thisMsg = super.parseSpecialAtkMsg();
        thisMsg = thisMsg.replaceAll("increasedArmour ", String.valueOf(super.getArmour()));
        thisMsg = thisMsg.replaceAll("normalArmour", String.valueOf(getOldSelfArmour()));
        return thisMsg;
    }
    @Override
    public void specialAttackPostProc() {
        super.setArmour(getOldSelfArmour());
    }
}
