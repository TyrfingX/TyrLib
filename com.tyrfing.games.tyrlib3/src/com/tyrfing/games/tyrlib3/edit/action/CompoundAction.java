package com.tyrfing.games.tyrlib3.edit.action;

import java.util.ArrayList;
import java.util.List;

public class CompoundAction extends AAction {
	private List<IAction> actions;
	
	private int currentlyExecuting;
	private boolean isContinuingExecute;
	
	public CompoundAction() {
		actions = new ArrayList<IAction>();
	}
	
	public List<IAction> getActions() {
		return actions;
	}

	@Override
	public void execute() {
		if (actions.size() > 0) {
			currentlyExecuting = 0;
			isContinuingExecute = false;
			continueExecute();
		}
	}

	@Override
	public boolean canExecute() {
		return actions.isEmpty() ? true : actions.get(0).canExecute();
	}

	@Override
	public void undo() {
		for (int i = actions.size() - 1; i >= 0; --i) {
			actions.get(i).undo();
		}
	}

	@Override
	public void continueExecute() {
		IAction action = actions.get(currentlyExecuting);
		
		if (isContinuingExecute) {
			action.continueExecute();
		} else {
			action.execute();
		}
		
		if (action.isFinished()) {
			currentlyExecuting++;
			
			if (isFinished()) {
				isContinuingExecute = false;
			}
		} else {
			isContinuingExecute = true;
		}
	}

	public IAction getExecuting() {
		if (isContinuingExecute) {
			return actions.get(currentlyExecuting);
		} else if (currentlyExecuting > 0){
			return actions.get(currentlyExecuting - 1);
		} else if (actions.size() > 0) {
			return actions.get(0);
		} else {
			return null;
		}
	}

	public void appendCurrentlyExecutingAction(IAction action) {
		actions.add(currentlyExecuting, action);
	}
	
	@Override
	public boolean isFinished() {
		return currentlyExecuting == actions.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CompoundAction[");
		for (IAction action : actions) {
			sb.append(action.toString());
			if (action != actions.get(actions.size() - 1)) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
