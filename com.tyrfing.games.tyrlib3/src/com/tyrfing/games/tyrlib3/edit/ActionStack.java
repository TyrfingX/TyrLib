package com.tyrfing.games.tyrlib3.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.model.game.IUpdateable;

public class ActionStack implements IUpdateable {
	private Stack<IAction> actions;
	private List<IAction> executing;
	private List<IActionStackListener> actionListeners;
	
	public ActionStack() {
		actions = new Stack<IAction>();
		actionListeners = new ArrayList<IActionStackListener>();
		executing = new ArrayList<IAction>();
	}
	
	public List<IActionStackListener> getActionListeners() {
		return actionListeners;
	}
	
	public Stack<IAction> getActions() {
		return actions;
	}
	
	public void execute(IAction action) {	
		notifyPreExecute(action);
		
		action.execute();
		actions.add(action);
		
		notifyPostExecute(action);
		
		while (!action.isFinished() && !action.isBlocked()) {
			action.continueExecute();
			notifyPostExecute(action);
		}
		
		if (action.isBlocked()) {
			executing.add(action);
		}
	}
	
	public void notifyPreExecute(IAction action) {
		for (IActionStackListener listener : actionListeners) {
			listener.onPreExecuteAction(action);
		}
	}
	
	public void notifyPostExecute(IAction action) {
		for (IActionStackListener listener : actionListeners) {
			listener.onPostExecuteAction(action);
		}
	}
	
	public IAction undo() {
		IAction action = actions.pop();
		action.undo();
		return action;
	}

	@Override
	public void onUpdate(float time) {
		List<IAction> finishedActions = new ArrayList<IAction>();
		
		for (IAction action : executing) {
			while (!action.isFinished() && !action.isBlocked()) {
				action.continueExecute();
				notifyPostExecute(action);
			}
			
			if (action.isFinished()) {
				finishedActions.add(action);
			}
		}
		
		executing.removeAll(finishedActions);
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
