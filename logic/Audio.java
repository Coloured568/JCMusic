package JCMusic.logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class Audio {
    private MediaPlayer mediaPlayer;
    private String currentFilePath;

    public void playAudio(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            System.out.println("Audio path is empty.");
            return;
        }

        stopAudio();

        try {
            File audioFile = new File(filePath);
            Media media = new Media(audioFile.toURI().toString());

            media.setOnError(() -> {
                System.out.println("error: " + media.getError());
            });

            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnError(() -> {
                System.out.println("error: " + mediaPlayer.getError());
            });

            mediaPlayer.setOnReady(() -> {
                System.out.println("now playing: : " + filePath);
                mediaPlayer.play();
                currentFilePath = filePath;
            });

        } catch (Exception e) {
            System.out.println("exception playing audio: " + e.getMessage());
        }
}


    public void pauseAudio() {
        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                System.out.println("playback paused.");
            }
        }
    }

    public void resumeAudio() {
        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
                System.out.println("playback resumed.");
            }
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            System.out.println("playback stopped.");
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public double getCurrentPositionSeconds() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0;
    }

    public double getTotalDurationSeconds() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            return mediaPlayer.getTotalDuration().toSeconds();
        }
        return 0;
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    public void setOnEndOfMedia(Runnable callback) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(callback);
        }
    }
}

