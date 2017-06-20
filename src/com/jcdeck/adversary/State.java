package com.jcdeck.adversary;

/**
 * 
 * Represents a state of the game. It can be expanded to get the possible states
 * by taking actions. It will evaluate how good this state is for each player so
 * the computer can choose the optimal action from any state.
 * 
 * @author James C Decker
 *
 */
public abstract class State {
	
	//DEPTH
	
	/**
	 * the depth this state occurs in the search tree. 0 is the start state
	 */
	private int depth;
	
	/**
	 * Sets the depth of the state. Called only once after the state
	 * is created. It does not change for a state.
	 * 
	 * @param depth the new depth of the state
	 */
	private final void setDepth(int depth){
		this.depth = depth;
	}
	
	
	//ACTION TO REACH THIS STATE
	
	/**
	 * the action taken to get to this state
	 */
	protected Action a;
	
	/**
	 * Returns the action taken to get to this state. Only called by
	 * {@link Adversary}  on the first set of sub-states to get the
	 * best action to take.
	 * 
	 * @return action that was taken to get to this state
	 */
	final Action getAction(){
		return this.a;
	}
	
	/**
	 * Sets the action that was taken to get to this state. Only called once
	 * when the state is created.
	 * 
	 * @param a
	 */
	private final void setAction(Action a){
		this.a = a;
	}
	
	
	//METHODS FOR EXPANDING THIS STATE
	
	/**
	 * Returns a Value object that represents the value of this state based on
	 * which player's turn it is or if a random action will occure. It will expand
	 * itself to a depth of 'maxDepth' to calculate the value of this state. It
	 * acts as a expecta-mini-max function.
	 * 
	 * @param maxDepth The max depth to expand the tree.
	 * @param alpha Used for pruning
	 * @param beta Used for pruning
	 * @return The value of this state
	 */
	final Value getValue(int maxDepth){
		
		//if this state is at the max depth return the value of this state
		if(maxDepth == depth)
			return evaluateState();
		
		//if not - must continue expanding tree
		
		Action[] possibleActions = this.getPossibleActions();
		
		//get the value of the the child states and return the best choice
		//depending on which player's turn it is.
		State[] children = expand(possibleActions);
		
		//if there are no sub-states, then this is an end state
		if(children==null || children.length==0)
			return evaluateState();
		
		//set the depth of the children to one more than the depth of this state
		for(State s : children)
			s.setDepth(this.depth+1);
		
		//get an array of values from the array of children
		Value[] values = new Value[children.length];
		for(int i = 0; i<children.length; i++)
			values[i] = children[i].getValue(maxDepth);
		
		
		//get the index of the player that will take an action from this state
		int turn = this.getTurn();
		
		/* If a player will take an action from this state:
		 * 
		 * find the value of the child states that is best
		 * for the player who will take make the move from this state 
		 */
		if(turn >= 0)
			return values[Value.getBestValueIndex(values, turn)];
		
		/* If a random action will take place from this state:
		 * 
		 * Calculate the value of this state based on the probability
		 * of each sub-state
		 */
		
		//weight each value
		for(int i = 0; i<values.length; i++)
			values[i].weight(possibleActions[i].getProbability());
		
		//return the sum of all the weighted values of all the sub-states
		return new Value(values);
		
	}
	
	/**
	 * Return an array of all possible sub states from this
	 * state. If a random action will happen, Each sub state represent
	 * the outcome of the random action. If there are no possible sub-states
	 * an empty array will be returned.
	 * 
	 * @return array of all possible sub states
	 */
	final State[] expand(Action[] possibleActions){
		
		
		//if actions is null or has no elements, then it was a end state - there are so sub-states
		if(possibleActions==null || possibleActions.length==0)
			return new State[0];
		
		//create an array to hold the sub-states
		State[] states = new State[possibleActions.length];
		
		//get each possible sub state by performing all possible actions from this state
		for(int i = 0; i<states.length; i++){
			//get the resulting state of actions[i]
			states[i] = performAction(possibleActions[i]);
			//have the new state recored what action was taken to get to that state
			states[i].setAction(possibleActions[i]);
		}
		
		//return the sub-states
		return states;
		
	}
	
	/**
	 * Returns the value for this state. Records the depth of this state
	 * in the {@code Value} object it returns. Calls {@link evaluate()}
	 * 
	 * @return the value object of this state
	 */
	final private Value evaluateState(){
		
		Value value = this.evaluate();
		
		value.setDepth(this.depth);
		
		return value;
		
	}
	
	
	//ABSTRACT METHODS
	
	/**
	 * Returns a list of all the possible actions that
	 * could be taken from this state. If the next state is randomly
	 * determined, then the actions should reflect the possible
	 * outcomes of the random entity. Note: if the actions are random,
	 * they must each be able to return the probability of happening.
	 * {@link Action#getProbability()}.
	 * 
	 * <p>
	 * 
	 * If there are no possible actions (e.g. there is a winner in this state,
	 * then a empty array or null should be returned.
	 * 
	 * <p>
	 * 
	 * Random action example: if one player has completed their turn and the
	 * next player rolls a die before choosing an action, then the
	 * Action[] should contain the actions 1-6 of the die.
	 * 
	 * @return An array of all possible actions from this state
	 */
	public abstract Action[] getPossibleActions();
	
	/**
	 * This should return a Value object where each players score is
	 * calculated and stored in the object
	 * 
	 * @return A Value object for this state
	 */
	protected abstract Value evaluate();
	
	/**
	 * Returns the index of the players whose turn it is. (i.e. the 
	 * player that will move next). If no one will move next and instead
	 * a random action will happen, -1 should be returned.
	 * 
	 * @return The index of the player who will make a move from this state. -1 if a random
	 * action will happen
	 */
	protected abstract int getTurn();
	
	/**
	 * Creates a new state that is the result of performing action {@code a}
	 * on this state. The Action Interface does not contain any methods. In the
	 * overriding function, {@code a} should be cast to a class in the
	 * program that implements opponent.Action.
	 * 
	 * <p>
	 * 
	 * The current state should not be modified by this method.
	 * 
	 * @param a action to take from this state
	 * @return a new State object that is the result of doing action a from this state
	 */
	public abstract State performAction(Action a);
	
	
	//toString
	/**
	 * Returns the index of the player whose turn it is and the depth of this state
	 * in the search tree.
	 */
	@Override
	public String toString(){
		return "com.jcdeck.adversary.State Turn: "+this.getTurn()+" Depth: "+this.depth;
	}
	
	
}
