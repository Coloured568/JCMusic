package JCMusic;

import JCMusic.logic.Audio;
import JCMusic.logic.Playlists;

import java.util.Scanner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;

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
        if (playlists.songs[index] != null) {
            boolean exit = false;
            System.out.print("\033[H\033[2J");
            System.out.println("JCMPL");
            System.out.println("-------");
            System.out.println("Controls: 'p' = play, 'q' = quit");

            while (!exit) {
                System.out.print("> ");
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "p":
                        if (!isPlaying) {
                            audio.playAudio(playlists.songs[index]);
                            isPlaying = true;

                            playbackThread = new Thread(() -> {
                                startTrack(audio, playlists);
                            });
                            playbackThread.start();
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
                        if (isPlaying) {
                            isPlaying = false;
                            audio.stopAudio();
                            if (playbackThread != null) playbackThread.interrupt();
                        }
                        
                        if (!isPlaying) {
                            audio.playAudio(playlists.songs[index++]);
                            isPlaying = true;

                            playbackThread = new Thread(() -> {
                                startTrack(audio, playlists);
                            });
                            playbackThread.start();
                        } 

                        break;

                    case "b":
                        if (isPlaying) {
                            isPlaying = false;
                            audio.stopAudio();
                            if (playbackThread != null) playbackThread.interrupt();
                        }
                        
                        if (!isPlaying) {
                            audio.playAudio(playlists.songs[index--]);
                            isPlaying = true;

                            playbackThread = new Thread(() -> {
                                startTrack(audio, playlists);
                            });
                            playbackThread.start();
                        } 

                        break;

                    default:
                        System.out.println("Unknown command. Use 'p', 's', 'q', 'n', or 'b'.");
                }
            }
        } else {
            System.out.println("No songs found in playlist.");
        }

        scanner.close();
    }

    private static void startTrack(Audio audioInstance, Playlists playlistsInstance) {
            Clip clip = audioInstance.getClip();
            AudioFormat format = clip.getFormat();
            float sampleRate = format.getFrameRate();

            while (isPlaying && clip.isActive()) {
                long framePosition = clip.getLongFramePosition();
                double seconds = framePosition / sampleRate;

                int totalSeconds = (int) seconds;
                int minutes = totalSeconds / 60;
                int secondsPart = totalSeconds % 60;
                int visualIndex = index + 1; // what actually shows up in the player

                System.out.print("\033[H\033[2J"); // Clear console
                System.out.flush();
                System.out.println("JCMPL");
                System.out.println("-------");
                System.out.printf("Now playing: %s (%d/%d)\n", playlistsInstance.songs[index], visualIndex, playlistsInstance.songs.length);
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
}
