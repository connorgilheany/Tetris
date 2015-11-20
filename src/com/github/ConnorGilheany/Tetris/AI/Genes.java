package com.github.ConnorGilheany.Tetris.AI;

import java.util.Random;

public class Genes implements Comparable<Genes> {

    private float heightPenalty;
    private float overhangPenalty;
    private float lineBonus;
    private float blockedPenalty;
    private float wallBonus;

    private long pointsScored;
    private int linesCleared = 0;
    private int blocksPlaced = 0;
    private Random rand = new Random();


    /**
     * Initializes a set of genes with random attributes in the range -10.00, 10.00
     */
    public Genes() {
        wallBonus = rand.nextInt(1000) / (float) 100;
        heightPenalty = -(rand.nextInt(1000) / (float) 100);
        lineBonus = rand.nextInt(1000) / (float) 100;
        overhangPenalty = -(rand.nextInt(1000) / (float) 100);
        blockedPenalty = -(rand.nextInt(1000) / (float) 100);
    }

    /**
     * Changes the genes slightly in an effort to find a potential better fit
     * Only used on genes that already perform well
     */
    public void mutate() {
        int choice = rand.nextInt(5);
        float change = rand.nextInt(50) / (float) 50;
        if (rand.nextBoolean())
            change *= -1;
        //	System.out.printf("Change: %f, choice: %d\n", change, choice);

        switch (choice) {
            case 0:
                wallBonus += change;
                break;
            case 1:
                lineBonus += change;
                break;
            case 2:
                heightPenalty += change;
                break;
            case 3:
                overhangPenalty += change;
                break;
            case 4:
                blockedPenalty += change;
                break;
        }
    }

    @Override
    public String toString() {
        return "Genes: \n" +
                " wallBonus: " + wallBonus +
                ",\n heightPenalty: " + heightPenalty +
                ",\n lineBonus: " + lineBonus +
                ",\n overhangPenalty: " + overhangPenalty +
                ",\n blockedPenalty: " + blockedPenalty +
                ",\n pointsScored: " + pointsScored +
                ",\n linesCleared: " + linesCleared +
                ",\n blocksPlaced: " + blocksPlaced;

    }


    /**
     * @param o the genes to compare with
     * @return 1 if this object scored more points, -1 if the parameter scored more points, 0 if they're tied
     */
    @Override
    public int compareTo(Genes o) {
        if (o.getPointsScored() == this.getPointsScored())
            return 0;
        else if (o.getPointsScored() > this.getPointsScored())
            return -1;
        else if (o.getPointsScored() < this.getPointsScored())
            return 1;

        return 0;
    }

    public long getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(long l) {
        this.pointsScored = l;
    }

    public float getHeightPenalty() {
        return heightPenalty;
    }

    public void setHeightPenalty(float heightPenalty) {
        this.heightPenalty = heightPenalty;
    }

    public float getOverhangPenalty() {
        return overhangPenalty;
    }

    public void setOverhangPenalty(float overhangPenalty) {
        this.overhangPenalty = overhangPenalty;
    }

    public float getLineBonus() {
        return lineBonus;
    }

    public void setLineBonus(float lineBonus) {
        this.lineBonus = lineBonus;
    }

    public float getBlockedPenalty() {
        return blockedPenalty;
    }

    public void setBlockedPenalty(float blockedPenalty) {
        this.blockedPenalty = blockedPenalty;
    }

    public float getWallBonus() {
        return wallBonus;
    }

    public void setWallBonus(float wallBonus) {
        this.wallBonus = wallBonus;
    }

    /**
     * @return a copy of the current genes
     */
    public Genes copy() {
        Genes copy = new Genes();
        copy.setBlockedPenalty(blockedPenalty);
        copy.setHeightPenalty(heightPenalty);
        copy.setLineBonus(lineBonus);
        copy.setOverhangPenalty(overhangPenalty);
        copy.setWallBonus(wallBonus);
        return copy;
    }


    public void addLinesCleared(int amt) {
        linesCleared += amt;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public void setBlocksPlaced(int blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }


}
