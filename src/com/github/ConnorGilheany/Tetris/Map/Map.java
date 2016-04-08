package com.github.ConnorGilheany.Tetris.Map;

import com.github.ConnorGilheany.Tetris.Pieces.GamePiece;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Map {

    private Block[][] blocks = new Block[22][10];// 22 10
    private int WIDTH = 30;
	private int HEIGHT = 30;
	private int X_OFFSET;
	private int Y_OFFSET;

	public Map(){
		
	}

    /**
     * Renders the map and it's blocks
     *
     * @param g the graphics object to render with
     */
    public void render(Graphics g){
		for(int y = 0; y < blocks.length; y++){ //TODO start at 2
			for(int x = 0; x < blocks[y].length; x++){
				Color color = blocks[y][x] == null? Color.gray : blocks[y][x].getColor();
				fillSquare(x, y, color, g);
			}
		}
	}
	
	public Block[][] getBlocks(){
		return blocks;
    }


    /**
     * Sets the X and Y offsets for displaying the map, so that it is centered
     * @param screenWidth the width of the screen
     * @param screenHeight the height of the screen
     */
    public void setScreenSize(int screenWidth, int screenHeight) {
		X_OFFSET = screenWidth/2 - (blocks[0].length* WIDTH / 2) ;
		Y_OFFSET = screenHeight/2 - ((blocks.length)* HEIGHT / 2);//TODO subtract two from blocks.length to make up for invisible spaces 

    }

    /**
     * Fills in the given block
     *
     * @param x     the x coordinate of the block to fill
     * @param y     the y coordinate of the block to fill
     * @param color the color to set the block to
     * @param g     the graphics object to draw with
     */
    public void fillSquare(int x, int y, Color color, Graphics g){
        if(blocks[y][x] != null) fillOutline(x, y, Color.lightGray, g);
		g.setColor(color);
		g.fillRect(X_OFFSET + x * WIDTH, Y_OFFSET + y * HEIGHT, WIDTH - 4, HEIGHT - 4);
    }

    /**
     * Fills in the outline of the given block
     * @param x the x coordinate of the block to outline
     * @param y the y coordinate of the block to outline
     * @param c the color to outline with
     * @param g the graphics object
     */
    public void fillOutline(int x, int y, Color c, Graphics g){
		g.setColor(c);
		g.fillRect(X_OFFSET + x * WIDTH - 2, Y_OFFSET + y * HEIGHT - 2, WIDTH, HEIGHT);
    }


    /**
     * Checks whether a piece can be placed in a location
     * @param gamePiece the piece to test with
     * @param x the x coordinate to place the piece at
     * @param y the y coordinate to place the piece at
     * @return whether the piece can be placed at the given location
     */
    public boolean canPut(GamePiece gamePiece, int x, int y) {
		boolean[][] shape = gamePiece.getShape().getCurrentShape();
		
		if(x < 0 || y < 0) return false;
		
		if(y + shape.length > blocks.length || x + shape[0].length > blocks[0].length) return false;
		
		for(int r = 0; r < shape.length; r++){
			for(int c = 0; c < shape[r].length; c++){
				if(shape[r][c] && blocks[y+r][x+c] != null){
					return false;
				}
			}
        }

        return true;
    }


    /**
     * Sets the contents of a block
     * @param x the x coordinate of the block to set
     * @param y the y coordinate of the block to set
     * @param color the color to set the block to
     */
    public void placeBlock(int x, int y, Color color) {
		blocks[y][x] = new Block(color);
    }

    /**
     * Checks for completed rows
     * @return the amount of rows completed
     */
    public int checkCompletion() {
		int completed = 0;
		for(int y = blocks.length - 1; y >= 0; y--){
			boolean full = true;
			for(int x = 0; x < blocks[y].length; x++){
				if(blocks[y][x] == null) full = false;
			}
			if(full){
				clearRow(y);
				y++;
				completed++;
			}

        }
        return completed;
    }

    /**
     * Clears a row after it has been completed
     * @param row the row to clear
     */
    private void clearRow(int row) {
        //clears finished row
        for(int x = 0; x < blocks[row].length; x++){
			removeBlock(x, row);
		}
        //Moves above rows down
        for(int y = row; y > 0; y--){
			for(int x = 0; x < blocks[y].length; x++){
				if(blocks[y-1][x] != null) placeBlock(x, y, blocks[y-1][x].getColor());
				else removeBlock(x, y);
				//blocks[y] = blocks[y-1];
            }
        }

        //Clears the top row
        for (int x = 0; x < blocks[0].length; x++) {
            removeBlock(x, 0);
        }
    }


    /**
     * Clears the entire map
     */
    public void clear() {
		for(int y = 0; y < blocks.length; y++){ 
			for(int x = 0; x < blocks[y].length; x++){
				removeBlock(x, y);
            }
        }
    }

    /**
     * Renders the next piece to the top right of the map
     * @param g the graphics object to draw with
     * @param next the piece to draw
     */
    public void renderNextPiece(Graphics g, GamePiece next) {
		boolean[][] shape = next.getShape().getCurrentShape();
		for(int y = 0; y < shape.length; y++){
			for(int x = 0; x < shape[y].length; x++){
				if(shape[y][x]){
					g.setColor(next.getColor());
					g.fillRect(2 * X_OFFSET + x * WIDTH, 2 * Y_OFFSET + y * HEIGHT, WIDTH - 4, HEIGHT - 4);
                }
            }
        }
    }

    /**
     * Clears the block at the given location
     * @param x the x coordinate of the block
     * @param y the y coordinate of the block
     */
    public void removeBlock(int x, int y) {
        blocks[y][x] = null;
    }

    /**
     * @return a copy of the map
     */
    public Map copy(){
		Map newMap = new Map();
		for(int y = 0; y < blocks.length; y++){
			for(int x = 0; x < blocks[y].length; x++){
				newMap.blocks[y][x] = blocks[y][x];
			}
		}
		return newMap;
	}

}
