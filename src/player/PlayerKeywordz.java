package player;

import storyBits.FileParser;
import storyBits.GlobalConf;
import storyBits.StoryDisplayer;

import java.util.*;
import java.util.stream.Collectors;
public class PlayerKeywordz {
    private static String levelName = "Level";
    private static String xPName = "Experience";
    private static String xPAbbr = "XP";
    private static String healthName = "Health";
    private static String manaName = "Mana";
    private static String strengthName = "Strength";
    private static String strengthDesc = "";
    private static String dexterityName = "Dexterity";
    private static String dexteritDesc = "";
    private static String intellectName = "Intellect";
    private static String intellectDesc = "";
    private static String currencyName = "Gold";
    private static String strAbbr = "STR";
    private static String dexAbbr = "DEX";
    private static String intAbbr = "INT";
    private static String keenEyeName = "Keen Eye";
    private static String []minorStats = new String[6];
    private static String []minorStatDescs = new String[6];
    public static int numMinorStatz;

    public static String getxPAbbr() {
        return xPAbbr;
    }

    public static void setxPAbbr(String xPAbbr) {
        PlayerKeywordz.xPAbbr = xPAbbr;
    }

    public static String getStrAbbr() {
        return strAbbr;
    }

    public static void setStrAbbr(String strAbbr) {
        PlayerKeywordz.strAbbr = strAbbr;
    }

    public static String getDexAbbr() {
        return dexAbbr;
    }

    public static void setDexAbbr(String dexAbbr) {
        PlayerKeywordz.dexAbbr = dexAbbr;
    }

    public static String getIntAbbr() {
        return intAbbr;
    }

    public static void setIntAbbr(String intAbbr) {
        PlayerKeywordz.intAbbr = intAbbr;
    }

    public static String getxPName() {
        return xPName;
    }

    public static void setxPName(String xPName) {
        PlayerKeywordz.xPName = xPName;
    }

    public static String[] getMinorStatDescs() {
        return minorStatDescs;
    }

    public static void setMinorStatDescs(String[] minorStatDescs) {
        PlayerKeywordz.minorStatDescs = minorStatDescs;
    }

    public static int getNumMinorStatz() {
        return numMinorStatz;
    }

    public static void setNumMinorStatz(int numMinorStatz) {
        PlayerKeywordz.numMinorStatz = numMinorStatz;
    }

    public static String getCurrencyName() {
        return currencyName;
    }

    public static void setCurrencyName(String currencyName) {
        PlayerKeywordz.currencyName = currencyName;
    }

    public static String getLevelName() {
        return levelName;
    }

    public static void setLevelName(String levelName) {
        PlayerKeywordz.levelName = levelName;
    }

    public static String getXPName() {
        return xPName;
    }

    public static void setXPName(String XPName) {
        PlayerKeywordz.xPName = XPName;
    }

    public static String getHealthName() {
        return healthName;
    }

    public static void setHealthName(String healthName) {
        PlayerKeywordz.healthName = healthName;
    }

    public static String getManaName() {
        return manaName;
    }

    public static void setManaName(String manaName) {
        PlayerKeywordz.manaName = manaName;
    }

    public static String getStrengthName() {
        return strengthName;
    }

    public static void setStrengthName(String strengthName) {
        PlayerKeywordz.strengthName = strengthName;
    }

    public static String getStrengthDesc() {
        return strengthDesc;
    }

    public static void setStrengthDesc(String strengthDesc) {
        PlayerKeywordz.strengthDesc = strengthDesc;
    }

    public static String getDexterityName() {
        return dexterityName;
    }

    public static void setDexterityName(String dexterityName) {
        PlayerKeywordz.dexterityName = dexterityName;
    }

    public static String getDexteritDesc() {
        return dexteritDesc;
    }

    public static void setDexteritDesc(String dexteritDesc) {
        PlayerKeywordz.dexteritDesc = dexteritDesc;
    }

    public static String getIntellectName() {
        return intellectName;
    }

    public static void setIntellectName(String intellectName) {
        PlayerKeywordz.intellectName = intellectName;
    }

    public static String getIntellectDesc() {
        return intellectDesc;
    }

    public static void setIntellectDesc(String intellectDesc) {
        PlayerKeywordz.intellectDesc = intellectDesc;
    }

    public static String[] getMinorStats() {
        return minorStats;
    }

    public static void setMinorStats(String[] minorStats) {
        PlayerKeywordz.minorStats = minorStats;
    }

    public static String[] getStatDescs() {
        return minorStatDescs;
    }

    public static void setStatDescs(String[] statDescs) {
        PlayerKeywordz.minorStatDescs = statDescs;
    }
    public static String getKeenEyeName() {
        return keenEyeName;
    }

    public static void setKeenEyeName(String keenEyeName) {
        PlayerKeywordz.keenEyeName = keenEyeName;
    }

    public static void addMinorStatName(String statName) {
        for (int i = 0; i < getMinorStats().length; i++) {
            if (getMinorStats()[i] == null) {
                getMinorStats()[i] = statName;
                return;
            }
        }
    }

    public static void addMinorStatDesc(String statDesc) {
        for (int i = 0; i < getStatDescs().length; i++) {
            if (getStatDescs()[i] == null) {
                getStatDescs()[i] = statDesc;
                return;
            }
        }
    }

    public static void initAllNamez(String statsTableFileName) throws Exception {
        if (statsTableFileName == null) {
            statsTableFileName = FileParser.joinConfigFolder("statsTable.txt");
        }
        List<String> namez = FileParser.parseFile(statsTableFileName, "#", true);
        if (namez == null) {
            GlobalConf.issueLog("Got a null value in the statsTable.txt file! Aborting!",
                    GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
        Map<String, String> parsedz = new LinkedHashMap<>();
        for (String thisLine: namez) {
            List<String> keyValPair= Arrays.stream(thisLine.split("::"))
                    .map(StoryDisplayer::removeWhiteSpace).toList();
            if (keyValPair.size() > 2) {
                GlobalConf.issueLog("Received malformed data in " + statsTableFileName + "!",
                        GlobalConf.SEVERITY_LEVEL_ERROR, true);
            }
            parsedz.put(keyValPair.get(0), keyValPair.get(1));
        }
        int minorStatCtr = 1;
        try {
            setCurrencyName(parsedz.get("Currency name"));
            setLevelName(parsedz.get("Name for displaying level"));
            setXPName(parsedz.get("Experience name"));
            setxPAbbr(parsedz.get("Abbreviation for experience"));
            setHealthName(parsedz.get("Name for health"));
            setManaName(parsedz.get("Name for Mana"));
            setStrengthName(parsedz.get("Name for strength"));
            setStrAbbr(parsedz.get("Abbreviation for strength"));
            setStrengthDesc(parsedz.get("Description for strength"));
            setDexterityName(parsedz.get("Name for dexterity"));
            setDexAbbr(parsedz.get("Abbreviation for dexterity"));
            setDexteritDesc(parsedz.get("Description for dexterity"));
            setIntellectName(parsedz.get("Name for intellect"));
            setIntAbbr(parsedz.get("Abbreviation for intellect"));
            setIntellectDesc(parsedz.get("Description for intellect"));
            setKeenEyeName(parsedz.get("Name for Keen Eye"));
            addMinorStatName(parsedz.get("Name for Keen Eye"));
            addMinorStatDesc(parsedz.get("Description for Keen Eye"));
            while (!parsedz.get("Name for extra talent #" + (minorStatCtr)).isEmpty()) {
                addMinorStatName(parsedz.get("Name for extra talent #" + (minorStatCtr)));
                addMinorStatDesc(parsedz.get("Description for talent #" + (minorStatCtr)));
                minorStatCtr++;
            }
            setNumMinorStatz(Arrays.stream(getMinorStats()).filter(Objects::nonNull).toList().size());
        } catch (NullPointerException e) {
            GlobalConf.issueLog("Error attempting to parse " + statsTableFileName + "! " +
                    "Some names may have been initialised unsuccessfully!",
                    GlobalConf.SEVERITY_LEVEL_ERROR, true);
        }
    }
}
