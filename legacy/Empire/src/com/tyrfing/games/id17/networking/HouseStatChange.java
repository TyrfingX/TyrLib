package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.houses.DiplomatStatModifier;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.MarriageStatModifier;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class HouseStatChange extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7919820325905261390L;

	public static final byte ADD = 0;
	public static final byte REMOVE = 1;
	public static final byte ADD_VARYING = 2;
	public static final byte ADD_DIPLO = 3;
	public static final byte ADD_MARRIAGE = 4;
	
	public final byte action;
	public final String name;
	public final byte stat;
	public final float value;
	public final float timestampStart;
	public final float duration;
	public final short houseAffectedID;
	public final short houseTargetID;
	public float changeSpeed;
	public float limit;
	
	public HouseStatChange(StatModifier sm, int action) {
		this.name = sm.name;
		this.stat = (byte) sm.stat;
		this.value = sm.value;
		this.timestampStart = sm.timestampStart;
		this.duration = sm.duration;
		this.houseAffectedID = (short) sm.affected;
		this.houseTargetID = (short) sm.target;
		
		if (sm instanceof VaryingStatModifier) {
			VaryingStatModifier vm = (VaryingStatModifier) sm;
			changeSpeed = vm.changeSpeed;
			limit = vm.limit;
			
			if (action == ADD) {
				this.action = ADD_VARYING;
			} else {
				this.action = (byte) action;
			}
		} else if (sm instanceof DiplomatStatModifier && action == ADD) {
			this.action = ADD_DIPLO;
		} else if (sm instanceof MarriageStatModifier && action == ADD) {
			this.action = ADD_MARRIAGE;
		} else {
			this.action = (byte) action;
		}
	}
	
	@Override
	public void process(Connection c) {
		House target = World.getInstance().getHouses().get(houseTargetID);
		House affected = World.getInstance().getHouses().get(houseAffectedID);
		if (action == HouseStatChange.ADD) {
			affected.addStatModifier(new StatModifier(name, stat, affected, target, timestampStart, duration, value));
		} else if (action == HouseStatChange.REMOVE) {
			affected.removeStatModfifier(name, target);
		} else if (action == HouseStatChange.ADD_VARYING) {
			affected.addStatModifier(new VaryingStatModifier(name, stat, affected, target, timestampStart, duration, value, changeSpeed, limit));
		} else if (action == HouseStatChange.ADD_DIPLO) {
			affected.addStatModifier(new DiplomatStatModifier(affected, target));
		} else if (action == HouseStatChange.ADD_MARRIAGE) {
			affected.addStatModifier(new MarriageStatModifier(affected, target));
		}
		
		target.updateBorders();
		affected.updateBorders();
	}
	
}
