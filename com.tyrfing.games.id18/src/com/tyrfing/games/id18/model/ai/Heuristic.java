package com.tyrfing.games.id18.model.ai;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;

public class Heuristic {
	
	private Faction faction;
	private Battle battle;
	
	public Heuristic(Battle battle, Faction faction) {
		this.faction = faction;
		this.battle = battle;
	}
	
	public float getEvaluation(int depth) {
		if (battle.areObjectivesAchieved(faction)) {
			// This is a winning state, we should definitely take it,
			// only consideration is that we should take it 
			return 1 + 1f / depth;
		} 
		
		float evaluation = 0;
		float maxEvaluation = 0;
		
		for (Unit unit : battle.getWaitingUnits()) {
			int hp = unit.getStats().get(StatType.HP);
			if (unit.getFaction().equals(faction)) {
				evaluation += hp;
			} 
			maxEvaluation += hp;
		}
		
		return evaluation / maxEvaluation;
	}
	
	public Faction getFaction() {
		return faction;
	}
}
