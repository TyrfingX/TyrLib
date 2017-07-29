package com.tyrfing.games.tyrlib3.edit.action;

import java.util.HashSet;
import java.util.Set;

import com.tyrfing.games.tyrlib3.edit.IActionStackListener;

public abstract class AAction implements IAction {

	private Set<IActionStackListener> blockingListeners;
	
	public AAction() {
		blockingListeners = new HashSet<IActionStackListener>();
	}

	@Override
	public void continueExecute() {
		
	}
	
	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public Set<IActionStackListener> getBlockingListeners() {
		return blockingListeners;
	}
	
	@Override
	public boolean isBlocked() {
		return !blockingListeners.isEmpty();
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}
}
