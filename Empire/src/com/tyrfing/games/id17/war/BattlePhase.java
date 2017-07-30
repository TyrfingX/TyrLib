package com.tyrfing.games.id17.war;

public enum BattlePhase {
	BATTLE_CHARGE, BATTLE_VOLLEY_1, BATTLE_BATTLE_1, BATTLE_FLANK_1, BATTLE_VOLLEY_2, BATTLE_BATTLE_2, BATTLE_FLANK_2,
	SIEGE_PREPARE, SIEGE_VOLLEY_2, SIEGE_VOLLEY_1, SIEGE_ASSAULT;
	
	public BattlePhase getNext() {
		int index = this.ordinal();
		BattlePhase[] phases = BattlePhase.values();
		
		if (index < 6) {
			return phases[index+1];
		} else if (index == 6){
			return phases[1];
		} else if (index < phases.length - 1) {
			return phases[index+1];
		} else {
			return phases[7];
		}
	}
	
	public String displayName() {
		switch(this){
		case BATTLE_CHARGE:
			return "Charge";
		case BATTLE_VOLLEY_1:
		case BATTLE_VOLLEY_2:
			return "Volley";
		case BATTLE_BATTLE_1:
		case BATTLE_BATTLE_2:
			return "Melee";
		case BATTLE_FLANK_1:
		case BATTLE_FLANK_2:
			return "Flank";
		case SIEGE_VOLLEY_1:
		case SIEGE_VOLLEY_2:
			return "Volley";
		case SIEGE_PREPARE:
			return "Prepare";
		case SIEGE_ASSAULT:
			return "Assault";
		}
		
		return "";
	}

	public boolean isRangedPhase() {
		return this == BATTLE_VOLLEY_1 || this == BATTLE_VOLLEY_2 || this == SIEGE_VOLLEY_1 || this == SIEGE_VOLLEY_2;
	}

	public boolean isDefPhase() {
		return this == BATTLE_VOLLEY_2 || this == BATTLE_BATTLE_2 || this == BATTLE_FLANK_2 || this == SIEGE_VOLLEY_2;
	}
	
}
