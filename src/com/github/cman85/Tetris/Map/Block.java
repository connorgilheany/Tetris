package com.github.cman85.Tetris.Map;

import org.newdawn.slick.Color;

public class Block {
	
	private Color color;
	
	public Block(Color color){
		setColor(color);
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}

}
