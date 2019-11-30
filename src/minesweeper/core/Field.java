package minesweeper.core;

import java.util.Random;

/**
 * Field represents playing field and game logic.
 */
public class Field {
	/**
	 * Playing field tiles.
	 */
	private final Tile[][] tiles;

	/**
	 * Field row count. Rows are indexed from 0 to (rowCount - 1).
	 */
	private final int rowCount;

	/**
	 * Column count. Columns are indexed from 0 to (columnCount - 1).
	 */
	private final int columnCount;

	/**
	 * Mine count.
	 */
	private final int mineCount;

	/**
	 * Game state.
	 */
	private GameState state = GameState.PLAYING;

	/**
	 * Constructor.
	 *
	 * @param rowCount    row count
	 * @param columnCount column count
	 * @param mineCount   mine count
	 */
	public Field(int rowCount, int columnCount, int mineCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.mineCount = mineCount;
		tiles = new Tile[rowCount][columnCount];

		// generate the field content
		generate();
	}

	/**
	 * Opens tile at specified indeces.
	 *
	 * @param row    row number
	 * @param column column number
	 */
	public void openTile(int row, int column) {
		Tile tile = tiles[row][column];
		if (tile.getState() == Tile.State.CLOSED) {
			tile.setState(Tile.State.OPEN);
			if (tile instanceof Mine) {
				setState(GameState.FAILED);
				return;
			} else if  (tile instanceof Clue) {
				Clue clue = (Clue) tile;
				if (clue.getValue() == 0 ) {
					openAdjacentTiles(row, column);
					return;
				}
			} 

			if (isSolved()) {
				setState(GameState.SOLVED);
				return;
			}
		}
	}

	/**
	 * Marks tile at specified indeces.
	 *
	 * @param row    row number
	 * @param column column number
	 */
	public void markTile(int row, int column) {
		Tile tile = tiles[row][column];
		if (tile.getState() == Tile.State.CLOSED) {
			tile.setState(Tile.State.MARKED);
			return;
		}
		if (tile.getState() == Tile.State.MARKED) {
			tile.setState(Tile.State.CLOSED);
			return;
		}
	}

	/**
	 * Generates playing field.
	 */
	private void generate() {
		Random rnd = new Random();
		int freeMines = mineCount;

		while (freeMines != 0) {
			int rndColumn = rnd.nextInt(columnCount);
			int rndRow = rnd.nextInt(rowCount);
			if (tiles[rndRow][rndColumn] == null) {
				tiles[rndRow][rndColumn] = new Mine();
				--freeMines;
			}
		}
		for (int x = 0; x < rowCount; x++) {
			for (int z = 0; z < columnCount; z++) {
				if (tiles[x][z] == null) {
					tiles[x][z] = new Clue(countAdjacentMines(x, z));
				}
			}
		}
	}

	/**
	 * Returns true if game is solved, false otherwise.
	 *
	 * @return true if game is solved, false otherwise
	 */
	private boolean isSolved() {
		if ((rowCount * columnCount) - getNumberOf(Tile.State.OPEN) == getMineCount()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns number of adjacent mines for a tile at specified position in the
	 * field.
	 *
	 * @param row    row number.
	 * @param column column number.
	 * @return number of adjacent mines.
	 */
	private int countAdjacentMines(int row, int column) {
		int count = 0;
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int actRow = row + rowOffset;
			if (actRow >= 0 && actRow < getRowCount()) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int actColumn = column + columnOffset;
					if (actColumn >= 0 && actColumn < getColumnCount()) {
						if (tiles[actRow][actColumn] instanceof Mine) {
							count++;
						}
					}
				}
			}
		}

		return count;
	}

	/**
	 * Opens all adjacent tiles where mines are absent and clues have value of 0.
	 * 
	 * @param row    row number.
	 * @param column column number.
	 */
	private void openAdjacentTiles(int row, int column) {
		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			int actRow = row + rowOffset;
			if (actRow >= 0 && actRow < getRowCount()) {
				for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {
					int actColumn = column + columnOffset;
					if (actColumn >= 0 && actColumn < getColumnCount()) {
						Tile tile = tiles[actRow][actColumn];
						if (tile instanceof Clue) {
							Clue clue = (Clue) tile;
							if (clue.getValue() == 0 && tile.getState() == Tile.State.CLOSED) {
								tile.setState(Tile.State.OPEN);
								openAdjacentTiles(actRow, actColumn);							
							} else if (clue.getValue() > 0 && tile.getState() == Tile.State.CLOSED  && !(tile instanceof Mine) ) {
								tile.setState(Tile.State.OPEN);
							} else if (clue.getValue() == 0 && tile.getState() == Tile.State.OPEN) {
								continue;
							} else if (clue.getValue() > 0 && tile.getState() == Tile.State.OPEN) {
								break;
							}
						}
					}
				}
			}
		}

	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public int getMineCount() {
		return mineCount;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public Tile getTile(int row, int column) {
		return tiles[row][column];
	}
	
	/**
	 * Gets the number of tiles by a given state.
	 * 
	 * @param state
	 * @return
	 */
	private int getNumberOf(Tile.State state) {
		int count = 0;
		for (int x = 0; x < rowCount; x++) {
			for (int y = 0; y < columnCount; y++) {
				Tile tile = tiles[x][y];
				if (tile.getState() == state) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Gets the number of remaining mines while the number of mines are higher or equal to number of marks.
	 * Otherwise stays on 0.
	 * 
	 * 
	 * @return
	 */
	public int getRemainingMineCount() {
		
		int numberOfMarks = getNumberOf(Tile.State.MARKED);
		int numberOfMines = getMineCount();
		int remainingMines = numberOfMines - numberOfMarks;
		if (numberOfMines < numberOfMarks) {
			remainingMines = 0;
		} 
		return remainingMines;
	}

}
