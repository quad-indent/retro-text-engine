package foeTypes;

import combat.CombatUtils;
import combat.Foe;
import storyBits.GlobalConf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiTalented extends Foe {
    private List<Foe> talentz;
    private int currentMimic;

    public int getCurrentMimic() {
        return currentMimic;
    }
    public void setCurrentMimic(int currentMimic) {
        this.currentMimic = currentMimic;
    }
    public List<Foe> getTalentz() {
        return talentz;
    }

    public MultiTalented(Map<String, String> argz) throws Exception {
        super(argz);
        talentz = new ArrayList<>();
        if (argz.containsKey("multiAttackerMessage")) {
            MultiAttacker multiAtkTalent = new MultiAttacker(prepareBespokeMap(argz, "multiattacker"));
            getTalentz().add(multiAtkTalent);
        }
        if (argz.containsKey("armourShredderMessage")) {
            ArmourShredder armShredTalent = new ArmourShredder(prepareBespokeMap(argz, "armourshredder"));
            getTalentz().add(armShredTalent);
        }
        if (argz.containsKey("armourBoosterMessage")) {
            ArmourBooster armBoostTalen = new ArmourBooster(prepareBespokeMap(argz, "armourbooster"));
            getTalentz().add(armBoostTalen);
        }
    }

    private Map<String, String> prepareBespokeMap(Map<String, String> argz, String specificTalentType) throws Exception {
        Map<String, String> returnal = new LinkedHashMap<>(argz);
        switch (specificTalentType.toLowerCase()) {
            case "multiattacker":
                returnal.put("specialAttackMessage", returnal.get("multiAttackerMessage"));
                break;
            case "armourbooster":
                returnal.put("specialAttackMessage", returnal.get("armourBoosterMessage"));
                break;
            case "armourshredder":
                returnal.put("specialAttackMessage", returnal.get("armourShredderMessage"));
                break;
            default:
                GlobalConf.issueLog("Could not retrieve corresponding value whilst " +
                        "initialising a MultiTalented foe!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        return returnal;
    }
    @Override
    public String specialAttackPreProc() {
        int choice = CombatUtils.genRandomNum(0, getTalentz().size() - 1);
        this.setCurrentMimic(choice);
        getTalentz().get(choice).mimicAnother(this);
        getTalentz().get(choice).specialAttackPreProc();
        super.mimicAnother(getTalentz().get(choice));
        return getTalentz().get(choice).parseSpecialAtkMsg();
    }
    @Override
    public void specialAttackPostProc() {
        int choice = this.getCurrentMimic();
        getTalentz().get(choice).specialAttackPostProc();
        this.mimicAnother(getTalentz().get(choice));
        this.setCurrentMimic(-1);
    }
}