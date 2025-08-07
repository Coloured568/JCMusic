package JCMusic.logic;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;

public class PlaylistCreator {
    public void createPlaylist(String filename) {
        try {
        File playlistFile = new File(filename + ".jcmpl");
        if (playlistFile.createNewFile()) {
            System.out.println("Playlist created: " + playlistFile.getName());
        } else {
            System.out.println("Playlist already exists.");
        }
        } catch (IOException e) {
            System.out.printf("error: %s\n", e);
        }
    }

    // you can also write file paths
    public void importSongs(String playlistFile, String directory) throws Exception {
        try 
        {
            File playerFileBool = new File(playlistFile + ".jcmpl"); // temp variable to check if the file exists
            if(playerFileBool.exists()) {
                try {
                    writeSongs(playlistFile + ".jcmpl", directory);
                } catch (Exception e) {
                    System.out.printf("error: %s\n", e);
                }
            } else {
                createPlaylist(playlistFile);
                writeSongs(playlistFile + ".jcmpl", directory);
            }
        } 
        catch (IOException e) {
            System.out.printf("error: %s\n", e);
        }
    }

    public void removeSong(String playlistFile, String audioFile) {
        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(playlistFile + ".jcmpl"));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
    
            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();
            String inputStr = inputBuffer.toString();
    
            System.out.println(inputStr); // display the original file for debugging
    
            // logic to replace lines in the String (could use regex here to be generic)
            inputStr = inputStr.replace(audioFile, null);
    
            // display the new file for debugging
            System.out.println("----------------------------------\n" + inputStr);
    
            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(playlistFile);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
    
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    private void writeSongs(String playlistFile, String directory) throws Exception {
        try {
            FileWriter writer = new FileWriter(playlistFile);
            File dir = new File(directory);
            String[] songFiles = dir.list();

            for(int i = 0; i < songFiles.length; i++) {
                if(songFiles[i].endsWith(".wav")) {
                    writer.write(songFiles[i]);
                    writer.write(System.lineSeparator());
                    System.out.printf("Added song: %s\n", songFiles[i]);
                }
            }
            
            writer.close();
            System.out.println("Imported files successfully!");
        } catch(Exception e) {
            System.out.printf("error: %s\n", e);
        }
    }
}

    