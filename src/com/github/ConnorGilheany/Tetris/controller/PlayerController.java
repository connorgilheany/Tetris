package com.github.ConnorGilheany.Tetris.controller;

import com.github.ConnorGilheany.Tetris.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

public class PlayerController implements Controller {

	@Override
	public void doInput(Game game,  GameContainer gc) {
		if(gc.getInput().isKeyPressed(Input.KEY_SPACE)){
			game.getCurrentPiece().slideDown(game.getMap());
			game.tick(true);
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

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endGame(Game game) {
		// TODO Auto-generated method stub
		
	}

}
