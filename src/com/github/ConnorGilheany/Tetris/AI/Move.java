package com.github.ConnorGilheany.Tetris.AI;

import com.github.ConnorGilheany.Tetris.Game;
import com.github.ConnorGilheany.Tetris.Map.Block;
import com.github.ConnorGilheany.Tetris.Map.Map;

public class Move {

    public int l, w, b, o;
    float a;
    private int x, y;
    private int rotations;
    private double points;

    /**
     * Creates a Move to be tested by the AI
     *
     * @param x         the x position of the piece
     * @param y         the y position of the piece
     * @param rotations the # of rotations of the piece
     */
    public Move(int x, int y, int rotations) {
        this.x = x;
        this.y = y;
        this.rotations = rotations;
    }

    /**
     * Calculates the amount of points this move would score
     *
     * @param genes the genes to base the score on
     * @param game  the game being played
     * @return the amount of points this move would score
     */
    public double calculatePoints(Genes genes, Game game) {
        l = calculateLines(game);
        points += genes.getLineBonus() * Math.pow(l, 1.5); //1, 2.83, 5.2, 8

        a = averageHeights(game);
        points += Math.pow(genes.getHeightPenalty(), 1) * a;

        w = walledPieces(game);
        points += genes.getWallBonus() * w;

        Map map = game.getMap();
        if (l != 0) map = makeNewMap(map, game);

        b = blockedPieces(map);
        points += genes.getBlockedPenalty() * b;

        o = overhangs(map);
        points += genes.getOverhangPenalty() * o;

        return points;
    }

    /**
     * Simulates removing the completed lines in the map
     *
     * @param oldMap the map to copy
     * @param game   the game being played
     * @return the new map with completed lines removed
     */
    private Map makeNewMap(Map oldMap, Game game) {
        Map newMap = oldMap.copy();
        game.getCurrentPiece().cement(newMap);
        newMap.checkCompletion();
        return newMap;
    }

    /**
     * @param map the map to count
     * @return the number of overhangs in the map
     */
    private int overhangs(Map map) {

        Block[][] blocks = map.getBlocks();
        int count = 0;
        for (int x = 0; x < blocks[0].length; x++) {

            boolean openPieceReached = false;
            for (int y = 21; y > 0; y--) {

                if (blocks[y][x] == null) {
                    openPieceReached = true;
                } else {
                    if (openPieceReached) count++;
                }

            }
        }
        return count;
    }

    /**
     * @param map the map to count
     * @return the number of blocked pieces in the map
     */
    private int blockedPieces(Map map) {
        Block[][] blocks = map.getBlocks();
        int count = 0;
        for (int x = 0; x < blocks[0].length; x++) {

            boolean topPieceReached = false;
            for (int y = 0; y < blocks.length; y++) {

                if (blocks[y][x] != null) {
                    topPieceReached = true;
                } else {
                    if (topPieceReached) count++;
                }

            }
        }
        return count;
    }

    /**
     * Calculates the average height of the current game piece
     *
     * @param game the game being played
     * @return the average height of the current game piece
     */
    private float averageHeights(Game game) {
        float totalHeight = 0;
        float count = 0;
        boolean[][] shape = game.getCurrentPiece().getShape().getCurrentShape();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x]) {
                    totalHeight += (22 - (this.y + y));
                    count++;
                }
            }
        }
        return totalHeight / count;
    }

    /**
     * Calculates the number of pieces on the wall
     *
     * @param game the game being played
     * @return the number of pieces on the wall
     */
    private int walledPieces(Game game) {
        int count = 0;
        for (int y = 0; y < game.getCurrentPiece().getShape().getCurrentShape().length; y++) {
            boolean[] row = game.getCurrentPiece().getShape().getCurrentShape()[y];

            if (x == 0 && row[0]) count++;
            else if (x + row.length == game.getMap().getBlocks()[y].length && row[row.length - 1]) count++;
        }
        //	System.out.printf("Walled pieces: %d\n", count);
        return count;
    }

    /**
     * Calculates the number of lines that are filled
     *
     * @param game the game being played
     * @return the number of lines filled
     */
    private int calculateLines(Game game) {
        int count = 0;
        for (int y = 0; y < game.getMap().getBlocks().length; y++) {
            Block[] row = game.getMap().getBlocks()[y];
            boolean full = true;
            for (int x = 0; x < row.length; x++) {
                if (row[x] == null) full = false;
            }
            if (full) count++;
        }
        //	System.out.printf("Completed lines: %d\n", count);
        return count;
    }

    /**
     * @return the amount of points this move would score
     */
    public double getPoints() {
        return points;
    }

    public int getRotations() {
        return rotations;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Move: x=%d, y=%d, rotations=%d, points=%.2f\nLinesCleared=%d AverageHeights=%.2f WalledPieces=%d Overhangs=%d BlockedPieces=%d", x, y, rotations, points, l, a, w, o, b);
    }
}
