# JCMusic
The successor to CMusic!

# How do I run the application?
1. Install Java 23 here -> https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html
2. Open the jar file!

# Notes
**Lots of things are subject to change considering this is still a beta**

This was made in JDK 23, please install it if you plan on using/contirbuting this JCMusic!
Alongside this certain features haven't been ported yet, such as themes and the legacy playlist format.
MP3s are not supported yet.
Supports: the `.wav ,.aif, .aiff, .au, .snd` audio formats.

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
