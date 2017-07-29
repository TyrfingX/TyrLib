package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.model.ai.Heuristic;
import com.tyrfing.games.id18.model.ai.MiniMaxAlgorithm;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.ActionStack;

public class AiFactory {
	public static final int MAX_DEPTH = 5;
	
	public static final AiFactory INSTANCE = new AiFactory(MAX_DEPTH);
	
	private int maxDepth;
	
	public AiFactory(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public AiActionProvider createAi(BattleDomain battleDomain, Faction faction) {
		Battle battle = battleDomain.getBattle();
		Heuristic heuristic = new Heuristic(battle, faction);
		ActionStack battleActionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		
		MiniMaxAlgorithm minMaxAlgorithm = new MiniMaxAlgorithm(battle, battleActionStack, heuristic, maxDepth);
		
		AiActionProvider ai = new AiActionProvider(minMaxAlgorithm);
		battleDomain.getFactionActionProviders().add(ai);
		
		return ai;
	}
}
