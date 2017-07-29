package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.ai.MinMaxAlgorithm;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;

public class AiActionProvider extends AFactionActionProvider {
	
	private Battle battle;
	private MinMaxAlgorithm minMaxAlgorithm;
	
	public AiActionProvider(MinMaxAlgorithm minMaxAlgorithm) {
		super(minMaxAlgorithm.getHeuristic().getFaction());
		
		this.minMaxAlgorithm = minMaxAlgorithm;
		this.battle = minMaxAlgorithm.getBattle();
	}
	
	public void requestAction(IActionRequester actionRequester) {
		ActionStack actionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		
		int depth = 0;
		EvaluatedAction action = minMaxAlgorithm.computeAction(depth, actionStack);
		actionRequester.onProvideRequest(action.getAction());
	}
}
