package hw3;
/**
 * @author Abhay Prasanna Rao
 */
import java.util.Random;
import java.util.ArrayList;
import java.util.List;



import api.ScoreUpdateListener;
import api.ShowDialogListener;
import api.Tile;

/** 
 * Class that models a game.
 */
public class ConnectGame {

	/**
	 * Listener for displaying dialogs to the user.
	 */
	private ShowDialogListener dialogListener;

	/**
	 * Listener for updating the score in the user interface.
	 */
	private ScoreUpdateListener scoreListener;

	/**
	 * The grid representing the game board.
	 */
	private Grid grid;

	/**
	 * The minimum level of tiles allowed on the grid.
	 */
	private int minTileLevel;

	/**
	 * The maximum level of tiles allowed on the grid.
	 */
	private int maxTileLevel;

	/**
	 * A random number generator for generating new tiles.
	 */
	private Random rand;

	/**
	 * The current score of the game.
	 */
	private long score;

	/**
	 * A list of the currently selected tiles in the game.
	 */
	private List<Tile> selectedTiles = new ArrayList<>();

	/**
	 * Constructs a new ConnectGame object with given grid dimensions and minimum
	 * and maximum tile levels.
	 * 
	 * @param width  grid width
	 * @param height grid height
	 * @param min    minimum tile level
	 * @param max    maximum tile level
	 * @param rand   random number generator
	 */
	public ConnectGame(int width, int height, int min, int max, Random rand) {
		this.grid = new Grid(width, height);
		this.minTileLevel = min;
		this.maxTileLevel = max;
		this.rand = rand;
		this.score = 0;
		this.selectedTiles = new ArrayList<>();
		radomizeTiles();
	}

	/**
	 * Gets a random tile with level between minimum tile level inclusive and
	 * maximum tile level exclusive. For example, if minimum is 1 and maximum is 4,
	 * the random tile can be either 1, 2, or 3.
	 * <p>
	 * DO NOT RETURN TILES WITH MAXIMUM LEVEL
	 * 
	 * @return a tile with random level between minimum inclusive and maximum
	 *         exclusive
	 */
	public Tile getRandomTile() {
		int level = rand.nextInt(maxTileLevel - minTileLevel) + minTileLevel;
		return new Tile(level);
	}

	/**
	 * Regenerates the grid with all random tiles produced by getRandomTile().
	 */
	public void radomizeTiles() {
		for (int i = 0; i < grid.getWidth(); i++) {
			for (int j = 0; j < grid.getHeight(); j++) {
				grid.setTile(getRandomTile(), i, j);
			}
		}
	}

	/**
	 * Determines if two tiles are adjacent to each other. The may be next to each
	 * other horizontally, vertically, or diagonally.
	 * 
	 * @param t1 one of the two tiles
	 * @param t2 one of the two tiles
	 * @return true if they are next to each other horizontally, vertically, or
	 *         diagonally on the grid, false otherwise
	 */
	public boolean isAdjacent(Tile t1, Tile t2) {
		int dx = Math.abs(t1.getX() - t2.getX());
		int dy = Math.abs(t1.getY() - t2.getY());

		return (dx <= 1 && dy <= 1) && !(dx == 0 && dy == 0);
	}


	/**
	 * Indicates the user is trying to select (clicked on) a tile to start a new
	 * selection of tiles.
	 * <p>
	 * If a selection of tiles is already in progress, the method should do nothing
	 * and return false.
	 * <p>
	 * If a selection is not already in progress (this is the first tile selected),
	 * then start a new selection of tiles and return true.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return true if this is the first tile selected, otherwise false
	 */
	public boolean tryFirstSelect(int x, int y) {
		if (selectedTiles.isEmpty()) {
			Tile tile = grid.getTile(x, y);
			tile.setSelect(true);
			selectedTiles.add(tile);
			return true;
		}
		return false;
	}


	/**
	 * Indicates the user is trying to select (mouse over) a tile to add to the
	 * selected sequence of tiles. The rules of a sequence of tiles are:
	 * 
	 * <pre>
	 * 1. The first two tiles must have the same level.
	 * 2. After the first two, each tile must have the same level or one greater than the level of the previous tile.
	 * </pre>
	 * 
	 * For example, given the sequence: 1, 1, 2, 2, 2, 3. The next selected tile
	 * could be a 3 or a 4. If the use tries to select an invalid tile, the method
	 * should do nothing. If the user selects a valid tile, the tile should be added
	 * to the list of selected tiles.
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 */

	public void tryContinueSelect(int x, int y) {
		// If no initial tile is chosen, do nothing
		if (selectedTiles.isEmpty()) {
			return;
		}
		Tile nextTile = grid.getTile(x, y);
		Tile previousTile = selectedTiles.get(selectedTiles.size() - 1);
		// Verify if the tiles are adjacent, if not, do nothing
		boolean adjacent = isAdjacent(previousTile, nextTile);
		if (!adjacent) {
			return;
		}

		// If the first two tiles share the same level, add the tile to selectedTiles
		if (selectedTiles.size() == 1) {
			if (previousTile.getLevel() == nextTile.getLevel()) {
				selectedTiles.add(nextTile);
				nextTile.setSelect(true);
			}
		} else if (selectedTiles.size() >= 2) {
			Tile pinFinalTile = selectedTiles.get(selectedTiles.size() - 2);
			// If the penultimate tile (in selectedTiles) matches the nextTile, deselect the last tile.
			if (nextTile.getX() == pinFinalTile.getX() && nextTile.getY() == pinFinalTile.getY()) {
				unselect(previousTile.getX(), previousTile.getY());
			}
			// After the first pair, each subsequent tile must have the same level or one greater than the level of the preceding tile.
			else if ((nextTile.getLevel() == previousTile.getLevel()) || (nextTile.getLevel() == previousTile.getLevel() + 1)) {
				selectedTiles.add(nextTile);
				nextTile.setSelect(true);
			}
		}

	}


	/**
	 * Indicates the user is trying to finish selecting (click on) a sequence of
	 * tiles. If the method is not called for the last selected tile, it should do
	 * nothing and return false. Otherwise it should do the following:
	 * 
	 * <pre>
	 * 1. When the selection contains only 1 tile reset the selection and make sure all tiles selected is set to false.
	 * 2. When the selection contains more than one block:
	 *     a. Upgrade the last selected tiles with upgradeLastSelectedTile().
	 *     b. Drop all other selected tiles with dropSelected().
	 *     c. Reset the selection and make sure all tiles selected is set to false.
	 * </pre>
	 * 
	 * @param x the column of the tile selected
	 * @param y the row of the tile selected
	 * @return return false if the tile was not selected, otherwise return true
	 */
	public boolean tryFinishSelection(int x, int y) {
		if(selectedTiles.size()<1) {
			return false;
		}
		Tile lastSelectedTile = selectedTiles.get(selectedTiles.size() - 1);
		if (lastSelectedTile.getX() == x && lastSelectedTile.getY() == y) {
			if (selectedTiles.size() == 1) {
				unselect(x, y);
				selectedTiles.clear();
			} else {
				// Calculate the score for the selected tiles
				long selectedScore = 0;
				for (Tile tile : selectedTiles) {
					selectedScore += (int) Math.pow(2, tile.getLevel());
				}

				// Upgrade the last selected tile
				upgradeLastSelectedTile();

				// Update the player's score
				score += selectedScore;
				if (scoreListener != null) {
					scoreListener.updateScore(score);
				}

				// Drop all selected tiles except the last one
				for (int i = 0; i < selectedTiles.size() - 1; i++) {
					Tile tile = selectedTiles.get(i);
					tile.setSelect(false);
					unselect(tile.getX(), tile.getY());
				}

				// Update the removed tiles with new tiles
				dropSelected();

				// Unselect the last selected tile
				lastSelectedTile.setSelect(false);

				// Clear the selected tiles list
				selectedTiles.clear();


			}
			return true;
		}
		return false;
	}


	/**
	 * Increases the level of the last selected tile by 1 and removes that tile from
	 * the list of selected tiles. The tile itself should be set to unselected.
	 * <p>
	 * If the upgrade results in a tile that is greater than the current maximum
	 * tile level, both the minimum and maximum tile level are increased by 1. A
	 * message dialog should also be displayed with the message "New block 32,
	 * removing blocks 2". Not that the message shows tile values and not levels.
	 * Display a message is performed with dialogListener.showDialog("Hello,
	 * World!");
	 */
	public void upgradeLastSelectedTile() {
		if (selectedTiles.isEmpty()) {
			return;
		}

		Tile lastSelectedTile = selectedTiles.get(selectedTiles.size() - 1);
		int newLevel = lastSelectedTile.getLevel() + 1;
		lastSelectedTile.setLevel(newLevel);
		lastSelectedTile.setSelect(false);
		selectedTiles.remove(selectedTiles.size() - 1);

		if (newLevel > maxTileLevel) {
			minTileLevel += 1; 
			maxTileLevel += 1;
			int newValue = (int) Math.pow(2, newLevel);
			int oldValue = (int) Math.pow(2, minTileLevel - 1);
			dialogListener.showDialog("New block " + newValue + ", removing blocks " + oldValue);
		}
	}


	/**
	 * Gets the selected tiles in the form of an array. This does not mean selected
	 * tiles must be stored in this class as a array.
	 * 
	 * @return the selected tiles in the form of an array
	 */
	public Tile[] getSelectedAsArray() {
		return selectedTiles.toArray(new Tile[0]);

	}

	/**
	 * Removes all tiles of a particular level from the grid. When a tile is
	 * removed, the tiles above it drop down one spot and a new random tile is
	 * placed at the top of the grid.
	 * 
	 * @param level the level of tile to remove
	 */
	public void dropLevel(int level) {
		for (int x = 0; x < grid.getWidth(); x++) {
			int gap = 0; // Counter to track how many tiles with the specified level were found

			for (int y = grid.getHeight() - 1; y >= 0; y--) {
				Tile currentTile = grid.getTile(x, y);
				if (currentTile.getLevel() == level) {
					gap++;
				} else if (gap > 0) {
					grid.setTile(currentTile, x, y + gap);
					grid.setTile(null, x, y);
				}
			}

			// Fill the gaps with new random tiles
			for (int i = 0; i < gap; i++) {
				Tile newTile = getRandomTile();
				grid.setTile(newTile, x, i);
			}
		}
	}

	/**
	 * Removes all selected tiles from the grid. When a tile is removed, the tiles
	 * above it drop down one spot and a new random tile is placed at the top of the
	 * grid.
	 */
	public void dropSelected() {

		Tile[] selectedTiles = getSelectedAsArray();

		for (Tile tile : selectedTiles) {
			int x = tile.getX();
			int y = tile.getY();

			// Shift the tiles above the selected tile down one position
			for (int i = y; i > 0; i--) {
				grid.setTile(grid.getTile(x, i - 1), x, i);
			}

			// Add a new random tile at the top of the column
			Tile newTile = getRandomTile();
			grid.setTile(newTile, x, 0);
		}

	}

	/**
	 * Remove the tile from the selected tiles.
	 * 
	 * @param x column of the tile
	 * @param y row of the tile
	 */
	public void unselect(int x, int y) {
		Tile tile = grid.getTile(x, y);
		tile.setSelect(false);
		selectedTiles.remove(tile);
	}

	/**
	 * Gets the player's score.
	 * 
	 * @return the score
	 */
	public long getScore() {
		return score;
	}

	/**
	 * Gets the game grid.
	 * 
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
	}

	/**
	 * Gets the minimum tile level.
	 * 
	 * @return the minimum tile level
	 */
	public int getMinTileLevel() {
		return minTileLevel;
	}

	/**
	 * Gets the maximum tile level.
	 * 
	 * @return the maximum tile level
	 */
	public int getMaxTileLevel() {
		return maxTileLevel;
	}

	/**
	 * Sets the player's score.
	 * 
	 * @param score number of points
	 */
	public void setScore(long score) {
		this.score = score;
	}

	/**
	 * Sets the game's grid.
	 * 
	 * @param grid game's grid
	 */
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	/**
	 * Sets the minimum tile level.
	 * 
	 * @param minTileLevel the lowest level tile
	 */
	public void setMinTileLevel(int minTileLevel) {
		this.minTileLevel = minTileLevel;
	}

	/**
	 * Sets the maximum tile level.
	 * 
	 * @param maxTileLevel the highest level tile
	 */
	public void setMaxTileLevel(int maxTileLevel) {
		this.maxTileLevel = maxTileLevel;
	}

	/**
	 * Sets callback listeners for game events.
	 * 
	 * @param dialogListener listener for creating a user dialog
	 * @param scoreListener  listener for updating the player's score
	 */
	public void setListeners(ShowDialogListener dialogListener, ScoreUpdateListener scoreListener) {
		this.dialogListener = dialogListener;
		this.scoreListener = scoreListener;
	}

	/**
	 * Save the game to the given file path.
	 * 
	 * @param filePath location of file to save
	 */
	public void save(String filePath) {
		GameFileUtil.save(filePath, this);
	}

	/**
	 * Load the game from the given file path
	 * 
	 * @param filePath location of file to load
	 */
	public void load(String filePath) {
		GameFileUtil.load(filePath, this);
	}

}
