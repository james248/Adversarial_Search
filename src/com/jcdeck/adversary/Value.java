package com.jcdeck.adversary;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that holds the score for each player in the game for a state. It is
 * used to find the best action for an AI Adversary to take.
 * 
 * @author James
 *
 */
public final class Value {
	
	//CONSTRUCTORS
	
	/**
	 * Constructs a new Value with {@code numOfPlayers} players. Each player's
	 * score is set to 0;
	 * 
	 * @param numOfPlayers The number of players to be represented with
	 * this Value object
	 */
	public Value(int numOfPlayers){
		this.predictedScores = new double[numOfPlayers];
	}
	
	/**
	 * Constructs a new Value and sets each player's predicted score
	 * to their predicted score in the array passed. If any element
	 * in {@code predictedScores} is negative, it will be set to 0;
	 * 
	 * @param predictedScores a double array that holds each player's predicted score
	 */
	public Value(double[] predictedScores){
		this.predictedScores = new double[predictedScores.length];
		for(int i= 0; i<this.getNumOfPlayers(); i++)
			this.setScore(i, predictedScores[i]);
	}
	
	/**
	 * Constructs a new Value object based on the weights of each of the Value
	 * objects in {@code values}. This should be used when a player is not making
	 * an action; the best value cannot be determined because a random action will
	 * occur. The weight of each Value will determine how much influence it has
	 * on this new object. Conducts weighted element-wise addition of {@code predictedScores}.
	 * 
	 * @param values array of Value objects to be added based on their weight
	 */
	Value(Value[] values){
		this.predictedScores = new double[values[0].predictedScores.length];
		//for the score of each player add up the scores from that player for each value in 'values'
		for(int i = 0; i<this.predictedScores.length; i++)
			for(int j = 0; j<values.length; j++)
				this.predictedScores[i] += values[j].predictedScores[i];
	}
	
	
	
	//PREDICTED SCORES
	
	/**
	 * An array that holds all the predicted scores for each player for a state.
	 */
	private double[] predictedScores;
	
	//GETTERS
	
	/**
	 * Returns the number of players in the game. This is the number of predicted
	 * scores the Value object contains.
	 * 
	 * @return	number of players in the game. Both human and AI
	 */
	public int getNumOfPlayers(){
		return this.predictedScores.length;
	}

	/**
	 * Returns the score of the player at index {@code player}. If {@code player}
	 * is out of the bounds of the array {@code predictedScores} then NaN
	 * will be returned.
	 * 
	 * @param player index of the player's score to return
	 * @return score of {@code player}. NaN if an out of bounds exception will be thrown
	 */
	public double getScore(int player){
		//if 'player' is out of range return NAN
		if(!this.legalPlayerIndex(player))
			return Double.NaN;
		//otherwise return the score of the player at index player
		return this.predictedScores[player];
	}
	
	/**
	 * Returns the score of {@code player} over the sum of all other
	 * scores.
	 * 
	 * @param player index of player whose score is being calculated
	 * @return calculated score of {@code player}
	 */
	public double getCalculatedScore(int player){
		
		//check is 'player' is a legal index
		if(!this.legalPlayerIndex(player))
			return 0;
		
		//calculate the sum of all other player's scores
		double sum = 0;
		for(int i = 0; i<this.getNumOfPlayers(); i++)
			if(i != player)
				sum += this.getScore(i);
		
		//if the sum is 0 (no other players have points) - return the raw score
		if(sum == 0)
			return this.getScore(player);
		
		//if the sum 
		return this.getScore(player) / sum;
	}
	
	//SETTERS
	
	/**
	 * Sets the score of {@code player} to {@code score}. If {@code player}
	 * is not in the bounds of {@code predictedScores} it will do nothing.
	 * If {@code score} is less than 0, it will be set to 0;
	 * 
	 * @param player index of the player whose score is being set
	 * @param score new score for {@code player}
	 */
	public void setScore(int player, double score){
		if(this.legalPlayerIndex(player))
			this.predictedScores[player] = Math.max(0, score);
	}
	
	/**
	 * Adds {@code deltaScore} to the predicted score of {@code player}. If
	 * {@code player} is not in the bounds of {@code predictedScores}, it
	 * will do nothing. If the calculating results in a negative score, the
	 * score will be set to 0.
	 * 
	 * @param player index of score to change
	 * @param deltaScore amount to change the score
	 */
	public void changeScore(int player, double deltaScore){
		if(this.legalPlayerIndex(player))
			this.setScore(player, this.getScore(player) + deltaScore);
	}
	
	/**
	 * Multiplies {@code player}'s score by {@code multiplier} If {@code player}
	 * is not in the bounds of {@code predictedScores} it will do nothing.
	 * If {@code multiplier} is less than 0, it will be set to 0;
	 * 
	 * @param player index of score to change
	 * @param multiplier value that will be multiplied to the score
	 */
	public void adjustScore(int player, double multiplier){
		if(this.legalPlayerIndex(player))
			this.setScore(player, this.getScore(player) * multiplier);
	}
	
	/**
	 * Returns true if {@code player} is within the bounds
	 * of {@code predictedScores}. false otherwise
	 * 
	 * @param player index to check
	 * @return true if {@code player} is in the bounds of {@code predictedScores}
	 */
	private boolean legalPlayerIndex(int player){
		if(player<0)
			return false;
		if(player>=this.getNumOfPlayers())
			return false;
		return true;
	}
	
	
	//DEPTH

	/**
	 * The depth at at which this value object was calculated. Used when choosing between
	 * multiple value objects that have the same calculated score for a player - the value
	 * with the shortest depth will be chosen
	 */
	private int depth;
	
	/**
	 * Returns the depth at which this value object was calculated.
	 * 
	 * @return the depth at which this value object was calculated.
	 */
	int getDepth(){
		return this.depth;
	}
	
	/**
	 * Sets the depth at which this value object was calculated.
	 * 
	 * @param depth the depth at which this value object was calculated.
	 */
	void setDepth(int depth){
		this.depth = depth;
	}
	
	
	//METHODS
	
	
	/**
	 * Multiplies each player's predicted score by {@code weight}. It
	 * is used to weight different values according to their probability
	 * during a random state. If weight is less than 0, it will be set to 0.
	 * 
	 * @param weight The weight to multiply all player's predicted scores by
	 */
	void weight(double weight){
		//Iterate through all predicted scores of the players
		for(int i = 0; i<this.getNumOfPlayers(); i++)
			//multiply it by 'weight'
			this.setScore(i, this.getScore(i)*weight);
	}
	
	
	/**
	 * Returns the index of the Value in {@code values} that is the best
	 * for player {@code playerIndex}. If there are multiple elements
	 * in {@code values} that are equally the best for {@code playerIndex} then
	 * a random one of those will be returned.
	 * 
	 * <p>
	 * 
	 * The best value for a player is calculated by dividing that player's predicted
	 * score by the sum of all other predicted scores for the {@code Value} object.
	 * 
	 * <p>
	 * 
	 * If {@code values} does not have any elements -1 will be returned.
	 * 
	 * @param values array of values to search through
	 * @param playerIndex index of the player whose predicted score will be maximized
	 * @return The index of the Value in {@code values} that is best for {@code playerIndex}
	 */
	static int getBestValueIndex(Value[] values, int playerIndex){
		
		//if no values were passed, return -1
		if(values.length==0)
			return -1;
		
		//if there is only one value, it must be the max
		if(values.length==1)
			return 0;
		
		//only run through it again if there are different depths
		boolean differentDepths = false;
		
		//find the Value in values that has the greatest value for playerIndex
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		double best = values[0].getCalculatedScore(playerIndex);
		indexes.add(0);
		//if any depth is different than the first depth there must be different depths
		final int uniformDepth = values[0].getDepth();
		for(int i = 1; i<values.length; i++){
			double test = values[i].getCalculatedScore(playerIndex);
			int testDepth = values[i].getDepth();
			//if any depth is different than the first depth there must be different depths
			if(testDepth != uniformDepth)
				differentDepths = true;
			//add test to indexes if it is better than or equal to the best value in 'values'
			if(test==best)
				indexes.add(i);
			else if(test > best){
				best = test;
				indexes.clear();
				indexes.add(i);
			}
		}
		
		
		if(differentDepths){
			
			//if there are multiple Value objects that are the best, choose
			//the one with the shortest depth
			Integer[] bestIndexes = indexes.toArray(new Integer[indexes.size()]);
			indexes.clear();
			int bestDepth = values[bestIndexes[0]].getDepth();
			for(Integer i : bestIndexes){
				//test the depth at one of the best indexes
				int testDepth = values[i].getDepth();
				if(testDepth==bestDepth)
					indexes.add(i);
				else if(testDepth < bestDepth){
					bestDepth = testDepth;
					indexes.clear();
					indexes.add(i);
				}
			}
			
		}
		
		
		final int rd = (int) (Math.random()*(indexes.size()));
		return indexes.get(rd);
		
	}
	
	/**
	 * Returns all of the predicted scores held in this {@code Value} object.
	 * 
	 */
	@Override
	public String toString(){
		return "com.jcdeck.adversary.Value: "+Arrays.toString(this.predictedScores);
	}
	
}
