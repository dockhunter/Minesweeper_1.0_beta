package minesweeper.consoleui;

import java.io.BufferedReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.io.InputStreamReader;

import minesweeper.UserInterface;
import minesweeper.core.Clue;
import minesweeper.core.Field;
import minesweeper.core.GameState;
import minesweeper.core.Mine;
import minesweeper.core.Tile;

/**
 * Console user interface.
 */
public class ConsoleUI implements UserInterface {
	/** Playing field. */
	private Field field;

	/** Input reader. */
	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * Reads line of text from the reader.
	 * 
	 * @return line as a string
	 */
	private String readLine() {
		try {
			return input.readLine();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Starts the game.
	 * 
	 * @param field field of mines and clues
	 */
	@Override
	public void newGameStarted(Field field) {
		this.field = field;
		do {
			update();
			processInput();
			if (field.getState() == GameState.SOLVED){
				System.out.println(
						"┬ ┬┌─┐┬ ┬  ┬ ┬┌─┐┌┐┌┬\n" + 
						"└┬┘│ ││ │  ││││ │││││\n" + 
						" ┴ └─┘└─┘  └┴┘└─┘┘└┘o"
						+ "LUCKY BASTARD...");
				System.exit(0);
				return;
			} else if (field.getState() == GameState.FAILED) {
				update();
				System.err.println(
						"╔═╗╔═╗╔╦╗╔═╗  ╔═╗╦  ╦╔═╗╦═╗\n" + 
						"║ ╦╠═╣║║║║╣   ║ ║╚╗╔╝║╣ ╠╦╝\n" + 
						"╚═╝╩ ╩╩ ╩╚═╝  ╚═╝ ╚╝ ╚═╝╩╚═"
						+ "\nYOU DIED YOU IDIOT! HAHAHA");
				System.exit(0);
				return;
			}
		} while (field.getState() == GameState.PLAYING);
	}

	/**
	 * Updates user interface - prints the field.
	 */
	@Override
	public void update() {
		System.out.print("  ");
		for (int x = 0; x < field.getColumnCount(); x++) {
			System.out.printf("%3d", x);
		}
		for (int x = 0; x < field.getRowCount(); x++) {
			System.out.printf("%n%2c", x + 'A');
			for (int z = 0; z < field.getColumnCount(); z++) {
				Tile till = field.getTile(x, z);
				if (till instanceof Mine && till.getState() == Tile.State.OPEN) {
					System.err.printf("%3c", 'X');
				} else if (till instanceof Clue && till.getState() == Tile.State.OPEN) {
					Clue clue = (Clue) till;
					System.out.print("  " + clue.getValue());
				} else if (till.getState() == Tile.State.MARKED) {
					System.out.printf("%3c", 'M');
				} else if (till.getState() == Tile.State.CLOSED) {
					System.out.printf("%3c", '-');
				}
			}
		}
		System.out.println("\n\n-------------------------------"
				+ "\nNUMBER OF REMAINING MINES: " + field.getRemainingMineCount() + "\n");
	}

	/**
	 * Processes user input. Reads line from console and does the action on a
	 * playing field according to input string.
	 */
	private void processInput() {
		
		System.out.println("Enter your deadly choice: "
				+ "\n<M> MARK A TILE + ENTER ROW & COLUMN"
				+ "\n<O> OPEN A TILE + ENtER ROW & COLUMN"
				+ "\n<X> EXIT GAME"
				+ "\n-------------------------------");
		try {		
			handleInput(readLine());
		} catch (WrongFormatException ex) {
			System.err.println("Wrong format. " + ex.getMessage());
		}

	}
	
	/**
	 * Processes user input with an exception handler.
	 *  
	 * throws WrongFormatException
	 */
	private void handleInput(String input) throws WrongFormatException {
	
		String answer;
		answer = input;

		Pattern pattern = Pattern.compile("(O|M)([A-I])([0-8])", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(answer.toUpperCase());
		if (answer.equalsIgnoreCase("X")) {
			System.err.println("GAME OVER! PUSSY! \nYOU FAILED ME!!");
			System.exit(0);
		} else if (matcher.matches()) {
			String row = matcher.group(2);
			char c = row.charAt(0);
			int x = (c + 1 - 'A') - 1;
			String column = matcher.group(3);
			int y = Integer.parseInt(column);
			if (matcher.group(1).charAt(0) == 'M') {
				field.markTile(x, y);
			} else if (matcher.group(1).charAt(0) == 'O') {
				field.openTile(x, y);
			}
		} else if (!matcher.matches()) {
			System.err.println("NOSENSE! There is no such tile!");
		}
	}

}
