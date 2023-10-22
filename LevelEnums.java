// Using xp progression used by DnD 5e, which is non-linear. So, enums

import javax.xml.xpath.XPath;

public enum LevelEnums
{
    LV_0(0), LV_1(0), LV_2(300), LV_3(900), LV_4(2700), LV_5(6500),
    LV_6(14000), LV_7(23000), LV_8(34000), LV_9(48000), LV_10(64000),
    LV_11(85000), LV_12(100000), LV_13(120000), LV_14(140000), LV_15(165000),
    LV_16(195000), LV_17(225000), LV_18(265000), LV_19(305000), LV_20(355000);
    public final int val;
    public final static int[] XPArray;
    private LevelEnums(int val)
    {
        this.val = val;
    }
    static
    {
        LevelEnums[] levels = LevelEnums.values();
        XPArray = new int[levels.length];
        for (int i = 0; i < levels.length; i++)
        {
            XPArray[i] = levels[i].val;
        }
    }
}