package com.tyrfing.games.tyrlib3.edit;

import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;

public abstract class ACompoundedActionListener implements IActionStackListener {

	@Override
	public void onPreExecuteAction(IAction action) {
		if (action instanceof CompoundAction) {
			onPreExecuteCompoundAction((CompoundAction) action);
		} 
	}
	
	private void onPreExecuteCompoundAction(CompoundAction compoundAction) {
		IAction action = compoundAction.getExecuting();
		
		if (action instanceof CompoundAction) {
			onPreExecuteAction(action);
		} else {
			onPreExecuteCompoundedAction(compoundAction, action);
		}
	}
	
	protected abstract void onPreExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction);

	@Override
	public void onPostExecuteAction(IAction action) {
		if (action instanceof CompoundAction) {
			onPostExecuteCompoundAction((CompoundAction) action);
		} 
	}
	
	private void onPostExecuteCompoundAction(CompoundAction compoundAction) {
		IAction action = compoundAction.getExecuting();
		
		if (action instanceof CompoundAction) {
			onPostExecuteAction(action);
		} else {
			onPostExecuteCompoundedAction(compoundAction, action);
		}
	}
	
	protected abstract void onPostExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction);
}
