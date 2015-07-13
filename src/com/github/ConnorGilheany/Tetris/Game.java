package com.github.ConnorGilheany.Tetris;

import java.io.IOException;

import com.github.ConnorGilheany.Tetris.Map.Map;
import com.github.ConnorGilheany.Tetris.Pieces.RandomGenerator;
import com.github.ConnorGilheany.Tetris.controller.AIController;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.github.ConnorGilheany.Tetris.Pieces.GamePiece;
import com.github.ConnorGilheany.Tetris.controller.Controller;
import com.github.ConnorGilheany.Tetris.controller.PlayerController;

public class Game extends BasicGame {

	private Map map;
	public int TICK_TIME = 500;
	public final int AI_SPEED = 3000;//1000
	private Controller controller;
	private boolean AI_CONTROLLER = true;

	private GamePiece currentPiece;
	private GamePiece nextPiece;

	private boolean paused = false;
	private boolean fast = false;
	public boolean mutate = true;


	private int points = 0;

	public Game(String title) {
		super(title);

		try {
			AppGameContainer agc = new AppGameContainer(this, 1000, 750, false);
			agc.setTargetFrameRate(60);
			agc.setUpdateOnlyWhenVisible(false);
			agc.start();
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		        public void run() {
		        	exit();	
		        }
		    }));
			
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
		g.drawString("Points: " + points, 50, 30);
		/*g.drawString(
				String.format("Piece Position: x: %d, y: %d",
						currentPiece.getX(), currentPiece.getY()), 50, 150);*/
		// g.drawString("Current piece: " + currentPiece.toString(), 50, 700);
		if (paused)
			g.drawString("PAUSED", 50, 250);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		setController();
		map = new Map();
		if (currentPiece == null)
			nextGamePiece();
	}

	private void setController() {
		if (AI_CONTROLLER)
			controller = new AIController();
		else
			controller = new PlayerController();
		controller.init();

	}

	private int totalDelta = 0;

	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		map.setScreenSize(gc.getWidth(), gc.getHeight());
		totalDelta += delta;

		if (gc.getInput().isKeyPressed(Input.KEY_EQUALS))
			TICK_TIME -= 10;
		if (gc.getInput().isKeyPressed(Input.KEY_MINUS))
			TICK_TIME += 10;
		if(gc.getInput().isKeyPressed(Input.KEY_PERIOD))
			fast = !fast;
		if(gc.getInput().isKeyPressed(Input.KEY_COMMA))
			swapInputs();
		if(gc.getInput().isKeyPressed(Input.KEY_C))
			clearData();
		if(gc.getInput().isKeyPressed(Input.KEY_M))
			mutate = !mutate;

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

	private void exit() {
		if(AI_CONTROLLER){
			try {
				((AIController)controller).save();
				((AIController)controller).saveBestEverGenes();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void clearData() {
		if(AI_CONTROLLER){
			end();
			((AIController)controller).clearData();
		}
	}

	private void swapInputs() {
		AI_CONTROLLER = !AI_CONTROLLER;
		setController();
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
					if (takeInput) 
						controller.doInput(this, null);

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
		//System.out.println("Ending game");
		RandomGenerator.newGame();
		controller.endGame(this);
		nextPiece = null;
		currentPiece = null;
		map.clear();
		points = 0;
		nextGamePiece();

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
