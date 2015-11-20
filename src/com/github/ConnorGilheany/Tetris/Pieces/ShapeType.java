package com.github.ConnorGilheany.Tetris.Pieces;

public enum ShapeType{		


	I(new boolean[][]{
			{true},
			{true},
			{true},
			{true}
    }, 2),

    J(new boolean[][]{
            {false, true},
			{false, true},
			{true, true}
    }, 4),
    L(new boolean[][]{
            {true, false},
			{true, false},
			{true, true}
    }, 4),
    Z(new boolean[][]{
            {true, true, false},
			{false, true, true},
    }, 2),
    S(new boolean[][]{
            {false, true, true},
			{true, true, false},
    }, 2),
    O(new boolean[][]{
            {true, true},
			{true, true},
    }, 1),
    /*D(new boolean[][]{
        {true}
	}),*/
	T(new boolean[][]{
			{false, true, false},
			{true, true, true},
    }, 4);


    private boolean[][] shape;
    private int maxRotations;

    ShapeType(boolean[][] shape, int maxRotations) {
        this.shape = shape;
        this.maxRotations = maxRotations;
    }

    public boolean[][] getShape(){
		return shape;
    }

    public int getMaxRotations() {
        return maxRotations;
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
		//if(this == D) return "D";
		return "null";
	}
}