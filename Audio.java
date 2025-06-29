package JCMusic.logic;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Audio {

    private Clip clip;
    private AudioInputStream audioStream;
    private long clipTimePosition = 0;

    public void playAudio(String filePath) {
        stopAudio(); // stop previous audio if any

        try {
            File audioFile = new File(filePath);
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void pauseAudio() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resumeAudio() {
        if (clip != null && !clip.isRunning()) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
        }
    }

    public void stopAudio() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        try {
            if (audioStream != null) {
                audioStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Clip getClip() {
        return clip;
    }
}
