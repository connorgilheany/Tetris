package com.github.ConnorGilheany.Tetris.controller;

import com.github.ConnorGilheany.Tetris.Game;
import org.newdawn.slick.GameContainer;

public interface Controller {
	
	public void doInput(Game game, GameContainer gc);

	void init();

	public void endGame(Game game);
}
