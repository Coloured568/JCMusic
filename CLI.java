package JCMusic;

import JCMusic.logic.Audio;
import JCMusic.logic.PlaylistCreator;
import JCMusic.logic.Playlists;

import java.util.Scanner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

public class CLI {
    public static int index;
    public static boolean dirSpecified;
    public static String playlistName;
    private static boolean isPlaying = false;
    private static Thread playbackThread;

    public static void cliApp() {
        Audio audio = new Audio();
        Playlists playlists = new Playlists();
        Scanner scanner = new Scanner(System.in);
        PlaylistCreator pc = new PlaylistCreator();

        if (!dirSpecified) {
            System.out.println("Enter playlist directory (*.jcmpl): ");
            playlistName = scanner.nextLine();
        }

        try {
            playlists.loadPlaylist(playlistName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        index = 0;
        if (playlistName != null) {
            boolean exit = false;
            clearConsole();
            System.out.println("JCMPL");
            System.out.println("-------");
            System.out.println("Controls: 'p' = play, 'q' = quit");
            System.out.println("Commands: 'editor' - enter playlist editor.");

            while (!exit) {
                System.out.print("> ");
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "p":
                        if (!isPlaying) {
                            if (playlists.songs == null || playlists.songs.length == 0) {
                                System.out.println("No playlist specified or it's empty! Please enter 'editor' or specify playlist.");
                            } else {
                                isPlaying = true;
                                playbackThread = new Thread(() -> {
                                    while (isPlaying && index < playlists.songs.length) {
                                        audio.playAudio(playlists.songs[index]);
                                
                                        // Update UI on the Event Dispatch Thread
                                        int currentIndex = index;
                                        SwingUtilities.invokeLater(() -> startTrack(audio, playlists, currentIndex));
                                
                                        // Wait while the clip is playing
                                        Clip clip = audio.getClip();
                                        while (clip != null && clip.isRunning()) {
                                            try {
                                                Thread.sleep(200); // Don't block the UI
                                            } catch (InterruptedException e) {
                                                return;
                                            }
                                            if (!isPlaying) break;
                                        }
                                
                                        if (!isPlaying) break;
                                        index++;
                                    }
                                
                                    if (index >= playlists.songs.length) {
                                        isPlaying = false;
                                        System.out.println("\nReached end of playlist.");
                                    }
                                });
                                
                                playbackThread.start();
                            }
                        } else {
                            System.out.println("Already playing.");
                        }
                        break;

                    case "s":
                        if (isPlaying) {
                            isPlaying = false;
                            audio.stopAudio();
                            if (playbackThread != null) playbackThread.interrupt();
                            System.out.println("Stopped playback.");
                        } else {
                            System.out.println("Nothing is playing.");
                        }
                        break;

                    case "q":
                        if (isPlaying) {
                            isPlaying = false;
                            audio.stopAudio();
                            if (playbackThread != null) playbackThread.interrupt();
                        }
                        exit = true;
                        System.out.println("Exiting...");
                        break;

                    case "n":
                        if (index < playlists.songs.length - 1) {
                            audio.stopAudio();
                            index++;
                            isPlaying = true;
                    
                            if (playbackThread != null && playbackThread.isAlive()) {
                                playbackThread.interrupt();
                            }
                    
                            playbackThread = new Thread(() -> {
                                audio.playAudio(playlists.songs[index]);
                    
                                // update UI
                                int currentIndex = index;
                                SwingUtilities.invokeLater(() -> startTrack(audio, playlists, currentIndex));
                    
                                Clip clip = audio.getClip();
                                while (clip != null && clip.isRunning()) {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                }
                    
                                index++;
                                if (index >= playlists.songs.length) {
                                    isPlaying = false;
                                    System.out.println("\nReached end of playlist.");
                                }
                            });
                    
                            playbackThread.start();
                        }
                        break;
                    

                    case "b":
                        if (index > 0) {
                            audio.stopAudio();
                            index--;
                            isPlaying = true;
                    
                            if (playbackThread != null && playbackThread.isAlive()) {
                                playbackThread.interrupt();
                            }
                    
                            playbackThread = new Thread(() -> {
                                audio.playAudio(playlists.songs[index]);
                    
                                // update UI
                                int currentIndex = index;
                                SwingUtilities.invokeLater(() -> startTrack(audio, playlists, currentIndex));
                    
                                Clip clip = audio.getClip();
                                while (clip != null && clip.isRunning()) {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        return;
                                    }
                                }
                    
                                index++;
                                if (index >= playlists.songs.length) {
                                    isPlaying = false;
                                    System.out.println("\nReached end of playlist.");
                                }
                            });
                    
                            playbackThread.start();
                        }
                        break;
                    

                    case "editor":
                        System.out.println("Enter playlist name.");
                        System.out.print("> ");
                        String newPlaylistName = scanner.nextLine();

                        System.out.println("Enter song(s) directory.");
                        System.out.print("> ");
                        String directory = scanner.nextLine();

                        try {
                            pc.importSongs(newPlaylistName, directory);
                        } catch (Exception e) {
                            System.out.printf("Error importing songs: %s\n", e);
                        }

                        try {
                            playlists.loadPlaylist(newPlaylistName + ".jcmpl");
                            playlistName = newPlaylistName + ".jcmpl";
                            index = 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return;
                        }
                        break;

                    default:
                        System.out.println("Unknown command. Use 'p', 's', 'q', 'n', 'b', or 'editor'.");
                }
            }
        } else {
            System.out.println("No playlist specified! Please enter 'editor' or specify playlist.");
        }

        scanner.close();
    }

    private static void restartTrack(Audio audio, Playlists playlists) {
        if (isPlaying) {
            isPlaying = false;
            audio.stopAudio();
            if (playbackThread != null) playbackThread.interrupt();
        }

        isPlaying = true;
        playbackThread = new Thread(() -> {
            audio.playAudio(playlists.songs[index]);
            startTrack(audio, playlists, index);
        });
        playbackThread.start();
    }

    private static void startTrack(Audio audioInstance, Playlists playlistsInstance, int currentIndex) {
        Clip clip = audioInstance.getClip();
        AudioFormat format = clip.getFormat();
        float sampleRate = format.getFrameRate();

        while (isPlaying && clip.isRunning()) {
            long framePosition = clip.getLongFramePosition();
            double seconds = framePosition / sampleRate;

            int totalSeconds = (int) seconds;
            int minutes = totalSeconds / 60;
            int secondsPart = totalSeconds % 60;

            clearConsole();
            System.out.println("JCMPL");
            System.out.println("-------");
            System.out.printf("Now playing: %s (%d/%d)\n", playlistsInstance.songs[currentIndex], currentIndex + 1, playlistsInstance.songs.length);
            System.out.println("Playlist: " + playlistName);
            System.out.printf("Time: %02d:%02d\n", minutes, secondsPart);
            System.out.print("Controls: 'p' = play, 's' = stop, 'q' = quit, 'n' = next track, 'b' = previous track");
            System.out.print("\n> ");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
