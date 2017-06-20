package com.jcdeck.adversary;

/**
 * This is used to take actions from states. It will be what is returned
 * by the search algorithm.
 * 
 * @author James C Decker
 *
 */
public interface Action {
	

	/**
	 * Returns the probability that this action will be taken. This will
	 * only be called if a random action will be made from a given state.
	 * 
	 * @return A double between 0 and 1 that represents the probability
	 * of this action being taken from a given state.
	 */
	double getProbability();
	
}
