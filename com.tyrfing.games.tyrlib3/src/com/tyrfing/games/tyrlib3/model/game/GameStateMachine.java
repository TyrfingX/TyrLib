package com.tyrfing.games.tyrlib3.model.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.tyrlib3.model.data.Pair;

public class GameStateMachine<T> {
	public List<GameState> gameStates;
	private Map<Pair<GameState, T>, GameState> transitions;
	
	private GameState currentState;
	
	public GameStateMachine() {
		gameStates = new ArrayList<GameState>();
		transitions = new HashMap<Pair<GameState, T>, GameState>();
	}
	
	public void addGameState(GameState state) {
		gameStates.add(state);
	}
	
	public GameState getGameState(int index) {
		return gameStates.get(index);
	}
	
	public void removeGameState(int index) {
		gameStates.remove(index);
	}
	
	public void removeGameState(GameState state) {
		gameStates.remove(state);
	}
	
	public void enterState(GameState state) {
		state.enterGameState();
		currentState = state;
	}
	
	public void enterState(int index) {
		enterState(getGameState(index));
	}
	
	public void addTransition(GameState start, T input, GameState end) {
		Pair<GameState, T> pair = new Pair<GameState, T>(start, input);
		transitions.put(pair, end);
	}
	
	public void addTransition(int index1, T input, int index2) {
		addTransition(getGameState(index1), input, getGameState(index2));
	}
	
	public boolean step(T input) {
		GameState newState = transitions.get(new Pair<GameState, T>(currentState, input));
		if (newState == null) {
			return false;
		} else {
			transistStates(newState);
			return true;
		}
	}
	
	public void transistStates(GameState newState) {
		currentState.leaveGameState();
		newState.enterGameState();
		currentState = newState;
	}
	
	public GameState getCurrentState() {
		return currentState;
	}
}
