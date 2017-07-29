package com.tyrfing.games.tyrlib3.edit.action;

import java.io.Serializable;
import java.util.Set;

import com.tyrfing.games.tyrlib3.edit.IActionStackListener;

public interface IAction extends Serializable {
	public void execute();
	
	public void continueExecute();
	
	public boolean canExecute();
	
	public Set<IActionStackListener> getBlockingListeners();
	
	public boolean isFinished();
	
	public boolean isBlocked();
	
	public void undo();
}
