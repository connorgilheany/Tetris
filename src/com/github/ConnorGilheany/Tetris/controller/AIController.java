package com.github.ConnorGilheany.Tetris.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import com.github.ConnorGilheany.Tetris.Game;
import com.github.ConnorGilheany.Tetris.AI.Genes;
import com.github.ConnorGilheany.Tetris.AI.Move;
import com.github.ConnorGilheany.Tetris.Pieces.GamePiece;

public class AIController implements Controller {

	public static final String FILE_PATH = "/Users/connorgilheany/Desktop/TetrisData.txt";
	public static final String FILE_PATH_BEST = "/Users/connorgilheany/Desktop/TetrisBestGenes.txt";


	private Genes currentGenes;
	private Genes bestGenes;
	private Genes bestEverGenes = new Genes();
	private int runs = 1;
	private int runsSinceLoad = 1;
	private int currMoves = 0;
	private int totalMoves = 0;
	private int totalLinesCleared = 0;
	private long totalPoints = 0;
	private List<Integer> recentPoints = new LinkedList<Integer>();

	private List<Integer> previousScores = new LinkedList<Integer>();
	private int prevGamesCounted = 1000;
	private int average = 0;
	private int interval = 50; // Amount of times a gene will be used
	private int lastBestGeneRun = 0;
	private int runsSinceNewTree = 0;
	private int bestScoreEver = 0;

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
		currentGenes = bestGenes.copy();
		// GamePiece.rand = new Random(currentGenes.getRandSeed());

		for (int i = 0; i < 20; i++)
			recentPoints.add(0);
		for (int i = 0; i < prevGamesCounted; i++)
			previousScores.add(0);
	}

	@Override
	public void doInput(Game game, GameContainer gc) {
		Move bestMove = null;
		// if(gc.getInput().isKeyPressed(Input.KEY_ESCAPE))
		// end();
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
		currentGenes.setBlocksPlaced(currentGenes.getBlocksPlaced() + 1);
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
		runs++;
		runsSinceLoad++;
		runsSinceNewTree++;
		currentGenes.setPointsScored(currentGenes.getPointsScored()
				+ game.getPoints());
		average = calculateAverage();

		if (runs % interval == 0) {
			currentGenes.setPointsScored(currentGenes.getPointsScored()
					/ interval); // Averages out ppg
			if (bestGenes == null
					|| currentGenes.getPointsScored() > bestGenes
							.getPointsScored()) {
				bestGenes = currentGenes;
				lastBestGeneRun = runs;
				// GamePiece.rand = new Random(bestGenes.getRandSeed());
			}
			if (runs < 50000 || runsSinceNewTree < 50000)
				currentGenes = new Genes();
			else if (runs - lastBestGeneRun > 400 * interval && lastBestGeneRun > 50000) {// Our tree isn't making
													// progress
				currentGenes = new Genes();
				System.out.println("Best Genes, got stuck and dropped:\n"
						+ bestGenes.toString());
				runsSinceNewTree = 0;
				bestGenes = currentGenes;
			} else {
				// System.out.println("Mutating genes");
				currentGenes = bestGenes.copy();
				if (game.mutate)
					currentGenes.mutate();
			}
		}
		currMoves = 0;
		totalPoints += game.getPoints();
		if (game.getPoints() > 30000) {
			recentPoints.add(0, game.getPoints());
			recentPoints.remove(20);
		}
		previousScores.add(0, game.getPoints());
		previousScores.remove(prevGamesCounted);

		if(bestScoreEver < game.getPoints())
			bestScoreEver = game.getPoints();
		try {
			save();
			saveBestEverGenes();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void drawStats(Graphics g) {
		// g.drawString("PRESS ESCAPE TO EXIT", 400, 25);
		g.setColor(Color.red);
		int x = 50;
		int y = 50;
		g.drawString("AI Stats", x, y);
		g.drawString("Run: " + runs, x, y += 25);
		g.drawString("Current Moves: " + currMoves, x, y += 25);
		g.drawString("Total Moves: " + totalMoves, x, y += 25);
		g.drawString("Total Lines Cleared: " + totalLinesCleared, x, y += 25);
		g.drawString("Total Points: " + totalPoints, x, y += 25);
		g.drawString("Best " + bestGenes.toString(), x, y += 25);
		g.drawString("Current " + currentGenes.toString(), x, y += 175);
		g.drawString("Best EVER " + bestEverGenes, x, y += 175);

		drawRecentPoints(g);
		
		g.setColor(Color.red);
		g.drawString("Best Score Ever: ", 750, 565);
		setColorForScore(g, bestScoreEver);
		g.drawString(""+bestScoreEver, 750, 580);
		
		g.setColor(Color.red);
		g.drawString("Last 1000 Avg: " + average, 750, 650);
		g.drawString("LastBestGeneRun: " + lastBestGeneRun, 750, 675);
		g.drawString("RunsSinceNewTree: " + runsSinceNewTree, 750, 700);

		// g.drawString(str, x, y);

	}

	private void drawRecentPoints(Graphics g) {
		int y = 225;
		g.drawString("Recent Points:\n", 750, y += 15);

		for (int i : recentPoints) {
			setColorForScore(g, i);
			g.drawString(i + "", 750, y += 15);
		}
		// g.drawString("Recent Points:\n" + listToString(recentPoints), 750,
		// 225);
	}

	private void setColorForScore(Graphics g, int i) {
		if (i < 100000)
			g.setColor(Color.red);
		else if (i < 500000)
			g.setColor(Color.yellow);
		else if (i < 1000000)
			g.setColor(Color.cyan);
		else
			g.setColor(Color.white);
	}

	public void save() throws IOException {
		// Set up the FileWriter with our file name.
		FileWriter saveFile = new FileWriter(FILE_PATH);

		// Write the data to the file.
		saveFile.write("\n");
		saveFile.write(runs + "\n");
		saveFile.write(totalMoves + "\n");
		saveFile.write(totalLinesCleared + "\n");
		saveFile.write(totalPoints + "\n");
		saveFile.write(lastBestGeneRun + "\n");
		saveFile.write(runsSinceNewTree + "\n");
		saveFile.write(bestScoreEver + "\n");
		
		saveFile.write(bestGenes.getWallBonus() + "\n");
		saveFile.write(bestGenes.getHeightPenalty() + "\n");
		saveFile.write(bestGenes.getLineBonus() + "\n");
		saveFile.write(bestGenes.getOverhangPenalty() + "\n");
		saveFile.write(bestGenes.getBlockedPenalty() + "\n");
		saveFile.write(bestGenes.getPointsScored() + "\n");
		saveFile.write(bestGenes.getLinesCleared() + "\n");
		saveFile.write(bestGenes.getBlocksPlaced() + "\n");

		saveFile.write("\n");
		// All done, close the FileWriter.
		saveFile.close();

	}

	public void saveBestEverGenes() throws IOException {
		if (bestEverGenes.compareTo(bestGenes) == -1) {
			bestEverGenes = bestGenes;
			System.out.println("Best Genes Ever!");
			FileWriter saveFile = new FileWriter(FILE_PATH_BEST);
			saveFile.write("\n");
			saveFile.write(bestGenes.getWallBonus() + "\n");
			saveFile.write(bestGenes.getHeightPenalty() + "\n");
			saveFile.write(bestGenes.getLineBonus() + "\n");
			saveFile.write(bestGenes.getOverhangPenalty() + "\n");
			saveFile.write(bestGenes.getBlockedPenalty() + "\n");
			saveFile.write(bestGenes.getPointsScored() + "\n");
			saveFile.write(bestGenes.getLinesCleared() + "\n");
			saveFile.write(bestGenes.getBlocksPlaced() + "\n");

			saveFile.close();
		}
	}

	public void load() throws NumberFormatException, IOException {

		BufferedReader saveFile = new BufferedReader(new FileReader(FILE_PATH));

		// Throw away the blank line at the top.
		saveFile.readLine();
		// Get the integer value from the String.
		runs = Integer.parseInt(saveFile.readLine());
		totalMoves = Integer.parseInt(saveFile.readLine());
		totalLinesCleared = Integer.parseInt(saveFile.readLine());
		totalPoints = Integer.parseInt(saveFile.readLine());
		lastBestGeneRun = Integer.parseInt(saveFile.readLine());
		runsSinceNewTree = Integer.parseInt(saveFile.readLine());
		bestScoreEver = Integer.parseInt(saveFile.readLine());

		bestGenes.setWallBonus(Float.parseFloat(saveFile.readLine()));
		bestGenes.setHeightPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setLineBonus(Float.parseFloat(saveFile.readLine()));
		bestGenes.setOverhangPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setBlockedPenalty(Float.parseFloat(saveFile.readLine()));
		bestGenes.setPointsScored(Long.parseLong(saveFile.readLine()));
		bestGenes.addLinesCleared(Integer.parseInt(saveFile.readLine()));
		bestGenes.setBlocksPlaced(Integer.parseInt(saveFile.readLine()));

		// Not needed, but read blank line at the bottom.
		saveFile.readLine();
		saveFile.close();

		BufferedReader bestEverGenesFile = new BufferedReader(new FileReader(
				FILE_PATH_BEST));
		bestEverGenesFile.readLine();
		bestEverGenes.setWallBonus(Float.parseFloat(bestEverGenesFile
				.readLine()));
		bestEverGenes.setHeightPenalty(Float.parseFloat(bestEverGenesFile
				.readLine()));
		bestEverGenes.setLineBonus(Float.parseFloat(bestEverGenesFile
				.readLine()));
		bestEverGenes.setOverhangPenalty(Float.parseFloat(bestEverGenesFile
				.readLine()));
		bestEverGenes.setBlockedPenalty(Float.parseFloat(bestEverGenesFile
				.readLine()));
		bestEverGenes.setPointsScored(Long.parseLong(bestEverGenesFile
				.readLine()));
		bestEverGenes.addLinesCleared(Integer.parseInt(bestEverGenesFile
				.readLine()));
		bestEverGenes.setBlocksPlaced(Integer.parseInt(bestEverGenesFile
				.readLine()));
		bestEverGenesFile.close();

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

	public String listToString(List<Integer> list) {
		StringBuilder builder = new StringBuilder();
		for (Integer i : list) {
			builder.append(i).append("\n");
		}
		return builder.toString();
	}

	// Calculates average score of the last prevGamesCounted games
	public int calculateAverage() {
		int average = 0;
		for (Integer I : previousScores) {
			average += I;
		}
		return average / Math.min(prevGamesCounted, runsSinceLoad);
	}

	public void clearData() {
		previousScores.clear();
		recentPoints.clear();
		average = 0;
		runs = 0;
		currentGenes = new Genes();
		bestGenes = new Genes();
		currMoves = 0;
		totalMoves = 0;
		totalLinesCleared = 0;
		totalPoints = 0;
		lastBestGeneRun = 0;
		runsSinceNewTree = 0;
		for (int i = 0; i < 20; i++)
			recentPoints.add(0);
		for (int i = 0; i < prevGamesCounted; i++)
			previousScores.add(0);
		try {
			save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
