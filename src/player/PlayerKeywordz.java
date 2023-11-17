package player;

import storyBits.FileParser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public static void initAllNamez(String statsTableFileName) {
        if (statsTableFileName == null) {
            statsTableFileName = "statsTable.txt";
        }
        List<String> namez = FileParser.parseFile(statsTableFileName, "#", true);
        int minorStatCtr = 0;
        try {
            if (namez == null) {
                throw new NullPointerException();
            }
            setCurrencyName(namez.remove(0));
            setLevelName(namez.remove(0));
            setXPName(namez.remove(0));
            setxPAbbr(namez.remove(0));
            setHealthName(namez.remove(0));
            setManaName(namez.remove(0));
            setStrengthName(namez.remove(0));
            setStrAbbr(namez.remove(0));
            setStrengthDesc(namez.remove(0));
            setDexterityName(namez.remove(0));
            setDexAbbr(namez.remove(0));
            setDexteritDesc(namez.remove(0));
            setIntellectName(namez.remove(0));
            setIntAbbr(namez.remove(0));
            setIntellectDesc(namez.remove(0));
            setKeenEyeName(namez.remove(0));
            addMinorStatName(getKeenEyeName());
            addMinorStatDesc(namez.remove(0));
            while (namez.size() > 1) {
                addMinorStatName(namez.remove(0));
                addMinorStatDesc(namez.remove(0));
            }
            setNumMinorStatz(Arrays.stream(getMinorStats()).filter(Objects::nonNull).toList().size());
        } catch (NullPointerException e) {
            System.out.println("Error attempting to parse " + statsTableFileName + "! " +
                    "Some names may have been initialised unsuccessfully!");
        }
    }
}
