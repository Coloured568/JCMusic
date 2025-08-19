package JCMusic;

import javafx.application.Platform;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import JCMusic.logic.Audio;
import JCMusic.logic.PlaylistCreator;
import JCMusic.logic.Playlists;

public class GUI {
    static {
        // Static block runs once when the class is loaded, initializes JavaFX toolkit
        try {
            Platform.startup(() -> {
                // No-op Runnable just to start JavaFX
            });
        } catch (IllegalStateException e) {
            // JavaFX already initialized
        }
    }

    public static int index;
    public static String background;
    public static String foreground;

    public static void guiApp() {
        final boolean[] isSeeking = {false};
        final boolean[] isUpdatingSlider = {false};

        Audio audio = new Audio();
        Playlists playlists = new Playlists();

        JFrame frame = new JFrame("JCMusic");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setSize(350, 500);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0; // allows the column to grow in size
        c.insets = new Insets(3, 3, 3, 3);

        try {
            File config = new File("config.txt");
            if (config.createNewFile()) {
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

                Scanner reader = new Scanner(config);
                background = reader.nextLine().trim();
                foreground = reader.nextLine().trim();
                reader.close();
            } else {
                Scanner reader = new Scanner(config);
                background = reader.nextLine().trim();
                foreground = reader.nextLine().trim();
                reader.close();
            }
        } catch (Exception e) {
            System.out.printf("error %s\n", e);
        }

        Color backgroundColor = parseColor(background);
        Color foregroundColor = parseColor(foreground);

        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);
        panel.setLayout(new GridBagLayout());

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

        JTextField playlistName = new JTextField("Enter playlist filepath here (*.cmpl, *.jcmpl)");
        playlistName.setForeground(foregroundColor);
        playlistName.setBackground(backgroundColor);

        ImageIcon imageIcon = new ImageIcon("animation.gif"); // load the image to a imageIcon
        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(panel.getWidth(), 150, 0); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);  // transform it back 
        JLabel animation = new JLabel(imageIcon);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        int paddingy = 1;

        c.gridy = paddingy++;
        panel.add(title, c);
        c.gridy = paddingy++;
        panel.add(songTitle, c);
        c.gridy = paddingy++;
        panel.add(timeSlider, c);
        c.gridy = paddingy++;
        panel.add(loadPlaylistButton, c);
        c.gridy = paddingy++;
        panel.add(createPlaylistButton, c);
        c.gridy = paddingy++;
        panel.add(playButton, c);
        c.gridy = paddingy++;
        panel.add(resumeButton, c);
        c.gridy = paddingy++;
        panel.add(pauseButton, c);
        c.gridy = paddingy++;
        panel.add(skipButton, c);
        c.gridy = paddingy++;
        panel.add(previousButton, c);
        c.gridy = paddingy++;
        panel.add(playlistName, c);
        c.gridy = paddingy++;
        panel.add(animation, c);
        c.gridy = paddingy++;

        frame.add(panel);

        createPlaylistButton.addActionListener(e -> new Thread(() -> createPlaylistGUI()).start());

        loadPlaylistButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    playlists.loadPlaylist(playlistName.getText());
                    SwingUtilities.invokeLater(() -> songTitle.setText("Playlist loaded: " + playlistName.getText()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        playButton.addActionListener(e -> {
            new Thread(() -> {
                index = 0;
                if (playlists.songs.length > 0 && playlists.songs[index] != null) {
                    audio.playAudio(playlists.songs[index]);
                    audio.setOnEndOfMedia(() -> {
                        index = (index + 1) % playlists.songs.length;
                        audio.playAudio(playlists.songs[index]);
                        SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
                    });
                    SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
                }
            }).start();
        });

        resumeButton.addActionListener(e -> audio.resumeAudio());

        pauseButton.addActionListener(e -> audio.pauseAudio());

        skipButton.addActionListener(e -> {
            new Thread(() -> {
                audio.stopAudio();
                index = (index + 1) % playlists.songs.length;
                audio.playAudio(playlists.songs[index]);
                audio.setOnEndOfMedia(() -> {
                    index = (index + 1) % playlists.songs.length;
                    audio.playAudio(playlists.songs[index]);
                    SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
                });
                SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
            }).start();
        });

        previousButton.addActionListener(e -> {
            new Thread(() -> {
                audio.stopAudio();
                index--;
                if (index < 0) index = playlists.songs.length - 1;
                audio.playAudio(playlists.songs[index]);
                audio.setOnEndOfMedia(() -> {
                    index = (index + 1) % playlists.songs.length;
                    audio.playAudio(playlists.songs[index]);
                    SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
                });
                SwingUtilities.invokeLater(() -> songTitle.setText(new File(playlists.songs[index]).getName()));
            }).start();
        });

        Timer sliderTimer = new Timer(500, evt -> {
            if (isSeeking[0]) return;

            double current = audio.getCurrentPositionSeconds();
            double total = audio.getTotalDurationSeconds();

            if (total > 0) {
                isUpdatingSlider[0] = true;
                if (timeSlider.getMaximum() != (int) total) {
                    timeSlider.setMaximum((int) total);
                }
                if (timeSlider.getValue() != (int) current) {
                    timeSlider.setValue((int) current);
                }
                isUpdatingSlider[0] = false;
            }
        });
        sliderTimer.start();

        timeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (isUpdatingSlider[0]) return;

                if (timeSlider.getValueIsAdjusting()) {
                    isSeeking[0] = true;
                } else {
                    audio.seek(timeSlider.getValue());
                    isSeeking[0] = false;
                }
            }
        });

        frame.setSize(350, 500);
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
            reader.close();
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

        JTextField songDir = new JTextField("Enter song directory here.");
        songDir.setBackground(backgroundColor);
        songDir.setForeground(foregroundColor);

        JButton importSongsButton = new JButton("Import songs");
        importSongsButton.setBackground(backgroundColor);
        importSongsButton.setForeground(foregroundColor);

        sPanel.add(title);
        sPanel.add(playlistName);
        sPanel.add(songDir);
        sPanel.add(importSongsButton);
        sPanel.add(createPlaylistButton);
        sFrame.add(sPanel);

        sFrame.setSize(250, 400);
        sFrame.setLocationRelativeTo(null);
        sFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        sFrame.setVisible(true);

        createPlaylistButton.addActionListener(e -> new Thread(() -> pc.createPlaylist(playlistName.getText())).start());

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
