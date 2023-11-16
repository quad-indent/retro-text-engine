package storyBits;

import java.util.Map;

public class GlobalConf {
    private static boolean minimalConfig;
    public static boolean isMinimalConfig() {
        return minimalConfig;
    }
    public static void setMinimalConfig(boolean tempMinimalConfig) {
        minimalConfig = tempMinimalConfig;
    }
    public static void initGlobalConf(Map<String, String> confArgs) {
        setMinimalConfig(confArgs.get("minimalplayersheet").equalsIgnoreCase("true"));
    }
}
