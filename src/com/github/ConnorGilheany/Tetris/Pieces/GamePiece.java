package com.github.ConnorGilheany.Tetris.Pieces;

import com.github.ConnorGilheany.Tetris.Game;
import com.github.ConnorGilheany.Tetris.Map.Map;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.Random;

public class GamePiece {

	public static Random rand = new Random();
	private Shape shape;
	private Color color;
	private int x = 3;
	private int y = 0;

    /**
     * Creates a new piece with random shape and color
     *
     * @param game the game the piece is a part of
     */
    public GamePiece(Game game) {
		shape = new Shape();
		color = new Color(rand.nextInt(255), rand.nextInt(255),
				rand.nextInt(255));
		
		if(!canGo(game.getMap(), x, y))
			game.end();
    }

    /**
     * creates a new game piece. Only used internally in the copy method
     *
     * @param shape the shape of the piece to create
     * @param color the color of the piece to create
     */
    private GamePiece(Shape shape, Color color) {
        this.shape = shape;
		this.color = color;
	}

	@Override
	public String toString() {
		return String.format("GamePiece={%s, %s}", shape.toString(),
				color.toString());
    }

    /**
     * translates the piece in the given x and y amounts
     * @param map the map to move the piece on
     * @param deltaX the amount to move the piece in the X direction (negative is left)
     * @param deltaY the amount to move the piece in the Y direction (negative is up)
     * @return Whether the piece can be placed in the given location
     */
    public boolean move(Map map, int deltaX, int deltaY) {
		boolean canGo = canGo(map, x + deltaX, y + deltaY);
		if (canGo) {
			y += deltaY;
			x += deltaX;
		}
		return canGo;
    }

    /**
     * Checks if the game piece can go in the location passed
     * @param map the map to place it on
     * @param x the x location to place it on
     * @param y the y location to place it on
     * @return whether the piece can go there
     */
    public boolean canGo(Map map, int x, int y) {
		return map.canPut(this, x, y);
    }

    /**
     * Draws the game piece on the given map
     * @param map the map to draw the piece on
     * @param g the graphics object to draw with
     */
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

    /**
     * Freezes the piece in place, inserting it's blocks into the map
     * @param map the map to freeze the piece on
     */
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

    /**
     * @return the shape of the piece
     */
    public Shape getShape() {
		return this.shape;
    }

    /**
     * rotates the piece once clockwise
     * @param map the map to rotate the piece on
     */
    public void rotate(Map map) {
		GamePiece rotated = this.copy();
		rotated.getShape().rotateCW();
		if (map.canPut(rotated, x, y))
			getShape().rotateCW();
    }

    /**
     * @return a copy of the game piece
     */
    public GamePiece copy() {
		Shape shapeCopy = shape.copy();
		return new GamePiece(shapeCopy, color);
    }


    /**
     * Draws the target: highlights where the piece will land
     * @param map the map to draw the target on
     * @param g the graphics object to draw with
     */
    public void drawTarget(Map map, Graphics g) {
		int deltaY = 0;
		while (canGo(map, x, y + deltaY)) {
			deltaY++;
		}
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

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
	}

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Moves the piece all the way down vertically
     * @param map the map to move the piece down on
     */
    public void slideDown(Map map) {

        while (move(map, 0, 1)) ;
    }

}
