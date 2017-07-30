package com.tyrfing.games.id17.houses;

import java.io.Serializable;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;

public class StatModifier implements IUpdateable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7854164555718477759L;
	
	public final int stat;
	public final int affected;
	public final int target;
	public final float duration;
	public float value;
	public final String name;
	
	public float timestampStart;
	
	protected boolean finished = false;
	
	/**
	 * 
	 * @param name Name of the stat modifier
	 * @param stat Stat to be affected 
	 * @param affected Affected house
	 * @param target Other house related to the stat
	 * @param duration Duration of the modification time
	 * @param value Value of the modifier
	 */
	
	public StatModifier(String name, int stat, House affected, House target, float duration, float value) {
		this(name, stat, affected, target, World.getInstance().getWorldTime(), duration, value);
		if (affected == target) {
			throw new RuntimeException("Error in StatModifier::StatModifier invalid affected and target values");
		}
	}
	
	public StatModifier(String name, int stat, House house, float duration, float value) {
		this(name, stat, house, house, World.getInstance().getWorldTime(), duration, value);
	}
	
	public StatModifier(String name, int stat, House affected, House target, float timestampStart, float duration, float value) {
		this.name = name;
		this.stat = stat;
		this.affected = affected.id;
		this.target = target.id;
		this.duration = duration;
		this.value = value;
		this.timestampStart = timestampStart;
	}
	
	public void apply() {
		if (affected != target) {
			World.getInstance().getHouses().get(affected).changeHouseStat(World.getInstance().getHouses().get(target), stat, value);
		} else {
			World.getInstance().getHouses().get(affected).changeReputation(stat, value);
		}
	}
	
	public void unapply() {
		if (affected != target)  {
			World.getInstance().getHouses().get(affected).changeHouseStat(World.getInstance().getHouses().get(target), stat, -value);
		} else {
			World.getInstance().getHouses().get(affected).changeReputation(stat, -value);
		}
	}

	@Override
	public void onUpdate(float time) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (World.getInstance().getWorldTime() >= timestampStart + duration && duration != -1) {
				World.getInstance().getHouses().get(affected).removeStatModfifier(name, World.getInstance().getHouses().get(target));
				finished = true;
			}
		}
	}

	public float getDuration() {
		return World.getInstance().getWorldTime() - timestampStart;
	}
	
	@Override
	public boolean isFinished() {
		return finished;
	}
}
