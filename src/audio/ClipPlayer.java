package audio;

import combat.CombatUtils;
import storyBits.StoryDisplayer;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
public class ClipPlayer {
    private final static Path tunezPath;
    private final static Clip djBooth;
    private static String currentTune = "";
    public static String getCurrentTune() {
        return currentTune;
    }
    public static void setCurrentTune(String currentTune) {
        ClipPlayer.currentTune = currentTune;
    }
    static {
        tunezPath = Paths.get(System.getProperty("user.dir"), "tunes");
        try {
            djBooth = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
    public static Path getTunezPath() {
        return tunezPath;
    }
    public static Clip getDjBooth() {
        return djBooth;
    }
    public static String[] getTuneInfo(String rawTune) throws Exception {
        String[] splitz = new String[]{"", "loop"};
        String[] actualSplitz = rawTune.split(",");
        if (actualSplitz.length > 2) {
            throw new Exception("Malformed tune data!");
        }
        for (int i = 0; i < actualSplitz.length; i++) {
            splitz[i] = StoryDisplayer.removeWhiteSpace(actualSplitz[i]);
        }
        return splitz;
    }
    public static String getFileFormatOfTune(String tuneName) {
        List<File> filezInDir = Arrays.stream(Objects.requireNonNull(getTunezPath().toFile().listFiles())).
                filter(file -> !file.isDirectory()).toList();
        if (filezInDir.isEmpty()) {
            return null;
        }
        List<String> matchesFound = filezInDir.stream().
                map(File::getName).filter(fileName -> fileName.split("\\.")[0]
                        .equals(tuneName)).toList();
        if (matchesFound.isEmpty()) {
            return null;
        }
        return matchesFound.get(0).split("\\.")[1];
    }
    public static void playTune(String tuneName) throws Exception {
        String[] parsedTune = getTuneInfo(tuneName);
        String loopOption = parsedTune[1];
        tuneName = parsedTune[0];
        if (tuneName.equalsIgnoreCase(getCurrentTune())) {
            return;
        }
        if (tuneName.equalsIgnoreCase("none")) {
            if (getDjBooth().isActive()) {
                getDjBooth().close();
            }
            return;
        }
        String fileFormat = getFileFormatOfTune(tuneName);
        if (fileFormat == null) {
            return;
        }
        Path tunePath = getTunezPath().resolve(tuneName + "." + fileFormat);
        try (InputStream in = new FileInputStream(tunePath.toString())) {
            InputStream bufferedInS = new BufferedInputStream(in);
            try (AudioInputStream audioInS = AudioSystem.getAudioInputStream(bufferedInS)) {
                if (getDjBooth().isActive()) {
                    getDjBooth().close();
                }
                getDjBooth().open(audioInS);
                getDjBooth().start();
                if (loopOption.equalsIgnoreCase("loop")) {
                    getDjBooth().loop(Clip.LOOP_CONTINUOUSLY);
                }
                setCurrentTune(tuneName);
            }
        } catch (Exception e) {
            throw new Exception("Failed to play " + tunePath + "! Error: " + e.getMessage());
        }
    }
}