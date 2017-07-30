package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class NewWar extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5747199972743140981L;
	
	public final short goalHolding;
	public final short forHouse;
	public final byte warMode;
	
	public final float totalProgress;
	public final float occupyProgress;
	public final float battleProgress;
	public final float pillageProgress;
	
	public final short[] attackers;
	public final short[] defenders;
	public final short[] occupied;
	
	public final WarJustification justification;
	
	public NewWar(War war) {
		short forHouseID = -1;
		if (war.goal.forHouse != null) {
			forHouseID = (short) war.goal.forHouse.id;
		}
		
		attackers = new short[war.attackers.size()];
		defenders = new short[war.defenders.size()];
		occupied = new short[war.occupied.size()];
		
		for (int i = 0; i < war.attackers.size(); ++i) {
			attackers[i] = (short) war.attackers.get(i).id;
		}
		
		for (int i = 0; i < war.defenders.size(); ++i) {
			defenders[i] = (short) war.defenders.get(i).id;
		}
		
		for (int i = 0; i < war.occupied.size(); ++i) {
			occupied[i] = (short) war.occupied.get(i).getIndex();
		}
		
		this.goalHolding = war.goal.getGoalHoldingID();
		this.forHouse = forHouseID;
		this.warMode =  (byte) war.goal.warMode;
		
		this.totalProgress = war.totalProgress;
		this.battleProgress = war.battleProgress;
		this.occupyProgress = war.occupyProgress;
		this.pillageProgress = war.pillageProgress;
		
		this.justification = war.justification;
	}
	
	public String toString() {
		return "New War: " + attackers[0] + "," + defenders[0];
	}
	
	@Override
	public void process(Connection c) {
		
		List<House> houses = World.getInstance().getHouses();
		
		if (		houses.size() > attackers[0]
				&&	houses.size() > defenders[0]) {
			
			War war = new War(	houses.get(attackers[0]), 
								houses.get(defenders[0]), 
								new WarGoal(goalHolding == -1 ? null : World.getInstance().getHolding(goalHolding), 
											forHouse == -1 ? null : houses.get(forHouse), warMode), 
								justification);
			for (short i = 0; i < occupied.length; ++i) {
				war.addOccupied(World.getInstance().getBarony(occupied[i]));
			}
			
			for (int i = 1; i < attackers.length; ++i) {
				war.addAttackerAlly(houses.get(attackers[i]));
			}
			
			for (int i = 1; i < defenders.length; ++i) {
				war.addDefenderAlly(houses.get(defenders[i]));
			}
			
			war.totalProgress = this.totalProgress;
			war.battleProgress = this.battleProgress;
			war.occupyProgress = this.occupyProgress;
			war.pillageProgress = this.pillageProgress;
		}
	}
	
}
