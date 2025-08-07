# JCMusic
The successor to CMusic!

# How do I run the application?
1. Install Java 24 here -> [https://www.oracle.com/java/technologies/javase/jdk24-archive-downloads.html](https://www.oracle.com/java/technologies/downloads/#java24)
2. Open the jar file!

# Command line arguments
--no-gui: runs the app in CLI mode
- ex: `java -jar JCMusic.jar --no-gui`
- or if you want to specify a playlist file directory: `java -jar JCMusic.jar --no-gui <yourplaylist.jcmpl.`

If you want the gui just launch the jar as normal.

# Playlist editor
1. In the first box, input the directory you want the playlist file to be written to (including the filename at the end)/
2. Input the directory to scan songs (recursively scans directories for songs and imports them).
3. Click import songs to finalize the playlist creation
**Note:** this creates a new playlist file everytime, if you use an old playlist file name it WILL overwrite it completely (this will be subject to change).

# Customization
Simply just modify the colors in `config.txt`.

# Notes
**Lots of things are subject to change considering this is still a beta**

This was made in JDK 23, please install it if you plan on using/contirbuting this JCMusic!
Alongside this certain features haven't been ported yet, such as themes and the legacy playlist format.
MP3s are not supported yet.
Supports: the `.wav ,.aif, .aiff, .au, .snd` audio formats.

**For linux users**: WayLand is NOT supported and the GUI will go unresponsive!

# How to compile?
1. Firstly, ensure that the `compile.sh` is executable by running `chmod +X compile.sh`
2. Run `./compile.sh`
3. The JAR file should be outputted!

# Well how do I create and load a playlist?
Since it's still in development, it's still rather clunky.
1. Create a file that ends with **".jcmpl"**
2. Add the paths to your audio files (do not forget line breaks).
3. It should look something like:
```
song1.wav
song2.wav
```
4. Then, save your file.
5. In the app, type the path to your playlist file ex. `playlist.jcmpl`
6. Click "load playlist"
7. Press start and play your music!
