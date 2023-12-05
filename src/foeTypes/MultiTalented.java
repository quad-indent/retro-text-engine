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
            ArmourBooster armBoostTalent = new ArmourBooster(prepareBespokeMap(argz, "armourbooster"));
            getTalentz().add(armBoostTalent);
        }
        if (argz.containsKey("lifeLeechPercentage")) {
            LifeLeecher leechTalent = new LifeLeecher(prepareBespokeMap(argz, "lifeleecher"));
            getTalentz().add(leechTalent);
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
            case "lifeleecher":
                returnal.put("specialAttackMessage", returnal.get("lifeLeecherMessage"));
                break;
            default:
                GlobalConf.issueLog("Could not retrieve corresponding value whilst " +
                        "initialising a MultiTalented foe!", GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        return returnal;
    }
    @Override
    public String specialAttackPreProc() throws Exception {
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
        getTalentz().get(choice).setCurHealth(this.getCurHealth());
        getTalentz().get(choice).setCurMana(this.getCurMana());
        // otherwise damage wouldn't really be dealt to the enemy
        getTalentz().get(choice).specialAttackPostProc();
        this.mimicAnother(getTalentz().get(choice));
        this.setCurrentMimic(-1);
    }
}