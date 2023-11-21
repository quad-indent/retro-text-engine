package storyBits;

import java.util.Map;

public class GlobalConf {
    private static String storyTextPrefix;
    private static String promtTextPrefix;
    private static String invDispPrefix;

    public static String getStoryTextPrefix() {
        return storyTextPrefix;
    }

    public static void setStoryTextPrefix(String storyTextPrefix) {
        GlobalConf.storyTextPrefix = storyTextPrefix;
    }

    public static String getPromtTextPrefix() {
        return promtTextPrefix;
    }

    public static void setPromtTextPrefix(String promtTextPrefix) {
        GlobalConf.promtTextPrefix = promtTextPrefix;
    }

    public static String getInvDispPrefix() {
        return invDispPrefix;
    }

    public static void setInvDispPrefix(String invDispPrefix) {
        GlobalConf.invDispPrefix = invDispPrefix;
    }

    private static final short LOGS_FULLY_VERBOSE = -1;
    private static final short LOGS_COMPLETE_SUPPRESSION = 0;
    private static final short LOGS_SHOW_ERRORS = 1;
    private static final short LOGS_SHOW_WARNINGS = 2;
    private static final short LOGS_SHOW_INFO = 3;
    public static final short SEVERITY_LEVEL_ERROR = 1;
    public static final short SEVERITY_LEVEL_WARNING = 2;
    public static final short SEVERITY_LEVEL_INFO = 3;
    private static boolean minimalConfig;
    private static int logVerbosity = LOGS_COMPLETE_SUPPRESSION;
    public static void issueLog(String message, short severity) throws Exception {
        boolean shouldThrow = severity == SEVERITY_LEVEL_ERROR;
        issueLog(message, severity, shouldThrow);
    }
    public static void issueLog(String message, short severity, boolean shouldRaiseException) throws Exception {
        if (logVerbosity == LOGS_COMPLETE_SUPPRESSION && !shouldRaiseException) {
            return;
        }
        if (logVerbosity == LOGS_FULLY_VERBOSE ||
            severity <= logVerbosity ||
            shouldRaiseException) {
            String lilPreamble = switch (severity) {
                case SEVERITY_LEVEL_ERROR -> "Error! ";
                case SEVERITY_LEVEL_WARNING -> "Warning! ";
                case SEVERITY_LEVEL_INFO -> "";
                default -> null;
            };
            if (lilPreamble == null) {
                System.out.println("Uh oh, ran into an error when displaying log message! Continuing anyway");
                lilPreamble = "";
            }
            if (shouldRaiseException) {
                throw new Exception(lilPreamble + message);
            }
            System.out.println(lilPreamble + message);
        }
    }
    public static boolean isMinimalConfig() {
        return minimalConfig;
    }
    public static void setMinimalConfig(boolean tempMinimalConfig) {
        minimalConfig = tempMinimalConfig;
    }
    private static int getLogVerbosity() {
        return logVerbosity;
    }
    private static void setLogVerbosity(int logVerbosity) {
        GlobalConf.logVerbosity = logVerbosity;
    }

    public static void initGlobalConf(Map<String, String> confArgs) {
        setMinimalConfig(confArgs.get("minimalplayersheet").equalsIgnoreCase("true"));
        setLogVerbosity(switch (confArgs.get("logginglevel")) {
            case "LOGS_COMPLETE_SUPPRESSION" -> LOGS_COMPLETE_SUPPRESSION;
            case "LOGS_SHOW_ERRORS" -> LOGS_SHOW_ERRORS;
            case "LOGS_SHOW_WARNINGS" -> LOGS_SHOW_WARNINGS;
            case "LOGS_SHOW_INFO" -> LOGS_SHOW_INFO;
            default -> LOGS_FULLY_VERBOSE;
        });
        setStoryTextPrefix(parsePrefix(confArgs.getOrDefault("storytextprefix", ">> ")));
        setPromtTextPrefix(parsePrefix(confArgs.getOrDefault("prompttextprefix", ">> ")));
        setInvDispPrefix(parsePrefix(confArgs.getOrDefault("inventorytextprefix", ">> ")));
    }

    private static String parsePrefix(String rawPrefix) {
        if (!rawPrefix.contains("\"")) {
            return rawPrefix;
        }
        String returnal = StoryDisplayer.removeWhiteSpace(rawPrefix);
        returnal = returnal.substring(returnal.indexOf("\"") + 1);
        int finalIdx = returnal.length() - 1;
        while (returnal.charAt(finalIdx) != '"') {
            finalIdx--;
        }
        return returnal.substring(0, finalIdx);
    }
}
