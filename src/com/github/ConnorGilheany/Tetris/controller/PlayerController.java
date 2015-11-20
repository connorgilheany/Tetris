package com.github.ConnorGilheany.Tetris.controller;

import com.github.ConnorGilheany.Tetris.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class PlayerController implements Controller {

    /**
     * Checks for player input and responds appropriately
     *
     * @param game The game being played
     * @param gc   the game container
     */
    @Override
    public void doInput(Game game,  GameContainer gc) {
		if(gc.getInput().isKeyPressed(Input.KEY_SPACE)){
			game.getCurrentPiece().slideDown(game.getMap());
            game.tick();
        }
        if(gc.getInput().isKeyPressed(Input.KEY_RIGHT))
			game.getCurrentPiece().move(game.getMap(), 1, 0);
		if(gc.getInput().isKeyPressed(Input.KEY_LEFT))
			game.getCurrentPiece().move(game.getMap(), -1, 0);
		if(gc.getInput().isKeyPressed(Input.KEY_DOWN))
			game.getCurrentPiece().move(game.getMap(), 0, 1);
		if(gc.getInput().isKeyPressed(Input.KEY_UP))
			game.getCurrentPiece().rotate(game.getMap());
		if(gc.getInput().isKeyPressed(Input.KEY_C))
			game.end();
		if(gc.getInput().isKeyPressed(Input.KEY_P)){
			game.togglePause();
        }
    }

    /**
     * Initialization code. Empty.
     */
    @Override
    public void init() {
    }

    /**
     * Run upon ending the game. Empty.
     *
     * @param game the game object
     */
    @Override
    public void endGame(Game game) {
	}

}
