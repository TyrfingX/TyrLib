package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.ai.EvaluatedAction;
import com.tyrfing.games.id18.model.ai.MinMaxAlgorithm;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;

public class AiActionProvider extends AFactionActionProvider {
	
	private MinMaxAlgorithm minMaxAlgorithm;
	
	public AiActionProvider(MinMaxAlgorithm minMaxAlgorithm) {
		super(minMaxAlgorithm.getHeuristic().getFaction());
		
		this.minMaxAlgorithm = minMaxAlgorithm;
	}
	
	public void requestAction(IActionRequester actionRequester) {
		EvaluatedAction action = minMaxAlgorithm.computeAction();
		actionRequester.onProvideRequest(action.getAction());
	}
	
	public MinMaxAlgorithm getMinMaxAlgorithm() {
		return minMaxAlgorithm;
	}
}
