package com.faizaand.hamlet.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.faizaand.hamlet.AssetManager;
import com.faizaand.hamlet.GameScreen;
import com.faizaand.hamlet.HamletGame;
import com.faizaand.hamlet.entity.EntityPlayer;

/**
 * Stores map and player data for the level.
 * 
 * @author Faizaan Datoo
 */
public class Level {

	private HamletGame gameInstance;
	private GameScreen gameScreen;

	public String levelAssetKey; // Assigned to this level in AssetManager

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private EntityPlayer player;
//	private EntityKey key;
	// TODO There's no key in this game

	public LevelData levelData; // Data about the level
	private Json json; // JSON object for file writing.

	private float playerSpawnX, playerSpawnY; // Player spawn location
	private float yBottom; // The bottom of the map, for camera positioning
	private float mapEnd; // The end of the map, for camera positioning

	/**
	 * This constructor is to be used only in the LevelManager class. GameScreen
	 * should use the prepare method to pass in a camera and a player object.
	 */
	protected Level(String levelAssetKey) {
		this.levelAssetKey = levelAssetKey;
		this.map = AssetManager.getInstance().getMap(levelAssetKey);

		this.levelData = new LevelData();
		this.json = new Json(OutputType.minimal);
		loadData();

		renderer = new OrthogonalTiledMapRenderer(map, GameScreen.UNIT_SCALE);
	}

	/**
	 * This method should be called by the GameScreen class upon load. This sets
	 * up the level so it is ready to be rendered.
	 */
	public void prepare(OrthographicCamera camera, EntityPlayer player,
						HamletGame gameInstance, GameScreen gameScreen) {
		this.camera = camera;
		this.player = player;
		this.gameInstance = gameInstance;
		this.gameScreen = gameScreen;

		loadObjects();

		// Set player position to the spawn point
		player.position.set(playerSpawnX, playerSpawnY);
		camera.position.x = player.position.x;
		camera.position.y = player.position.y + 0.5f;
		camera.update();
	}

	/**
	 * Load all the objects from the "entities" layer in the map.
	 */
	private void loadObjects() {
		// Get the entities layer from the map
		MapLayer entitiesLayer = map.getLayers().get("entities");
		MapObjects entitiesObjects = entitiesLayer.getObjects();

		loadMapData(entitiesObjects);
		loadMapEntities(entitiesObjects);

	}

	/**
	 * Load data from the map.
	 * @param entitiesObjects The objects on the entities layer
	 */
	private void loadMapData(MapObjects entitiesObjects) {
		// Get the ybottom from the map
		this.yBottom = entitiesObjects.get("bottom").getProperties()
				.get("y", Float.class)
				* GameScreen.UNIT_SCALE;
		this.camera.position.y = this.yBottom;

		// Get the end of the map, which indicates where the camera should stop
		// moving.
		this.mapEnd = entitiesObjects.get("end").getProperties()
				.get("x", Float.class)
				* GameScreen.UNIT_SCALE;
	}

	/**
	 * Load entities from the map.
	 * @param entitiesObjects The objects on the entities layer
	 */
	private void loadMapEntities(MapObjects entitiesObjects) {
		// Get the key location from the map.
//		this.key = new EntityKey();
//		MapProperties keyProperties = entitiesObjects.get("key").getProperties();
//		float keySpawnX = keyProperties.get("x", Float.class) * GameScreen.UNIT_SCALE;
//		float keySpawnY = keyProperties.get("y", Float.class) * GameScreen.UNIT_SCALE;
//		this.key.position.set(keySpawnX, keySpawnY);

		// Get the player spawn position from the map
		MapProperties playerProperties = entitiesObjects.get("player")
				.getProperties();
		this.playerSpawnX = playerProperties.get("x", Float.class)
				* GameScreen.UNIT_SCALE;
		this.playerSpawnY = playerProperties.get("y", Float.class)
				* GameScreen.UNIT_SCALE;
	}

	/**
	 * Update all entities and the level.
	 * @param delta Used to calculate how much to update.
	 */
	public void update(float delta) {
		// If the game is frozen, don't update or this will cause glitching
		if (delta == 0) return;

		// Update the player
		player.update(delta, this.map);

		// Check if the player is dead
		if (!player.alive) {
			// Switch to the math screen
			AssetManager.getInstance().getSound("player-death").play();
//			gameInstance.setScreen(new MathScreen(gameInstance));
			// TODO There's no math in this game!
			return;
		}

		// Check if player is trying to go off the map
		checkPlayerPosition();

		// Check if the player got the key
//		checkForKeyCollision();

		// Make the camera follow the player
		adjustCamera();

	}

	public void render() {
		// Render the map
		renderer.setView(camera);
		renderer.render();

		// Render the player
		player.render(renderer.getBatch());

		// Render the key
//		key.render(renderer.getBatch());
	}
	
	/**
	 * Load data about this map
	 */
	public void loadData() {
		FileHandle levelHandle = Gdx.files.local("levels/" + levelAssetKey + ".json");
		if (!levelHandle.exists()) save(); // Create the LevelData file.

		this.levelData = json.fromJson(LevelData.class, levelHandle);
	}

	/**
	 * Save the data about this map
	 */
	public void save() {
		FileHandle levelHandle = Gdx.files.local("levels/" + levelAssetKey + ".json");
		levelHandle.writeString(json.toJson(levelData), false);
	}

	/**
	 * Dispose of all resources this map may be using.
	 */
	public void dispose() {
		// Dispose of all resources to free memory
		map.dispose();
		renderer.dispose();
		if (player != null) player.dispose();
	}

	public int getMapWidth() {
		return map.getProperties().get("width", Integer.class);
	}

	public float getMapEnd() {
		return this.mapEnd;
	}

	public float getMapBottom() {
		return this.yBottom;
	}

	/**
	 * Adjust the camera position to follow the player.
	 */
	private void adjustCamera() {
		camera.position.x = player.position.x;

		// Stop the camera if it goes off the map so the player does not see
		// past the edge.
		if (camera.position.x < 8) {
			camera.position.x = 8;
		}

		float stopOffset = 8; // Make the camera stop 8 units before the end.
		if (camera.position.x > this.mapEnd - stopOffset) {
			camera.position.x = this.mapEnd - stopOffset;
		}

		camera.update();
	}

	/**
	 * Stop player if it is trying to go off the map (so it doesn't fall off!)
	 */
	private void checkPlayerPosition() {
		if (player.position.x <= 0.5f) player.position.x = 0.5f;

		if (player.position.x >= mapEnd - 1) player.position.x = mapEnd - 1;
	}

	/**
	 * Check to see if the player collided with the key, and go to the next
	 * level.
	 */
	/*
	private void checkForKeyCollision() {
		Rectangle playerRect = player.getBoundingBox();
		Rectangle keyRect = key.getBoundingBox();
		if (playerRect.overlaps(keyRect)) {
			// Go onto the next level
			this.gameScreen.dispose();
			AssetManager.getInstance().getSound("correct-answer").play();
			if (this.gameInstance.levelManager.incrementLevel() == false) {
				// The game is finished, take them to the level screen
				this.gameInstance.setScreen(new LevelScreen(gameInstance));
			} else this.gameInstance.setScreen(new GameScreen(gameInstance));
		}
	}*/

}