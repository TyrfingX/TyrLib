package com.tyrfing.games.id17.war;

import java.io.Serializable;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class WarJustification implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8089838763131776378L;
	
	public short claim = -1;
	public short target = -1;
	public short holding = -1;
	private int honor;
	public int goal;
	private String name;
	
	public static final int NO_CLAIM = -100;
	
	public static final WarJustification NO_CLAIM_JUSTIFICATION = new WarJustification(WarGoal.NO_REASON);
	
	public WarJustification() {	
	}
	
	public WarJustification(int goal) {	
		this.goal = goal;
		
		if (goal == WarGoal.NO_REASON) {
			honor = NO_CLAIM;
		}
	}
	
	public WarJustification(Holding holding, House claim) {
		this.holding = holding.getHoldingID();
		if (claim != null) {
			honor = 0;
			this.claim = claim.id;
		} else {
			honor = NO_CLAIM;
		}
	}
	
	public WarJustification(String name, Holding holding, House target, House claim) {
		this.target = target.id;
		if (holding != null) {
			this.holding = holding.getHoldingID();
		}
		this.name = name;
		if (claim != null) {
			honor = 0;
		} else {
			honor = NO_CLAIM;
		}
	}
	
	public int getHonor(House user, House target) {
		int res = honor;
		if (user.getHouseStat(target, House.HAS_MARRIAGE) == 1) {
			res -= 100;
		}
		
		if (user.getHouseStat(target, House.HAS_DEFENSIVE_PACT) == 1) {
			res -= 100;
		}
		
		if (user.getHouseStat(target, House.HAS_TRADE_AGREEMENT) == 1) {
			res -= 100;
		}
		
		return res;
	}
	
	public String toString() {
		
		if (name != null) {
			return name;
		}
		
		if (claim != -1) {
			return "Claim";
		}
		
		if (goal == WarGoal.LIBERATION) {
			return "Liberation";
		}
		
		return "None";
	}
	
	public House getClaim() {
		if (claim == -1) return null;
		return World.getInstance().getHouses().get(claim);
	}
	
	public House getTarget() {
		if (target == -1) return null;
		return World.getInstance().getHouses().get(target);
	}
	
	public Holding getHolding() {
		if (holding == -1) return null;
		return World.getInstance().getHoldings().get(holding);
	}
	
	public boolean isApplyable(Holding holding) {
		return 		(holding == this.getHolding() || this.getHolding() == null) 
				&& 	(getTarget() == null || holding.getOwner() == getTarget() || holding.getOwner().getSupremeOverlord() == getTarget());
	}
}
