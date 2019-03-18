package com.faizaand.hamlet.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.faizaand.hamlet.GameGUI;

/**
 * @author Faizaan Datoo
 */
public class EntityPlayer extends EntityBase {

	public PlayerInput input;

	public EntityPlayer(GameGUI gui) {
		super("textures/player.txt");
		this.input = new PlayerInput(gui, this);
	}

	@Override
	public void update(float deltaTime, TiledMap currentMap) {
		super.update(deltaTime, currentMap);

		// The player has fallen off the map, kill it.
		if (position.y < -85) alive = false;
	}

	@Override
	protected void handleInput() {
		// Check for keyboard input
		if ((Gdx.input.isKeyPressed(Keys.SPACE)) && onGround) {
			jump();
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			left();
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
			right();
		}

		// Check for button input
		this.input.checkInput();
	}

	// These methods are local because they can to be accessed by PlayerInput.

	void left() {
		velocity.x = -maxVelocity;
		if (onGround) state = EntityState.Walking;
		facingRight = false;
	}

	void right() {
		velocity.x = maxVelocity;
		if (onGround) state = EntityState.Walking;
		facingRight = true;
	}

	void jump() {
		if (!onGround) return;
		velocity.y += jumpVelocity;
		state = EntityState.Jumping;
		onGround = false;
	}

}
