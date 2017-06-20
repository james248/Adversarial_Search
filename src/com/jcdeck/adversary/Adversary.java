package com.jcdeck.adversary;

/**
 * Contains the method that will take the current state and expand
 * it to find the best action to take from that state.
 * 
 * @author James C Decker
 *
 */
public class Adversary {
	
	/**
	 * Prevents {@code Adversary} from being initialized
	 */
	private Adversary(){
		
	}
	
	/**
	 * Calculates the best action for {@code playerIndex} to take. If {@code startState}
	 * is null, null will be returned. If there are no possible sub-states, null will
	 * be returned.If {@code depth} is less than 1, it will be changed to 1.
	 * 
	 * @param startState Current state of the game
	 * @param playerIndex index of the player whose turn it is
	 * @param depth maximum depth to search. If depth equals 1, then the computer
	 * will only plan for one action.
	 * @return the best action for {@code playerIndex} to take from the current state
	 */
	public static Action getOptimalAction(State startState, int playerIndex, int depth){
		
		//prevent null pointer exception
		if(startState == null)
			return null;
		
		//depth must be a minimum of one
		if(depth<=0)
			depth = 1;

		//get all possible sub states from this state
		final Action[] possibleActions = startState.getPossibleActions();
		
		//if there are no sub-state (i.e. this is an end state), return null
		if(possibleActions.length == 0)
			return null;
		
		//if there is only one possible action to take it must take that one
		if(possibleActions.length == 1)
			return possibleActions[0];
		
		//use the possible actions to get the sub-states
		State[] children = startState.expand(possibleActions);
		
		//create array to hold the value of each state
		Value[] values = new Value[children.length];
		
		//for each sub-state, get the value
		for(int i = 0; i<children.length; i++){
			System.out.print(" Expanding "+(i+1)+" of "+children.length+" - "+possibleActions[i]);
			values[i] = children[i].getValue(depth-1);
			System.out.println(" - Value: "+values[i].getCalculatedScore(playerIndex)+" Depth: "+values[i].getDepth());
		}
		
		//get the best action for 'playerIndex'
		final int bestSubState = Value.getBestValueIndex(values, playerIndex);
		
		return children[bestSubState].getAction();
	}
	
	/**
	 * Returns the name of this class.
	 */
	@Override
	public String toString(){
		return "com.jcdeck.adversary.Adversary";
	}
	
}
