package audio;

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
public class ClipPlayer implements LineListener {
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
    @Override
    public void update(LineEvent event) {
        return;
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
        if (tuneName.equalsIgnoreCase(getCurrentTune())) {
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
                getDjBooth().loop(Clip.LOOP_CONTINUOUSLY);
                setCurrentTune(tuneName);
            }
        } catch (Exception e) {
            throw new Exception("Failed to play " + tunePath + "! Error: " + e.getMessage());
        }
    }
//    public static void playTune(int tuneID) throws Exception {
//        if (tuneID == getCurrentTune()) {
//            return;
//        }
//        String fileFormat = getFileFormatOfTune(tuneID);
//        if (fileFormat == null) {
//            return;
//        }
//        Path tunePath = getTunezPath().resolve(Integer.toString(tuneID) + "." + fileFormat);
//        try (InputStream in = new FileInputStream(tunePath.toString())) {
//            InputStream bufferedInS = new BufferedInputStream(in);
//            try (AudioInputStream audioInS = AudioSystem.getAudioInputStream(bufferedInS)) {
//                if (getDjBooth().isActive()) {
//                    getDjBooth().close();
//                }
//                getDjBooth().open(audioInS);
//                getDjBooth().start();
//                getDjBooth().loop(Clip.LOOP_CONTINUOUSLY);
//                setCurrentTune(tuneID);
//            }
//        } catch (Exception e) {
//            throw new Exception("Failed to play " + tunePath + "! Error: " + e.getMessage());
//        }
//    }
}