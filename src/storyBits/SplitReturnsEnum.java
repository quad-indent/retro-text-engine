package storyBits;

public enum SplitReturnsEnum {
    STORY_PROMPT(2),
    ENDING(3),
    STORY_CHOICE(3),
    COMBAT_PROMPT(5); // IT MAY BE 6 IF LEVEL IS SPECIFIED!
    public final int val;
    SplitReturnsEnum(int val) {
        this.val = val;
    }
}