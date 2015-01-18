package com.github.cman85.Tetris.Pieces;

import java.util.Arrays;
import java.util.Random;

public class Shape {
	
	private ShapeType type;

	private boolean[][] currentShape;
	private int rotations = 0;
	
	public Shape(){
		setShapeType(getRandomShapeType());
	}
	
	public void rotateCW() {
		rotations += 1;
	    final int M = currentShape.length;
	    final int N = currentShape[0].length;
	    boolean[][] ret = new boolean[N][M];
	    for (int r = 0; r < M; r++) {
	        for (int c = 0; c < N; c++) {
	            ret[c][M-1-r] = currentShape[r][c];
	        }
	    }
	    currentShape = ret;
	}
	

	private ShapeType getRandomShapeType(){
		
		//return ShapeType.I;
		return ShapeType.values()[new Random().nextInt(ShapeType.values().length)];
	}
	
	public boolean[][] getCurrentShape(){
		return currentShape;
	}
	
	@Override
	public String toString(){
		return String.format("Shape={%s, %d}", type.toString(), rotations);
	}
	
	public ShapeType getShapeType() {
		return type;
	}

	public void setShapeType(ShapeType type) {
		this.type = type;
		setCurrentShape(type.getShape());

	}

	public void setCurrentShape(boolean[][] shape) {
		this.currentShape = shape;
	}
	
	public int getRotations(){
		return rotations;
	}
	
	public Shape copy(){
		Shape shapeCopy = new Shape();
		shapeCopy.setShapeType(getShapeType());
		shapeCopy.setCurrentShape(deepCopy(getCurrentShape()));
		return shapeCopy;
	}
	
	private boolean[][] deepCopy(boolean[][] original) {
		if (original == null) {
			return null;
		}

		final boolean[][] result = new boolean[original.length][];
		for (int i = 0; i < original.length; i++) {
			result[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return result;
	}

}
