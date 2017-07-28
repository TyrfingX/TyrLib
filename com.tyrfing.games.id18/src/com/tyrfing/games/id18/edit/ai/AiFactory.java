package com.tyrfing.games.id18.edit.ai;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.model.unit.Faction;

public class AiFactory {
	public static final int MAX_DEPTH = 5;
	
	public static final AiFactory INSTANCE = new AiFactory(MAX_DEPTH);
	
	private int maxDepth;
	
	public AiFactory(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public Ai createAi(BattleDomain battleDomain, Faction faction) {
		Ai ai = new Ai(battleDomain, faction, maxDepth);
		battleDomain.getFactionActionProviders().add(ai);
		return ai;
	}
}
