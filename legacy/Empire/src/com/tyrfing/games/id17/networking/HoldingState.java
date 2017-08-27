package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.buildings.Guild;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class HoldingState extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8702014206423494714L;

	public final short holdingID;
	public final short houseID;
	public final float pillagedTimestamp;
	public final byte[] buildings;
	public byte guildType = -1;
	public short guildTarget = -1;
	public final short occupied;
	public final int[] troops;
	public final byte[] types;
	public final int[] roads;
	
	public HoldingState(Holding holding) {
		houseID = (short) holding.getOwner().id;
		holdingID = holding.getHoldingID();
		if (holding.isPillaged()) {
			pillagedTimestamp = holding.pillageTimestamp;
		} else {
			pillagedTimestamp = -1;
		}
		
		buildings = new byte[Building.TYPE.values().length];
		for (int i = 0; i < buildings.length; ++i) {
			Building b = holding.isBuilt(Building.TYPE.values()[i]);
			if (b == null) {
				buildings[i] = -1;
			} else {
				buildings[i] = (byte) b.getLevel();
				if (b.getType() == Building.TYPE.Guild) {
					Guild g = (Guild) b;
					guildType = (byte) g.type.ordinal();
					if (g.getGuildType().equals(Guild.TYPE.Merchants)) {
						guildTarget = (short) g.target.getIndex();
					}
				} 
			}
		}
		
		if (holding instanceof Barony) {
			Barony barony = (Barony) holding;
			House occupee = barony.getOccupee();
			if (occupee != null) {
				occupied = occupee.id;
			} else {
				occupied = -1;
			}
			
			troops = new int[Army.MAX_REGIMENTS*2];
			types = new byte[Army.MAX_REGIMENTS*2];
			
			for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
				Regiment r = barony.getLevy().getRegiment(i);
				if (r != null) {
					troops[i] = (int) r.maxTroops;
					types[i] = (byte) r.unitType.ordinal();
				} else {
					types[i] = -1;
				}
			}
			
			for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
				Regiment r = barony.getGarrison().getRegiment(i);
				if (r != null) {
					troops[i+Army.MAX_REGIMENTS] = (int) r.maxTroops;
					types[i+Army.MAX_REGIMENTS] = (byte) r.unitType.ordinal();
				} else {
					types[i+Army.MAX_REGIMENTS] = -1;
				}
			}
			
		} else {
			occupied = -1;
			types = null;
			troops = null;
		}
		
		roads = new int[holding.getCountRoads()];
		for (int i = 0; i < roads.length; ++i) {
			roads[i] = holding.getRoadTarget(i);
		}
	}
	
	@Override
	public void process(Connection c) {
		
		List<Holding> holdings = World.getInstance().getHoldings();
		List<House> houses = World.getInstance().getHouses();
		
		Holding h = holdings.get(holdingID);
		House owner = houses.get(houseID);
		
		House.transferHolding(owner, h, false);
		
		if (pillagedTimestamp != -1) {
			h.setPillaged(true);
			h.pillageTimestamp = pillagedTimestamp;
		}
		
		for (int i = 0; i < roads.length; ++i) {
			h.addRoad(World.getInstance().getHolding(roads[i]));
		}
		
		for (int i = 0; i < buildings.length; ++i) {
			if (buildings[i] != -1) {
				Building b = Building.create(Building.TYPE.values()[i], buildings[i]);
				h.addBuilding(b);
				b.applyEffects(h, b.getLevel());
			}
		}
		
		if (guildType != -1) {
			Guild g = (Guild) h.isBuilt(Building.TYPE.Guild);
			if (guildTarget != -1) {
				g.setupGuild(
					Guild.TYPE.values()[guildType], 
					World.getInstance().getBarony(guildTarget), 
					h
				);
			} else {
				g.setupGuild(
					Guild.TYPE.values()[guildType], 
					null, 
					h
				);
			}
		}
		
		if (h instanceof Barony) {
			Barony b = (Barony) h;
			
			if (occupied != -1) {
				b.setOccupied(houses.get(occupied));
			}
			
			for (int i = 0; i < Army.MAX_REGIMENTS; ++i) {
				if (types[i] != -1) {
					Regiment r = b.getLevy().getRegiment(i);
					if (r == null) {
						r = new Regiment(UnitType.values()[types[i]], troops[i], troops[i], i);
						b.getLevy().addRegiment(r);
					} else {
						r.maxTroops = troops[i];
						r.unitType = UnitType.values()[types[i]];
					}
				}
			}
			
			for (int i = Army.MAX_REGIMENTS; i < Army.MAX_REGIMENTS*2; ++i) {
				if (types[i] != -1) {
					Regiment r = b.getGarrison().getRegiment(i-Army.MAX_REGIMENTS);
					if (r == null) {
						r = new Regiment(UnitType.values()[types[i]], troops[i], troops[i], i-Army.MAX_REGIMENTS);
						b.getGarrison().addRegiment(r);
					}  else {
						r.maxTroops = troops[i];
						r.unitType = UnitType.values()[types[i]];
					}
				}
			}
		}
	}
}
