import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * This Level class is responsible for managing all of the objects in your game
 * The GameEngine creates a new Level object for each level, and then calls 
 * that Level object's update() method repeatedly until it returns either 
 * "ADVANCE" (to proceed to the next level) or "QUIT" (to end the entire game).
 * <br/><br/>
 * This class should contain and make use of the following private fields:
 * <tt><ul>
 * <li>private Random rng</li>
 * <li>private Taxi taxi</li>
 * <li>private ArrayList<WarpStar> warpStars</li>
 * <li>private ArrayList<GasCloud> gasClouds</li>
 * <li>private ArrayList<Planet> planets</li>
 * <li>private int destinationPlanetIndex</li>
 * </ul></tt>
 */
public class Level
{
	private Random rng;
	private Taxi taxi;
	private ArrayList<WarpStar> warpStars;
	private ArrayList<GasCloud> gasClouds;
	private ArrayList<Planet> planets;
	private int destinationPlanetIndex;

	/**
	 * This constructor initializes a new level object, so that the GameEngine
	 * can begin calling its update() method to advance the game's play.  In
	 * the process of this initialization, all of the objects in the current
	 * level should be instantiated and initialized to their starting states.
	 * @param rng is the ONLY Random number generator that should be used by 
	 * throughout this level and by any of the objects within it.
	 * @param levelFilename is either null (when a random level should be 
	 * loaded) or a reference to the custom level file that should be loaded.
	 */
	public Level(Random rng, String levelFilename) {
		this.rng = rng;
		if (levelFilename == null) loadRandomLevel();
		else {
			if (!loadCustomLevel(levelFilename)) loadRandomLevel();
		}

		destinationPlanetIndex = 0;
		planets.get(0).setDestination(true);
	}

	/**
	 * The GameEngine calls this method repeatedly to update all of the objects
	 * within your game, and to enforce all of your game's rules.
	 * @param time is the time in milliseconds that have elapsed since the last
	 * time this method was called (or your constructor was called). This can 
	 * be used to help control the speed of moving objects within your game.
	 * @return "CONTINUE", "ADVANCE", or "QUIT".  When this method returns
	 * "CONTINUE" the GameEngine will continue to play your game by repeatedly
	 * calling it's update() method.  Returning "ADVANCE" instructs the 
	 * GameEngine to end the current level, create a new level, and to start
	 * updating that new level object instead of the current one. Returning 
	 * "QUIT" instructs the GameEngine to end the entire game.  In the case of
	 * either "QUIT" or "ADVANCE" being returned, the GameEngine presents a
	 * short pause and transition message to help the player notice the change.
	 */
	public String update(int time) {
		if (taxi.update(time)) {
			return "QUIT";
		}

		for (WarpStar warpStar: warpStars) warpStar.update(time);
		for (GasCloud gasCloud: gasClouds) gasCloud.update(time);
		for (Planet planet: planets) planet.update(time);

		for (GasCloud gasCloud: gasClouds) gasCloud.handleFueling(taxi);

		for (int i = gasClouds.size() - 1; i >= 0; i--) if (gasClouds.get(i).shouldRemove()) gasClouds.remove(i);

		for (WarpStar warpStar: warpStars) warpStar.handleNavigation(taxi);

		for (Planet planet: planets) {
			if (planet.handleLanding(taxi)) {
				planet.setDestination(false);
				destinationPlanetIndex++;

				if (destinationPlanetIndex >= planets.size()) return "ADVANCE";

				planets.get(destinationPlanetIndex).setDestination(true);
			}
		}


		return "CONTINUE";
	}	

	/**
	 * This method returns a string of text that will be displayed in the upper
	 * left hand corner of the game window.  Ultimately this text should convey
	 * the taxi's fuel level, and their progress through the destinations.
	 * However, this may also be useful for temporarily displaying messages
	 * that help you to debug your game.
	 * @return a string of text to be displayed in the upper-left hand corner
	 * of the screen by the GameEngine.
	 */
	public String getHUDMessage() {
		if (taxi.getFuel() < 0.0000001) return "You've run out of fuel!\nPress the SPACEBAR to end this game.";
		else if (taxi.hasCrashed()) return "You've crashed into a planet!\nPress the SPACEBAR to end this game.";
		else return String.format("Fuel: %.1f\nFares: " + destinationPlanetIndex + "/ " +
					planets.size(), taxi.getFuel()) ;
	}

	/**
	 * This method initializes the current level to contain a single taxi in 
	 * the center of the screen, along with 6 randomly positioned objects of 
	 * each of the following types: warp stars, gasClouds, and planets.
	 */
	private void loadRandomLevel() {
		taxi = new Taxi(GameEngine.getWidth() / 2, GameEngine.getHeight() / 2);

		warpStars = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			warpStars.add(new WarpStar(rng.nextInt(GameEngine.getWidth()), rng.nextInt(GameEngine.getHeight())));
		}

		gasClouds = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			gasClouds.add(new GasCloud(rng.nextInt(GameEngine.getWidth()), rng.nextInt(GameEngine.getHeight()), 0));
		}

		planets = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			planets.add(new Planet(rng, planets));
		}
	}

	/**
	 * Tbis method initializes the current level to contain each of the objects
	 * described in the lines of text from the specified file.  Each line in
	 * this file contains the type of an object followed by the position that
	 * it should be initialized to start the level.
	 * @param levelFilename is the name of the file (relative to the current
	 * working directory) that these object types and positions are loaded from
	 * @return true after the specified file's contents are successfully loaded
	 * and false whenever any problems are encountered related to this loading
	 */
	private boolean loadCustomLevel(String levelFilename) {
		warpStars = new ArrayList<>();
		gasClouds = new ArrayList<>();
		planets = new ArrayList<>();

		try {
			File file = new File(levelFilename);
			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.length() == 0) continue;

				String [] splitLine = line.split(" ");
				splitLine[2] = splitLine[2].substring(0, splitLine[2].length() - 1);

				if (splitLine[0].equals("GAS_CLOUD")) {
					gasClouds.add(new GasCloud(Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), 0));
				} else if (splitLine[0].equals("PLANET")) {
					planets.add(new Planet(Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3])));
				} else if (splitLine[0].equals("WARP_STAR")) {
					warpStars.add(new WarpStar(Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3])));
				} else if (splitLine[0].equals("TAXI")) {
					taxi = new Taxi(Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
				}
			}

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * This method creates and runs a new GameEngine with its first Level. Any
	 * command line arguments passed into this program are treated as a list of
	 * custom level filenames that should be played in order by the player.
	 * @param args is the sequence of custome level filenames to play through
	 */
	public static void main(String[] args) {
		GameEngine.start(null,args);
	} 
}
