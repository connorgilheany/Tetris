package com.github.cman85.Tetris.Pieces;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.github.cman85.Tetris.Game;
import com.github.cman85.Tetris.Map.Map;

public class GamePiece {

	public static Random rand = new Random();
	private Shape shape;
	private Color color;
	private int x = 3;
	private int y = 0;

	public GamePiece(Game game) {
		shape = new Shape();
		color = new Color(rand.nextInt(255), rand.nextInt(255),
				rand.nextInt(255));
		
		if(!canGo(game.getMap(), x, y))
			game.end();
	

	}

	public GamePiece(Shape shape, Color color) {
		this.shape = shape;
		this.color = color;
	}

	@Override
	public String toString() {
		return String.format("GamePiece={%s, %s}", shape.toString(),
				color.toString());
	}

	public boolean move(Map map, int deltaX, int deltaY) {
		boolean canGo = canGo(map, x + deltaX, y + deltaY);
		if (canGo) {
			y += deltaY;
			x += deltaX;
		}
		return canGo;
	}

	public boolean canGo(Map map, int x, int y) {
		return map.canPut(this, x, y);
	}

	public void draw(Map map, Graphics g) {
		boolean[][] shapeArray = shape.getCurrentShape();
		for (int y = 0; y < shapeArray.length; y++) {
			for (int x = 0; x < shapeArray[y].length; x++) {
				if (shapeArray[y][x]) {
					map.fillOutline(this.x + x, this.y + y, Color.lightGray, g);
					map.fillSquare(this.x + x, this.y + y, color, g);
				}
			}
		}
	}

	public void cement(Map map) {
		boolean[][] shapeArray = shape.getCurrentShape();
		for (int y = 0; y < shapeArray.length; y++) {
			for (int x = 0; x < shapeArray[y].length; x++) {
				if (shapeArray[y][x]){
					map.placeBlock(this.x + x, this.y + y, color);
				}
			}
		}
	}

	public Shape getShape() {
		return this.shape;
	}

	public void rotate(Map map) {
		GamePiece rotated = this.copy();
		rotated.getShape().rotateCW();
		if (map.canPut(rotated, x, y))
			getShape().rotateCW();
	}

	public GamePiece copy() {
		Shape shapeCopy = shape.copy();
		return new GamePiece(shapeCopy, color);
	}


	public void drawTarget(Map map, Graphics g) {
		int deltaY = 0;
		while (canGo(map, x, y + deltaY)) {
			deltaY++;
		}
		//g.drawString(String.format("Delta Y: %d", deltaY), 50, 200);
		deltaY--;

		boolean[][] shapeArray = shape.getCurrentShape();
		for (int y = 0; y < shapeArray.length; y++) {
			for (int x = 0; x < shapeArray[y].length; x++) {
				if (shapeArray[y][x]) {
					map.fillOutline(this.x + x, this.y + y + deltaY, Color.red, g);
				}
			}
		}

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Color getColor(){
		return color;
	}

	public void slideDown(Map map) {

		while (move(map, 0, 1));
	}

	public void setX(int x) {
		this.x = x;		
	}

	public void setY(int y) {
		this.y = y;
	}

}
