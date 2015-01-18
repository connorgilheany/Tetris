package com.github.cman85.Tetris.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.github.cman85.Tetris.Game;
import com.github.cman85.Tetris.AI.Genes;
import com.github.cman85.Tetris.AI.Move;
import com.github.cman85.Tetris.Pieces.GamePiece;

public class AIController implements Controller {

	private Genes currentGenes;
	private Genes bestGenes;
	private int runs = 1;
	private int currMoves = 0;
	private int totalMoves = 0;
	private int totalLinesCleared = 0;
	private long totalPoints = 0;
	private List<Integer> recentPoints = new LinkedList<Integer>();

	@Override
	public void init() {
		currentGenes = new Genes();
		bestGenes = new Genes();
		try {
			load();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0; i < 20; i++)
			recentPoints.add(0);
	}

	@Override
	public void doInput(Game game, GameContainer gc) {
		Move bestMove = null;
		//if(gc.getInput().isKeyPressed(Input.KEY_ESCAPE))
			//end();
		for (int rotations = 0; rotations < 4; rotations++) {

			int maxLength = (game.getMap().getBlocks()[0].length - game
					.getCurrentPiece().getShape().getCurrentShape()[0].length);
			for (int x = 0; x < maxLength + 1; x++) {
				if (!game.getCurrentPiece().canGo(game.getMap(), x, 0))
					continue;
				game.getCurrentPiece().setX(x);
				game.getCurrentPiece().slideDown(game.getMap());
				game.getCurrentPiece().cement(game.getMap());

				Move move = new Move(x, game.getCurrentPiece().getY(), game
						.getCurrentPiece().getShape().getRotations());
				move.calculatePoints(currentGenes, game);

				if (bestMove == null || bestMove.getPoints() < move.getPoints())
					bestMove = move;
				resetPiece(game);

			}
			game.getCurrentPiece().rotate(game.getMap());
		}
		// System.out.println(bestMove.toString());
		if (bestMove == null)
			return;
		doMove(game, bestMove);
	}


	private void doMove(Game game, Move bestMove) {
		currMoves++;
		totalMoves++;
		totalLinesCleared += bestMove.l;
		currentGenes.addLinesCleared(bestMove.l);
		GamePiece piece = game.getCurrentPiece();
		// Does the rotations
		for (int rotations = 0; rotations < bestMove.getRotations() % 4; rotations++) {
			piece.rotate(game.getMap());
		}

		game.getCurrentPiece().move(game.getMap(),
				bestMove.getX() - game.getCurrentPiece().getX(), 0);
		game.getCurrentPiece().slideDown(game.getMap());// TODO perhaps merge
														// this w/ above line

	}

	private void resetPiece(Game game) {
		boolean[][] shape = game.getCurrentPiece().getShape().getCurrentShape();
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				if (shape[y][x]) {
					game.getMap().removeBlock(
							game.getCurrentPiece().getX() + x,
							game.getCurrentPiece().getY() + y);
				}
			}
		}
		game.getCurrentPiece().setX(3);
		game.getCurrentPiece().setY(0);
	}

	@Override
	public void endGame(Game game) {
		currentGenes.setPointsScored(game.getPoints());
		if (bestGenes == null
				|| currentGenes.getPointsScored() > bestGenes.getPointsScored()) {
			System.out.println("New Best!!!");
			bestGenes = currentGenes;
			System.out.println(bestGenes.toString());
		}
		if (runs < 50000)
			currentGenes = new Genes();
		else {
			System.out.println("Mutating genes");
			currentGenes = bestGenes.copy();
			currentGenes.mutate();
		}
		runs++;
		currMoves = 0;
		totalPoints += game.getPoints();
		if(game.getPoints() > 30000){
			recentPoints.add(0, game.getPoints());
			recentPoints.remove(20);
		}
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void drawStats(Graphics g) {
		g.setColor(Color.red);
		g.drawString("AI Stats", 50, 200);
		g.drawString("Run: " + runs, 50, 225);
		g.drawString("Current Moves: " + currMoves, 50, 250);
		g.drawString("Total Moves: " + totalMoves, 50, 275);
		g.drawString("Total Lines Cleared: "+totalLinesCleared, 50, 300);
		g.drawString("Total Points: "+totalPoints, 50, 325);
		g.drawString("Best " + bestGenes.toString(), 50, 350);
		g.drawString("Current "+currentGenes.toString(), 50, 525);
		
		
		g.drawString("Recent Points:\n"+listToString(recentPoints), 750, 225);
		// g.drawString(str, x, y);

	}

	public void save() throws IOException {
		// Set up the FileWriter with our file name.
		FileWriter saveFile = new FileWriter("/Users/removed/Desktop/TetrisData.txt");

		// Write the data to the file.
		saveFile.write("\n");
		saveFile.write(runs + "\n");
		saveFile.write(totalMoves + "\n");
		saveFile.write(totalLinesCleared + "\n");
		saveFile.write(totalPoints + "\n");
		saveFile.write(bestGenes.getWallBonus() + "\n");
		saveFile.write(bestGenes.getHeightPenalty() + "\n");
		saveFile.write(bestGenes.getLineBonus() + "\n");
		saveFile.write(bestGenes.getOverhangPenalty() + "\n");
		saveFile.write(bestGenes.getBlockedPenalty() + "\n");
		saveFile.write(bestGenes.getPointsScored() + "\n");
		saveFile.write(bestGenes.getLinesCleared() + "\n");
		saveFile.write("\n");

		// All done, close the FileWriter.
		saveFile.close();
	}

	public void load() throws NumberFormatException, IOException {
		BufferedReader saveFile = new BufferedReader(new FileReader("/Users/removed/Desktop/TetrisData.txt"));

		// Throw away the blank line at the top.
		saveFile.readLine();
		// Get the integer value from the String.
		runs = Integer.parseInt(saveFile.readLine());
		totalMoves = Integer.parseInt(saveFile.readLine());
		totalLinesCleared = Integer.parseInt(saveFile.readLine());
		totalPoints = Integer.parseInt(saveFile.readLine());

		bestGenes.setWallBonus(Float.parseFloat(saveFile.readLine()));
		bestGenes.setHeightPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setLineBonus(Float.parseFloat(saveFile.readLine()));
		bestGenes.setOverhangPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setBlockedPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setPointsScored(Long.parseLong(saveFile.readLine()));
		bestGenes.addLinesCleared(Integer.parseInt(saveFile.readLine()));
		// Not needed, but read blank line at the bottom.
		saveFile.readLine();
		saveFile.close();

	}
	

	@SuppressWarnings("unused")
	private void end() {
		try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public String listToString(List<Integer> list){
		StringBuilder builder = new StringBuilder();
		for(Integer i: list){
			builder.append(i).append("\n");
		}
		return builder.toString();
	}

}
