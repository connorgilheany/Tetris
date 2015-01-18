package com.github.cman85.Tetris.Pieces;

public enum ShapeType{		


	I(new boolean[][]{
			{true},
			{true},
			{true},
			{true}
			}),
	
	J(new boolean[][]{
			{false, true},
			{false, true},
			{true, true}
	}),
	L(new boolean[][]{
			{true, false},
			{true, false},
			{true, true}
	}),
	Z(new boolean[][]{
			{true, true, false},
			{false, true, true},
	}),
	S(new boolean[][]{
			{false, true, true},
			{true, true, false},
	}),
	O(new boolean[][]{
			{true, true},
			{true, true},
	}),
	T(new boolean[][]{
			{false, true, false},
			{true, true, true},
	}); 
	
	private boolean[][] shape;

	private ShapeType(boolean[][] shape){
		this.shape = shape;
	}
	
	public boolean[][] getShape(){
		return shape;
	}
	@Override
	public String toString(){
		if(this == I) return "I";
		if(this == L) return "L";
		if(this == J) return "J";
		if(this == O) return "O";
		if(this == S) return "S";
		if(this == Z) return "Z";
		if(this == T) return "T";
		return "null";
	}
}