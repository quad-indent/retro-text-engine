package inventory;

public enum itEnum {
    I_TYPE(0),
    I_NAME(1),
    I_DESC(2),
    I_SELLSFOR(3),
    I_1H(4),
    I_ARMOURSLOT(4),
    I_ARMOURVAL(5),
    I_MINDMG(5),
    I_MAXDMG(6),
    I_SCALESTAT(7),
    I_ISSHIELD(8);

    public final int val;
    itEnum(int val) {
        this.val = val;
    }
}