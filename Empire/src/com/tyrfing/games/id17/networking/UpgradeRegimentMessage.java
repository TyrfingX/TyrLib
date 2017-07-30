package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.UpgradeRegimentProject;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class UpgradeRegimentMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -391940772974583532L;
	
	public final short holdingID;
	public final byte regimentID;
	public final byte unitType;
	public final boolean levy;
	
	public UpgradeRegimentMessage(int holdingID, int regimentID, int unitType, boolean levy) {
		this.holdingID = (short) holdingID;
		this.regimentID = (byte) regimentID;
		this.unitType = (byte) unitType;
		this.levy = levy;
	}
	
	@Override
	public void process(Connection c) {
		List<Holding> holdings = World.getInstance().getHoldings();
		
		Barony b = (Barony) holdings.get(holdingID);
		
		Army army = levy ? b.getLevy() : b.getGarrison();
		UnitType type = UnitType.values()[unitType];
		Regiment regiment = army.getRegiment(regimentID);
		int level = 0;
		if (regiment != null) {
			level = (int) (regiment.maxTroops / 100);
		}
		
		int cost = (int) UnitType.getPrice(type, level);
		int prod = (int) UnitType.getProd(type, level);
		
		if (	!EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()
			||	b.getOwner().getGold() >= cost) {
			if (regiment == null) {
				regiment = new Regiment(type, 0, 0, regimentID);
				army.addRegiment(regiment);
			}  
			
			b.startProject(new UpgradeRegimentProject(prod, regiment, army, cost));
		}
	}
}
