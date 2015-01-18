package com.github.cman85.Tetris.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.github.cman85.Tetris.Pieces.GamePiece;

public class Map {
	
	private Block[][] blocks = new Block[22][10];
	private int WIDTH = 30;
	private int HEIGHT = 30;
	private int X_OFFSET;
	private int Y_OFFSET;

	public Map(){
		
	}
	
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


	public void setScreenSize(int screenWidth, int screenHeight) {
		X_OFFSET = screenWidth/2 - (blocks[0].length* WIDTH / 2) ;
		Y_OFFSET = screenHeight/2 - ((blocks.length)* HEIGHT / 2);//TODO subtract two from blocks.length to make up for invisible spaces 

	}
	
	public void fillSquare(int x, int y, Color color, Graphics g){
		if(blocks[y][x] != null) fillOutline(x, y, Color.lightGray, g);
		g.setColor(color);
		g.fillRect(X_OFFSET + x * WIDTH, Y_OFFSET + y * HEIGHT, WIDTH - 4, HEIGHT - 4);
	}
	
	public void fillOutline(int x, int y, Color c, Graphics g){
		g.setColor(c);
		g.fillRect(X_OFFSET + x * WIDTH - 2, Y_OFFSET + y * HEIGHT - 2, WIDTH , HEIGHT );
	}


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


	public void placeBlock(int x, int y, Color color) {
		blocks[y][x] = new Block(color);
	}


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

	private void clearRow(int row) {
		for(int x = 0; x < blocks[row].length; x++){
			removeBlock(x, row);
		}
		for(int y = row; y > 0; y--){
			for(int x = 0; x < blocks[y].length; x++){
				if(blocks[y-1][x] != null) placeBlock(x, y, blocks[y-1][x].getColor());
				else removeBlock(x, y);
				//blocks[y] = blocks[y-1];
			}
		}
		
	}


	public void clear() {
		for(int y = 0; y < blocks.length; y++){ 
			for(int x = 0; x < blocks[y].length; x++){
				removeBlock(x, y);
			}
		}		
	}


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

	public void removeBlock(int x, int y) {
		blocks[y][x] = null;		
	}
	
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
