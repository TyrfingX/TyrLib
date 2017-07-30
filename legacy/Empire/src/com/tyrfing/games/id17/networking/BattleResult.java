package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class BattleResult extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6393527998431551119L;

	public final short winner;
	
	public BattleResult(int winner) {
		this.winner = (short) winner;
	}
	
	@Override
	public void process(Connection c) {
		if (winner < World.getInstance().armies.size()) {
			Army army = World.getInstance().armies.get(winner);
			if (army.getBattle() != null) {
				army.getBattle().win(army, army.getBattle().getOther(army));
			}
		}
	}
	
}
