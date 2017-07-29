package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.model.ai.Heuristic;
import com.tyrfing.games.id18.model.ai.MinMaxAlgorithm;
import com.tyrfing.games.id18.model.unit.Faction;

public class AiFactory {
	public static final int MAX_DEPTH = 5;
	
	public static final AiFactory INSTANCE = new AiFactory(MAX_DEPTH);
	
	private int maxDepth;
	
	public AiFactory(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public AiActionProvider createAi(BattleDomain battleDomain, Faction faction) {
		Heuristic heuristic = new Heuristic(battleDomain.getBattle(), faction);
		MinMaxAlgorithm minMaxAlgorithm = new MinMaxAlgorithm(battleDomain, heuristic, maxDepth);
		AiActionProvider ai = new AiActionProvider(minMaxAlgorithm);
		battleDomain.getFactionActionProviders().add(ai);
		return ai;
	}
}
