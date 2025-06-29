package JCMusic;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import JCMusic.logic.Audio;
import JCMusic.logic.Playlists;

import javax.sound.sampled.Clip;

public class JCMusic {
    public static int index;
    
    public static void main(String[] args) {
        final boolean[] isSeeking = { false };
        final boolean[] isUpdatingSlider = { false };

        Audio audio = new Audio();
        Playlists playlists = new Playlists();

        JFrame frame = new JFrame("JCMusic");
        JPanel panel = new JPanel();

        panel.setBackground(new java.awt.Color(0, 0, 0));
        panel.setForeground(new java.awt.Color(255, 255, 255));
        panel.setLayout(new GridLayout(0, 1, 2, 2));

        JLabel title = new JLabel("JCMusic", SwingConstants.CENTER);
        title.setForeground(new java.awt.Color(255, 255, 255));

        JLabel songTitle = new JLabel("No song playing", SwingConstants.CENTER);
        songTitle.setForeground(new java.awt.Color(255, 255, 255));

        JSlider timeSlider = new JSlider();
        timeSlider.setForeground(new java.awt.Color(255, 255, 255));
        timeSlider.setBackground(new java.awt.Color(0, 0, 0));
        
        JButton playButton = new JButton("Start");
        playButton.setForeground(new java.awt.Color(255, 255, 255));
        playButton.setBackground(new java.awt.Color(0, 0, 0));

        JButton pauseButton = new JButton("Pause");
        pauseButton.setForeground(new java.awt.Color(255, 255, 255));
        pauseButton.setBackground(new java.awt.Color(0, 0, 0));

        JButton resumeButton = new JButton("Resume");
        resumeButton.setForeground(new java.awt.Color(255, 255, 255));
        resumeButton.setBackground(new java.awt.Color(0, 0, 0));

        JButton skipButton = new JButton("Skip");
        skipButton.setForeground(new java.awt.Color(255, 255, 255));
        skipButton.setBackground(new java.awt.Color(0, 0, 0));

        JButton previousButton = new JButton("Previous");
        previousButton.setForeground(new java.awt.Color(255, 255, 255));
        previousButton.setBackground(new java.awt.Color(0, 0, 0));

        JButton loadPlaylistButton = new JButton("Load playlist");
        loadPlaylistButton.setForeground(new java.awt.Color(255, 255, 255));
        loadPlaylistButton.setBackground(new java.awt.Color(0, 0, 0));

        JTextField playlistName = new JTextField("Enter playlist filepath here (*.cmpl)");
        playlistName.setForeground(new java.awt.Color(255, 255, 255));
        playlistName.setBackground(new java.awt.Color(0, 0, 0));
        
        panel.add(title);
        panel.add(songTitle);
        panel.add(timeSlider);
        panel.add(loadPlaylistButton);
        panel.add(playButton);
        panel.add(resumeButton);
        panel.add(pauseButton);
        panel.add(skipButton);
        panel.add(previousButton);
        panel.add(playlistName);
        frame.add(panel);

        // Load playlist
        loadPlaylistButton.addActionListener(e -> {
            try {
                playlists.loadPlaylist(playlistName.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Play
        playButton.addActionListener(e -> {
            index = 0;
            if (playlists.songs.length > 0 && playlists.songs[index] != null) {
                audio.playAudio(playlists.songs[index]);
                updateSongTitleAndSlider(songTitle, timeSlider, audio.getClip(), playlists.songs[index]);
            }
        });

        // Resume
        resumeButton.addActionListener(e -> audio.resumeAudio());

        // Pause
        pauseButton.addActionListener(e -> audio.pauseAudio());

        // Skip
        skipButton.addActionListener(e -> {
            if (audio.getClip() != null) {
                audio.stopAudio();
            }
            index = (index + 1) % playlists.songs.length;
            if (playlists.songs[index] != null) {
                audio.playAudio(playlists.songs[index]);
                updateSongTitleAndSlider(songTitle, timeSlider, audio.getClip(), playlists.songs[index]);
            }
        });

        // Previous
        previousButton.addActionListener(e -> {
            if (audio.getClip() != null) {
                audio.stopAudio();
            }
            index--;
            if (index < 0) {
                index = playlists.songs.length - 1;
            }
            if (playlists.songs[index] != null) {
                audio.playAudio(playlists.songs[index]);
                updateSongTitleAndSlider(songTitle, timeSlider, audio.getClip(), playlists.songs[index]);
            }
        });

        // Timer to update slider every 500ms, skips update if user is seeking
        Timer sliderTimer = new Timer(500, evt -> {
            if (isSeeking[0]) return; // Skip updates while user drags slider

            Clip clip = audio.getClip();
            if (clip != null && clip.isRunning()) {
                int currentSec = (int) (clip.getMicrosecondPosition() / 1_000_000);
                int totalSec = (int) (clip.getMicrosecondLength() / 1_000_000);

                // Update slider programmatically - set flag so change listener ignores
                isUpdatingSlider[0] = true;
                if (timeSlider.getMaximum() != totalSec) {
                    timeSlider.setMaximum(totalSec);
                }
                if (timeSlider.getValue() != currentSec) {
                    timeSlider.setValue(currentSec);
                }
                if (currentSec == totalSec - 1) {
                    if (audio.getClip() != null) {
                        audio.stopAudio();
                    }
                    index = (index + 1) % playlists.songs.length;
                    if (playlists.songs[index] != null) {
                        audio.playAudio(playlists.songs[index]);
                        updateSongTitleAndSlider(songTitle, timeSlider, audio.getClip(), playlists.songs[index]);
                    }
                }
                isUpdatingSlider[0] = false;

                
            }
        });
        sliderTimer.start();

        // Slider seeking logic
        timeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (isUpdatingSlider[0]) return; // Ignore programmatic slider updates

                Clip clip = audio.getClip();
                if (clip != null && clip.isOpen()) {
                    if (timeSlider.getValueIsAdjusting()) {
                        isSeeking[0] = true; // User is dragging
                    } else {
                        // User finished dragging, seek clip
                        int seconds = timeSlider.getValue();
                        boolean wasRunning = clip.isRunning();
                        if (wasRunning) clip.stop();
                        clip.setMicrosecondPosition(seconds * 1_000_000L);
                        if (wasRunning) clip.start();

                        isSeeking[0] = false; // Done seeking
                    }
                }
            }
        });

        frame.setSize(250, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void updateSongTitleAndSlider(JLabel songTitle, JSlider slider, Clip clip, String title) {
        SwingUtilities.invokeLater(() -> {
            songTitle.setText(title);
            slider.setMaximum((int) (clip.getMicrosecondLength() / 1_000_000));
            slider.setValue(0);
        });
    }
}
