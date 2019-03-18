package com.faizaand.hamlet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.faizaand.hamlet.level.LevelManager;

public class HamletGame extends Game {
	public LevelManager levelManager;
	public MusicManager musicManager;

	@Override
	public void create() {
		loadAssets();

		// Load all levels into the game
		levelManager = new LevelManager();
		levelManager.loadLevels();

		// Load all background music
		musicManager = new MusicManager();

		this.setScreen(new GameScreen(this));
	}

	private void loadAssets() {
		// Load game sound effects
		AssetManager.getInstance().registerSound("wrong-answer",
				"sounds/wrong-answer.wav");
		AssetManager.getInstance().registerSound("correct-answer",
				"sounds/correct-answer.wav");
		AssetManager.getInstance().registerSound("player-death",
				"sounds/player-death.wav");
		AssetManager.getInstance().registerSound("button-click",
				"sounds/button-click.wav");

		// Load game music
		AssetManager.getInstance().registerMusic("music0", "music/music0.mp3");
		AssetManager.getInstance().registerMusic("music1", "music/music1.mp3");
		AssetManager.getInstance().registerMusic("music2", "music/music2.mp3");

		/*
		 * Textures
		 */

		// Textures for level packs
		AssetManager.getInstance().registerTexture("grassyJourney-logo",
				"textures/Grassy-Journey.png");
		AssetManager.getInstance().registerTexture("snowyPlains-logo",
				"textures/Snowy-Plains.png");

		// Textures for screens
		AssetManager.getInstance().registerTexture("options-logo",
				"textures/options-logo.png");
		AssetManager.getInstance().registerTexture("helpscreen",
				"textures/helpScreen.png");

		// Background for all menus
		AssetManager.getInstance().registerTexture("menuBackground",
				"textures/bg_castle.png");

		// Main menu buttons
		AssetManager.getInstance().registerTexture("playButton", "textures/play.png");
		AssetManager.getInstance().registerTexture("optionsButton",
				"textures/options.png");
		AssetManager.getInstance().registerTexture("helpButton", "textures/help.png");
		AssetManager.getInstance().registerTexture("problematicLogo",
				"textures/Problematic.png");

		// Background for math screen
		AssetManager.getInstance().registerTexture("mathscreenBackground",
				"textures/chalkboard.jpg");
	}

	@Override
	public void render() {
		// Remove pixels from the previous frame by setting all pixels to
		// sky blue.
		Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// The superclass' render method renders the current screen, call it
		super.render();
	}

	@Override
	public void dispose() {
		// Dispose of background music
		musicManager.dispose();
		// Dispose of all levels on exit
		levelManager.dispose();
		// Dispose of all assets on exit
		AssetManager.getInstance().disposeAll();
	}

}