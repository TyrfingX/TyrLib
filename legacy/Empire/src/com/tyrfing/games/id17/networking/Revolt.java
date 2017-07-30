package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.RebelController;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.RebelArmy;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class Revolt extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2001253983597823934L;

	public final int revoltees;
	public final short houseID;
	
	public Revolt(int revoltees, short houseID)  {
		this.revoltees = revoltees;
		this.houseID = houseID;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		RebelArmy army = UnrestSource.createRebelArmy(null, 0, revoltees);
		House rebelFaction = new House(House.REBEL_FACTION_NAME, new RebelController(new BehaviorModel(), army), houseID);
		rebelFaction.setIsNPCFaction(true);
		houses.add(rebelFaction);
		army.setOwner(rebelFaction);
	}
	
}
