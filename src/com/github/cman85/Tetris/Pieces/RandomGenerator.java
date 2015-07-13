package com.github.cman85.Tetris.Pieces;

import java.util.Random;

//Copy of BSPs Random Generator
public class RandomGenerator {
	
	private static ShapeType[] bag;
	private static int index = 0;
	
	static{
		bag = ShapeType.values();
		shuffleBag();
	}
	
	public static ShapeType nextPiece(){
		if(index != 0 && index % bag.length == 0){
			shuffleBag();
			index = 0;
		}
		return bag[index++];
	}
	
	public static void newGame(){
		index = 0;
		shuffleBag();
	}
	
	private static void shuffleBag(){
	    int index;
	    ShapeType temp;
	    Random random = new Random();
	    for (int i = bag.length - 1; i > 0; i--)
	    {
	        index = random.nextInt(i + 1);
	        temp = bag[index];
	        bag[index] = bag[i];
	        bag[i] = temp;
	    }
	}

}
