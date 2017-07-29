package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.ai.Heuristic;
import com.tyrfing.games.id18.model.ai.MiniMaxAlgorithm;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.model.ai.AMiniMaxAlgorithm;
import com.tyrfing.games.tyrlib3.model.ai.EvaluatedAction;

public class AiActionProvider extends AFactionActionProvider {
	
	private AMiniMaxAlgorithm minMaxAlgorithm;
	
	public AiActionProvider(MiniMaxAlgorithm minMaxAlgorithm) {
		super(((Heuristic) minMaxAlgorithm.getHeuristic()).getFaction());
		
		this.minMaxAlgorithm = minMaxAlgorithm;
	}
	
	public void requestAction(IActionRequester actionRequester) {
		EvaluatedAction action = minMaxAlgorithm.computeAction();
		actionRequester.onProvideRequest(action.getAction());
	}
	
	public AMiniMaxAlgorithm getMinMaxAlgorithm() {
		return minMaxAlgorithm;
	}
}
