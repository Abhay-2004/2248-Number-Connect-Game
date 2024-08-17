package hw3;
/**
 * @author Abhay Prasanna Rao
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class with static methods for saving and loading game files.
 */
public class GameFileUtil {
	/**
	 * Saves the current game state to a file at the given file path.
	 * <p>
	 * The format of the file is one line of game data followed by multiple lines of
	 * game grid. The first line contains the: width, height, minimum tile level,
	 * maximum tile level, and score. The grid is represented by tile levels. The
	 * conversion to tile values is 2^level, for example, 1 is 2, 2 is 4, 3 is 8, 4
	 * is 16, etc. The following is an example:
	 * 
	 * <pre>
	 * 5 8 1 4 100
	 * 1 1 2 3 1
	 * 2 3 3 1 3
	 * 3 3 1 2 2
	 * 3 1 1 3 1
	 * 2 1 3 1 2
	 * 2 1 1 3 1
	 * 4 1 3 1 1
	 * 1 3 3 3 3
	 * </pre>
	 * 
	 * @param filePath the path of the fileu to save
	 * @param game     the game to save
	 */
	public static void save(String filePath, ConnectGame game) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	        Grid grid = game.getGrid();
	        writer.write(grid.getWidth() + " " + grid.getHeight() + " " + game.getMinTileLevel() + " " + game.getMaxTileLevel() + " " + game.getScore() + "\n");
	        for (int y = 0; y < grid.getHeight(); y++) {
	            for (int x = 0; x < grid.getWidth(); x++) {
	                writer.write(grid.getTile(x, y).getLevel() + (x < grid.getWidth() - 1 ? " " : ""));
	            }
	            if (y < grid.getHeight() - 1) {
	                writer.write("\n");
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	
	
	/**
	 * Loads the file at the given file path into the given game object. When the
	 * method returns the game object has been modified to represent the loaded
	 * game.
	 * <p>
	 * See the save() method for the specification of the file format.
	 * 
	 * @param filePath the path of the file to load
	 * @param game     the game to modify
	 */
	public static void load(String filePath, ConnectGame game) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	        String[] gameData = reader.readLine().split(" ");
	        int width = Integer.parseInt(gameData[0]);
	        int height = Integer.parseInt(gameData[1]);
	        int minTileLevel = Integer.parseInt(gameData[2]);
	        int maxTileLevel = Integer.parseInt(gameData[3]);
	        long score = Long.parseLong(gameData[4]);

	        Grid grid = new Grid(width, height);
	        for (int y = 0; y < height; y++) {
	            String[] line = reader.readLine().split(" ");
	            for (int x = 0; x < width; x++) {
	                int level = Integer.parseInt(line[x]);
	                api.Tile tile = new api.Tile(level); 
	                
	                
	                grid.setTile(tile, x, y);
	            }
	        }

	        game.setGrid(grid);
	        game.setMinTileLevel(minTileLevel);
	        game.setMaxTileLevel(maxTileLevel);
	        game.setScore(score);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	}
		
	

