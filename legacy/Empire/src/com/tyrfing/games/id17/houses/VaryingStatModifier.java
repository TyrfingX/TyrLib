package com.tyrfing.games.id17.houses;

import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;

public class VaryingStatModifier extends StatModifier implements IUpdateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7690161022767720437L;
	
	public float changeSpeed;
	public float limit;

	public VaryingStatModifier(String name, int stat, House affected, House target, float duration, float value, float changeSpeed, float limit) {
		this(name, stat, affected, target, World.getInstance().getWorldTime(), duration, value, changeSpeed, limit);
	}
	
	public VaryingStatModifier(String name, int stat, House house, float duration, float value, float changeSpeed, float limit) {
		this(name, stat, house, house, World.getInstance().getWorldTime(), duration, value, changeSpeed, limit);
	}
	
	public VaryingStatModifier(String name, int stat, House affected, House target, float timestampStart, float duration, float value, float changeSpeed, float limit) {
		super(name, stat, affected, target, timestampStart, duration, value);
		
		this.changeSpeed = changeSpeed;
		this.limit = limit;
	}
	
	@Override
	public void onUpdate(float time) {
		
		super.onUpdate(time);
		
		if (	(value >= limit && changeSpeed >= 0)
			||	(value <= limit && changeSpeed <= 0)) {
			value = limit;
		} else {
			float change = time * changeSpeed;
			value += change;
			
			if (affected != target)  {
				World.getInstance().getHouses().get(affected).changeHouseStat(World.getInstance().getHouses().get(target), stat, change);
			} else {
				World.getInstance().getHouses().get(affected).changeReputation(stat, change);
			}
		}
	}

}
