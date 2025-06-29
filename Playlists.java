package JCMusic.logic;

import java.io.*;
import java.util.*;

public class Playlists {
    public static int index = 0;
    public static String[] tempSongs = new String[10000]; // this isn't quite "elegant" but it makes indexing easier.
    public static String[] songs;
    
    public static void loadPlaylist(String filename) throws Exception {
        if (filename.endsWith(".jcmpl")) {
            File file = new File(filename);
            
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String song = sc.nextLine();
                tempSongs[index] = song;
                index++;
            }

            songs = new String[index];

            sc.close();

            index = 0;

            while (tempSongs[index] != null) {
                songs[index] = tempSongs[index];
                System.out.println(songs[index]);
                index++;
            }

            index = 0;
        } else if (filename.endsWith(".cmpl")) {
            System.out.println("Hey I noticed you're trying to use the legacy format! That's currently unsupported.");
        } else {
            System.out.println("uhhh that file format isn't supported.");
        }
    }
}