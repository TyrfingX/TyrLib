package com.tyrfing.games.tyrlib3.edit;

import com.tyrfing.games.tyrlib3.edit.action.IAction;

public interface IActionStackListener {
	public void onPreExecuteAction(IAction action);
	public void onPostExecuteAction(IAction action);
}
