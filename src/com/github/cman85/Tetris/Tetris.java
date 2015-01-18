package com.github.cman85.Tetris;

import java.util.Arrays;

public class Tetris{
	
	
	public static void main(String[] args){
		new Game("Tetris");
		
	}
	
	static void printMatrix(boolean[][] mat) {
	    System.out.println("Matrix = ");
	    for (boolean[] row : mat) {
	        System.out.println(Arrays.toString(row));
	    }
	}


}
