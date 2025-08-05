package JCMusic;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        GUI gui = new GUI();

        // i probably could've used a switch statement here but i couldnt be bothered
        if (args.length > 0) {
            if(args[0].equals("--no-gui")) {
                if(args.length > 1) {
                    cli.dirSpecified = true;
                    cli.playlistName = args[1];
                    cli.cliApp();
                } else {
                    cli.cliApp();
                }
            } else {
                System.out.println("unknown argument: " + args[0]);
                gui.guiApp();
            }
        } else {
            gui.guiApp();
        }

    }
}
