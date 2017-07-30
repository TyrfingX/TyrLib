package com.tyrfing.games.id17.war;

import java.io.Serializable;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class WarGoal implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2286999498014420186L;
	public final static int CONQUER_HOLDING = 0;
	public final static int INDEPENDENCE = -1;
	public final static int REVOLT = -2;
	public final static int LIBERATION = -3;
	public final static int MARAUDE = -4;
	public final static int NO_REASON = -1000;
	
	public final static float PUSHED_CLAIM_RELATION = 40;
	public final static float PUSHED_CLAIM_DURATION = 50 * World.DAYS_PER_YEAR * World.SEASONS_PER_YEAR;
	
	public final Holding goalHolding;
	public final House forHouse;
	public final int warMode;
	
	public UnrestSource source;
	
	// War for a certain holding
	
	public WarGoal(Holding goalHolding, House house, int warMode) {
		this.goalHolding = goalHolding;
		this.forHouse = house;
		this.warMode = warMode;
	}
	
	public WarGoal(UnrestSource source) {
		this.goalHolding = null;
		this.forHouse = null;
		this.source = source;
		this.warMode = REVOLT;
	}
	
	// War not bound to conquering holdings
	
	public WarGoal() {
		goalHolding = null;
		forHouse = null;
		warMode = CONQUER_HOLDING;
	}
	
	public void enact(House winner, House looser, War war) {
		if (goalHolding != null && forHouse != null && (warMode == CONQUER_HOLDING || warMode == NO_REASON)) {
			House.transferHolding(forHouse, goalHolding, true);
			
			if (forHouse != winner) {
				forHouse.addStatModifier(new StatModifier(	"Pushed Claim", 
															House.RELATION_STAT, 
															forHouse, 
															winner, 
															PUSHED_CLAIM_DURATION, 
															PUSHED_CLAIM_RELATION));
			}
		} else if (warMode == INDEPENDENCE) {
			if (winner == war.defender) {
				war.defender.addSubHouse(looser);				
				winner.updateFamily();
				looser.updateFamily();
			}
		} else if (warMode == REVOLT) {
			
			if (winner == war.attacker) {
				source.comply(war.defender);
			}
		} else if (warMode == LIBERATION) {
			while (looser.getHoldings().size() > 0) {
				House.transferHolding(winner, looser.getHoldings().get(0), true);
			}
		} else if (warMode == MARAUDE) {
			
		}
	}
	
	public String resultsAttackerOnVictory(War war) {
		if (warMode != WarGoal.LIBERATION) {
			if (goalHolding != null && forHouse != null) {
				String msg = "- " + forHouse.getName() + " gains:\n   " +  goalHolding.getLinkedName();
				return msg;	
			}
		} else {
			String msg = "- " + forHouse.getName() + " gains:\n   All of " +  war.defender.getLinkedName() + " holdings.";
			return msg;	
		}
		return "";
	}
	
	public String resultsAttackerOnDefeat() {
		return "";
	}
	
	public String resultsDefenderOnVictory() {
		return "";
	}
	
	public String resultsDefenderOnDefeat(War war) {
		if (warMode != WarGoal.LIBERATION) {
			if (goalHolding != null && forHouse != null) {
				String msg = "- " + goalHolding.getOwner().getLinkedName() + " looses:\n   " +  goalHolding.getLinkedName();
				return msg;	
			} 
		} else {
			String msg = "- " + war.defender.getLinkedName() + " looses: All Holdings\n" + 
						 "- All Vassals transferred.\n" + 
						 "- We have been subjugated.";
			return msg;	
		}
		
		return "";
	}

	public short getGoalHoldingID() {
		if (goalHolding != null) {
			return goalHolding.getHoldingID();
		} else {
			return -1;
		}
	}
	
}
