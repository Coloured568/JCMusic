package JCMusic;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.sound.sampled.Clip;
import JCMusic.logic.Audio;
import JCMusic.logic.PlaylistCreator;
import JCMusic.logic.Playlists;

public class GUI {
    public static int index;
    public static String background;
    public static String foreground;

    public static void guiApp() {
        final boolean[] isSeeking = { false };
        final boolean[] isUpdatingSlider = { false };

        Audio audio = new Audio();
        Playlists playlists = new Playlists();

        JFrame frame = new JFrame("JCMusic");
        JPanel panel = new JPanel();

        try {
            File config = new File("config.txt");
            if(config.createNewFile()) {
                FileWriter writer = new FileWriter(config);
                System.out.println("Config file generated!");
                writer.write("BLACK"); // background color
                writer.write(System.lineSeparator());
                writer.write("WHITE"); // foreground color
                writer.write(System.lineSeparator());
                writer.write("// 1st line represents the background color");
                writer.write(System.lineSeparator());
                writer.write("// 2nd line represents the foreground color");
                writer.close();
                // parse colors
                Scanner reader = new Scanner(config);
                background = reader.nextLine().trim();
                foreground = reader.nextLine().trim();
                reader.close();

            } else {
                Scanner reader = new Scanner(config);
                background = reader.nextLine().trim();
                foreground = reader.nextLine().trim();
            }
        } 
        catch (Exception e) {
            System.out.printf("error %s\n", e);
        }

        Color backgroundColor = parseColor(background);
        Color foregroundColor = parseColor(foreground);

        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);
        panel.setLayout(new GridLayout(0, 1, 2, 2));

        JLabel title = new JLabel("JCMusic", SwingConstants.CENTER);
        title.setForeground(foregroundColor);

        JLabel songTitle = new JLabel("No song playing", SwingConstants.CENTER);
        songTitle.setForeground(foregroundColor);

        JSlider timeSlider = new JSlider();
        timeSlider.setForeground(foregroundColor);
        timeSlider.setBackground(backgroundColor);

        JButton playButton = new JButton("Start");
        playButton.setForeground(foregroundColor);
        playButton.setBackground(backgroundColor);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setForeground(foregroundColor);
        pauseButton.setBackground(backgroundColor);

        JButton resumeButton = new JButton("Resume");
        resumeButton.setForeground(foregroundColor);
        resumeButton.setBackground(backgroundColor);

        JButton skipButton = new JButton("Skip");
        skipButton.setForeground(foregroundColor);
        skipButton.setBackground(backgroundColor);

        JButton previousButton = new JButton("Previous");
        previousButton.setForeground(foregroundColor);
        previousButton.setBackground(backgroundColor);

        JButton createPlaylistButton = new JButton("Playlist editor");
        createPlaylistButton.setForeground(foregroundColor);
        createPlaylistButton.setBackground(backgroundColor);

        JButton loadPlaylistButton = new JButton("Load playlist");
        loadPlaylistButton.setForeground(foregroundColor);
        loadPlaylistButton.setBackground(backgroundColor);

        JTextField playlistName = new JTextField("Enter playlist filepath here (*.cmpl)");
        playlistName.setForeground(foregroundColor);
        playlistName.setBackground(backgroundColor);


        panel.add(title);
        panel.add(songTitle);
        panel.add(timeSlider);
        panel.add(loadPlaylistButton);
        panel.add(createPlaylistButton);
        panel.add(playButton);
        panel.add(resumeButton);
        panel.add(pauseButton);
        panel.add(skipButton);
        panel.add(previousButton);
        panel.add(playlistName);
        frame.add(panel);

        createPlaylistButton.addActionListener(e -> {
            new Thread(() -> {
                createPlaylistGUI();
            }).start();
        });

        // Load playlist on background thread
        loadPlaylistButton.addActionListener(e -> {
            new Thread(() -> {
                    try {
                        playlists.loadPlaylist(playlistName.getText());
                        SwingUtilities.invokeLater(() -> {
                            songTitle.setText("Playlist loaded: " + playlistName.getText());
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            });

        // Play button action on background thread
        playButton.addActionListener(e -> {
            new Thread(() -> {
                index = 0;
                if (playlists.songs.length > 0 && playlists.songs[index] != null) {
                    audio.playAudio(playlists.songs[index]);
                    Clip clip = audio.getClip();
                    SwingUtilities.invokeLater(() -> {
                        updateSongTitleAndSlider(songTitle, timeSlider, clip, playlists.songs[index]);
                    });
                }
            }).start();
        });

        // Resume (fast, safe on EDT)
        resumeButton.addActionListener(e -> audio.resumeAudio());

        // Pause (fast, safe on EDT)
        pauseButton.addActionListener(e -> audio.pauseAudio());

        // Skip on background thread
        skipButton.addActionListener(e -> {
            new Thread(() -> {
                if (audio.getClip() != null) {
                    audio.stopAudio();
                }
                index = (index + 1) % playlists.songs.length;
                if (playlists.songs[index] != null) {
                    audio.playAudio(playlists.songs[index]);
                    Clip clip = audio.getClip();
                    SwingUtilities.invokeLater(() -> {
                        updateSongTitleAndSlider(songTitle, timeSlider, clip, playlists.songs[index]);
                    });
                }
            }).start();
        });

        // Previous on background thread
        previousButton.addActionListener(e -> {
            new Thread(() -> {
                if (audio.getClip() != null) {
                    audio.stopAudio();
                }
                index--;
                if (index < 0) {
                    index = playlists.songs.length - 1;
                }
                if (playlists.songs[index] != null) {
                    audio.playAudio(playlists.songs[index]);
                    Clip clip = audio.getClip();
                    SwingUtilities.invokeLater(() -> {
                        updateSongTitleAndSlider(songTitle, timeSlider, clip, playlists.songs[index]);
                    });
                }
            }).start();
        });

        // Timer to update slider every 500ms, skips update if user is seeking
        Timer sliderTimer = new Timer(500, evt -> {
            if (isSeeking[0]) return;

            Clip clip = audio.getClip();
            if (clip != null && clip.isRunning()) {
                int currentSec = (int) (clip.getMicrosecondPosition() / 1_000_000);
                int totalSec = (int) (clip.getMicrosecondLength() / 1_000_000);

                isUpdatingSlider[0] = true;
                if (timeSlider.getMaximum() != totalSec) {
                    timeSlider.setMaximum(totalSec);
                }
                if (timeSlider.getValue() != currentSec) {
                    timeSlider.setValue(currentSec);
                }

                if (currentSec >= totalSec - 1) {
                    // Run next track logic in background thread to avoid blocking EDT
                    new Thread(() -> {
                        if (audio.getClip() != null) {
                            audio.stopAudio();
                        }
                        index = (index + 1) % playlists.songs.length;
                        if (playlists.songs[index] != null) {
                            audio.playAudio(playlists.songs[index]);
                            Clip clip2 = audio.getClip();
                            SwingUtilities.invokeLater(() -> {
                                updateSongTitleAndSlider(songTitle, timeSlider, clip2, playlists.songs[index]);
                            });
                        }
                    }).start();
                }
                isUpdatingSlider[0] = false;
            }
        });
        sliderTimer.start();

        // Slider seeking logic
        timeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (isUpdatingSlider[0]) return;

                Clip clip = audio.getClip();
                if (clip != null && clip.isOpen()) {
                    if (timeSlider.getValueIsAdjusting()) {
                        isSeeking[0] = true;
                    } else {
                        int seconds = timeSlider.getValue();
                        boolean wasRunning = clip.isRunning();
                        if (wasRunning) clip.stop();
                        clip.setMicrosecondPosition(seconds * 1_000_000L);
                        if (wasRunning) clip.start();

                        isSeeking[0] = false;
                    }
                }
            }
        });

        frame.setSize(250, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void createPlaylistGUI() {
        PlaylistCreator pc = new PlaylistCreator();
        JFrame sFrame = new JFrame("JCMusic | Playlist creator");
        JPanel sPanel = new JPanel();

        try {
            File config = new File("config.txt");
            Scanner reader = new Scanner(config);
            background = reader.nextLine().trim();
            foreground = reader.nextLine().trim();
        } catch (Exception e) {
            System.out.printf("error %s\n", e);
        }

        Color backgroundColor = parseColor(background);
        Color foregroundColor = parseColor(foreground);

        sPanel.setBackground(backgroundColor);
        sPanel.setForeground(foregroundColor);
        sPanel.setLayout(new GridLayout(0, 1, 2, 0));

        JLabel title = new JLabel("Playlist editor", SwingConstants.CENTER);
        title.setForeground(foregroundColor);
        title.setBackground(backgroundColor);

        JTextField playlistName = new JTextField("Enter playlist name here.", SwingConstants.CENTER);
        playlistName.setBackground(backgroundColor);
        playlistName.setForeground(foregroundColor);

        JButton createPlaylistButton = new JButton("Create playlist");
        createPlaylistButton.setBackground(backgroundColor);
        createPlaylistButton.setForeground(foregroundColor);

        JSeparator separator = new JSeparator();
        separator.setForeground(foregroundColor);

        JTextField songDir = new JTextField("Enter song directory here.");
        songDir.setBackground(backgroundColor);
        songDir.setForeground(foregroundColor);

        JButton importSongsButton = new JButton("Import songs");
        importSongsButton.setBackground(backgroundColor);
        importSongsButton.setForeground(foregroundColor);


        sPanel.add(title);
        sPanel.add(playlistName);
        //sPanel.add(createPlaylistButton);
        sPanel.add(songDir);
        sPanel.add(importSongsButton);
        sFrame.add(sPanel);
        

        sFrame.setSize(250, 400);
        sFrame.setLocationRelativeTo(null);
        sFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        sFrame.setVisible(true);

        createPlaylistButton.addActionListener(e -> {
            new Thread(() -> {
                pc.createPlaylist(playlistName.getText());
            }).start();
        });

        importSongsButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    pc.importSongs(playlistName.getText(), songDir.getText());
                } catch (Exception er) {
                    System.out.printf("error: %s\n", er);
                }
            }).start();
        });
    }

    private static void updateSongTitleAndSlider(JLabel songTitle, JSlider slider, Clip clip, String title) {
        SwingUtilities.invokeLater(() -> {
            songTitle.setText(title);
            slider.setMaximum((int) (clip.getMicrosecondLength() / 1_000_000));
            slider.setValue(0);
        });
    }

    public static Color parseColor(String colorString) {
        switch (colorString) {
            case "BLACK": return Color.BLACK;
            case "WHITE": return Color.WHITE;
            case "RED": return Color.RED;
            case "GREEN": return Color.GREEN;
            case "BLUE": return Color.BLUE;
            case "GRAY": return Color.GRAY;
            case "LIGHT_GRAY": return Color.LIGHT_GRAY;
            case "DARK_GRAY": return Color.DARK_GRAY;
            case "YELLOW": return Color.YELLOW;
            case "ORANGE": return Color.ORANGE;
            case "PINK": return Color.PINK;
            case "CYAN": return Color.CYAN;
            case "MAGENTA": return Color.MAGENTA;
            default: return Color.BLACK; // Fallback color
        }
    }
}
