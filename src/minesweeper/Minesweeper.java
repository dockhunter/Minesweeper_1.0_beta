package minesweeper;

import minesweeper.consoleui.ConsoleUI;
import minesweeper.core.Field;

/**
 * Main application class.
 */
public class Minesweeper {
    /** User interface. */
    private UserInterface userInterface;
    
    /** Saves the starting time of the game */
    private long startMillis;
    
    
    /**
     * Constructor.
     */
    public Minesweeper() {
    	System.err.println(
    			"╔╦╗┬┌┐┌┌─┐┌─┐┬ ┬┌─┐┌─┐┌─┐┌─┐┬─┐1.0 Beta\n" + 
    			"║║║││││├┤ └─┐│││├┤ ├┤ ├─┘├┤ ├┬┘\n" + 
    			"╩ ╩┴┘└┘└─┘└─┘└┴┘└─┘└─┘┴  └─┘┴└─\n" + 
    			"||||||||LETS GET KILLED!|||||||\n");
        userInterface = new ConsoleUI();
        
        Field field = new Field(9, 9, 10);
        userInterface.newGameStarted(field);

        System.currentTimeMillis();

    }

    /**
     * Main method.
     * @param args arguments
     */
    public static void main(String[] args) {
        new Minesweeper();
    }
    
    /** Returns the play time
     * 
     */
    public int getPlayingSeconds() {
    	return 0;
    }
}
