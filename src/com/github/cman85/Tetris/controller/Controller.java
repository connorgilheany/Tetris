package com.github.cman85.Tetris.controller;

import org.newdawn.slick.GameContainer;

import com.github.cman85.Tetris.Game;

public interface Controller {
	
	public void doInput(Game game, GameContainer gc);

	void init();

	public void endGame(Game game);
}
