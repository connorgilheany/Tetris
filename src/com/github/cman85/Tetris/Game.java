package com.github.cman85.Tetris;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.github.cman85.Tetris.Map.Map;
import com.github.cman85.Tetris.Pieces.GamePiece;
import com.github.cman85.Tetris.controller.AIController;
import com.github.cman85.Tetris.controller.Controller;
import com.github.cman85.Tetris.controller.PlayerController;

public class Game extends BasicGame {

	private Map map;
	public final int TICK_TIME = 500;
	public int AI_SPEED = 1000;
	private Controller controller;
	private boolean AI_CONTROLLER = true;

	private GamePiece currentPiece;
	private GamePiece nextPiece;

	private boolean paused = false;
	private boolean fast = false;

	private int points = 0;

	public Game(String title) {
		super(title);

		try {
			AppGameContainer agc = new AppGameContainer(this, 1000, 750, false);
			agc.setTargetFrameRate(60);
			agc.setUpdateOnlyWhenVisible(false);
			agc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {

		currentPiece.drawTarget(map, g);
		map.render(g);
		map.renderNextPiece(g, nextPiece);
		currentPiece.draw(map, g);
		if (AI_CONTROLLER)
			((AIController) controller).drawStats(g);

		g.setColor(Color.white);
		g.drawString("Points: " + points, 50, 100);
		g.drawString(
				String.format("Piece Position: x: %d, y: %d",
						currentPiece.getX(), currentPiece.getY()), 50, 150);
		// g.drawString("Current piece: " + currentPiece.toString(), 50, 700);
		if (paused)
			g.drawString("PAUSED", 50, 250);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		if (AI_CONTROLLER)
			controller = new AIController();
		else
			controller = new PlayerController();
		controller.init();
		map = new Map();
		if (currentPiece == null)
			nextGamePiece();
	}

	private int totalDelta = 0;

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		map.setScreenSize(gc.getWidth(), gc.getHeight());
		totalDelta += delta;

		if (gc.getInput().isKeyPressed(Input.KEY_EQUALS))
			AI_SPEED += 10;
		if (gc.getInput().isKeyPressed(Input.KEY_MINUS))
			AI_SPEED -= 10;
		if (!AI_CONTROLLER)
			controller.doInput(this, gc);

		if ((totalDelta > TICK_TIME && (!AI_CONTROLLER || !fast) ) || (AI_CONTROLLER && fast)) {
			tick(true);
			totalDelta = 0;
		}

		/*
		 * if (AI_CONTROLLER) controller.doInput(this, gc);
		 */

	}

	public void tick(boolean takeInput) {

		if (!paused) {
			if (AI_CONTROLLER) {
				if (fast) {
					for (int i = 0; i < AI_SPEED; i++) {

						if (takeInput) {
							controller.doInput(this, null);
						}

						if (!currentPiece.move(map, 0, 1)) {
							freezePiece();
							checkCompletion();
						}
					}

				} else {

					if (takeInput) {
						controller.doInput(this, null);
					}

					if (!currentPiece.move(map, 0, 1)) {
						freezePiece();
						checkCompletion();
					}
				}
			} else {
				if (!currentPiece.move(map, 0, 1)) {
					freezePiece();
					checkCompletion();
				}

			}
		}

	}

	private void checkCompletion() {
		int completed = map.checkCompletion();
		rowsCompleted(completed);
	}

	private void rowsCompleted(int completed) {
		switch (completed) {
		case 1:
			addPoints(40);
			break;
		case 2:
			addPoints(100);
			break;
		case 3:
			addPoints(300);
			break;
		case 4:
			addPoints(1200);
			break;
		}

	}

	public void freezePiece() {
		currentPiece.cement(map);
		nextGamePiece();

	}

	private void nextGamePiece() {
		if (nextPiece == null)
			nextPiece = new GamePiece(this);
		currentPiece = nextPiece;
		nextPiece = new GamePiece(this);
	}

	public GamePiece getCurrentPiece() {
		return currentPiece;
	}

	public Map getMap() {
		return map;
	}

	public void end() {
		System.out.println("Ending game");
		controller.endGame(this);
		map.clear();
		points = 0;

	}

	public void togglePause() {
		paused = !paused;
	}

	public void addPoints(int amount) {
		points += amount;
	}

	public int getPoints() {
		return points;
	}

}
