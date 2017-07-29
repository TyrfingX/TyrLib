package com.tyrfing.games.id18.model.ai;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;

public class Heuristic {
	
	private Faction faction;
	private Battle battle;
	
	public Heuristic(Battle battle, Faction faction) {
		this.faction = faction;
		this.battle = battle;
	}
	
	public float getEvaluation(int depth) {
		if (battle.areObjectivesAchieved(faction)) {
			return 1.f / depth;
		} 
		
		return 0;
	}
	
	public Faction getFaction() {
		return faction;
	}
}
