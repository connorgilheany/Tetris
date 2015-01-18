package com.github.cman85.Tetris.AI;

import java.util.Random;

public class Genes {
	
	private float heightPenalty;
	private float overhangPenalty;
	private float lineBonus;
	private float blockedPenalty;
	private float wallBonus;
	
	private long pointsScored;
	private int linesCleared = 0;
	private Random rand = new Random();

	
	public Genes(){
		wallBonus = rand.nextInt(1000) / (float)100;
		heightPenalty = -(rand.nextInt(1000) / (float)100);
		lineBonus = rand.nextInt(1000) / (float)100;
		overhangPenalty = -(rand.nextInt(1000) / (float)100);
		blockedPenalty = -(rand.nextInt(1000) / (float)100);
	}
	
	public void mutate(){
		int choice = rand.nextInt(5);
		float change = rand.nextInt(100) / (float)50;
		if(rand.nextBoolean())
			change *= -1;
		System.out.printf("Change: %f, choice: %d\n", change, choice);

		switch(choice){
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
	
	public Genes mate(Genes other){
		if(other.getPointsScored() > this.getPointsScored()){
			return other;
		}
		return this;
	}
	
	@Override
	public String toString(){
		return "Genes: \n" +
				" wallBonus: " + wallBonus +
				",\n heightPenalty: " + heightPenalty +
				",\n lineBonus: " + lineBonus +
				",\n overhangPenalty: " + overhangPenalty +
				",\n blockedPenalty: " + blockedPenalty +
				",\n linesCleared: " + linesCleared +
				",\n pointsScored: " + pointsScored;
	}
	
	public int compareTo(Object o){
		if(o.equals(o))
			; //Used to throw nullpointer to conform to java standards
		if(o instanceof Genes){
			Genes other = (Genes)o;
			if(other.getPointsScored() == this.getPointsScored())
				return 0;
			else if(other.getPointsScored() > this.getPointsScored())
				return -1;
			else if(other.getPointsScored() < this.getPointsScored())
				return 1;
		}
		return 0;
	}

	public long getPointsScored() {
		return pointsScored;
	}
	
	public void setPointsScored(long l){
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

	public Genes copy() {
		Genes copy = new Genes();
		copy.setBlockedPenalty(blockedPenalty);
		copy.setHeightPenalty(heightPenalty);
		copy.setLineBonus(lineBonus);
		copy.setOverhangPenalty(overhangPenalty);
		copy.setWallBonus(wallBonus);
		return copy;
	}
	
	public void addLinesCleared(int amt){
		linesCleared += amt;
	}
	public int getLinesCleared(){
		return linesCleared;
	}

}
