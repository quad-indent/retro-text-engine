package foeTypes;

import combat.Foe;
import player.PlayerClass;

import java.util.LinkedHashMap;
import java.util.Map;

public class ArmourShredder extends Foe {
    private int oldPlayerArmour;
    private double armourShredVal;

    public double getArmourShredVal() {
        return armourShredVal;
    }

    public void setArmourShredVal(double armourShredVal) {
        this.armourShredVal = armourShredVal;
    }

    public int getOldPlayerArmour() {
        return oldPlayerArmour;
    }

    public void setOldPlayerArmour(int oldPlayerArmour) {
        this.oldPlayerArmour = oldPlayerArmour;
    }

    public ArmourShredder(Map<String, String> argz) {
        super(argz);
        this.armourShredVal = 100. * (Integer.parseInt(argz.get("armourShredVal")) / 100.);
    }
    @Override
    public String specialAttackPreProc() {
        setOldPlayerArmour(PlayerClass.getArmour());
        PlayerClass.setArmour((int)Math.round(PlayerClass.getArmour() * getArmourShredVal()));
        String thisMsg = super.parseSpecialAtkMsg();
        thisMsg = thisMsg.replaceAll("decreasedArmour ", String.valueOf(PlayerClass.getArmour()));
        thisMsg = thisMsg.replaceAll("normalArmour", String.valueOf(getOldPlayerArmour()));
        return thisMsg;
    }
    @Override
    public void specialAttackPostProc() {
        PlayerClass.setArmour(getOldPlayerArmour());
    }
}
