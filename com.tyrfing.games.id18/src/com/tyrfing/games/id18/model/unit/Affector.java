package com.tyrfing.games.id18.model.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.tyrfing.games.tyrlib3.model.IUUID;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public class Affector implements ISaveable, IUUID {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2131258366359575132L;

	private String name;
	
	private List<StatModifier> effectModifiers;
	private List<StatModifier> costModifiers;
	
	private int maxRange;
	private int minRange;
	
	private List<Vector2I> aoe;
	
	private boolean isMoveToTarget;
	private boolean isLineRange;
	
	private UUID uuid;
	
	public Affector(String name) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		
		effectModifiers = new ArrayList<StatModifier>();
		costModifiers = new ArrayList<StatModifier>();
		aoe = new ArrayList<Vector2I>();
		aoe.add(new Vector2I());
	}
	
	public String getName() {
		return name;
	}
	
	public List<StatModifier> getEffectModifiers() {
		return effectModifiers;
	}
	
	public List<StatModifier> getCostModifiers() {
		return costModifiers;
	}
	
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}
	
	public int getMaxRange() {
		return maxRange;
	}
	
	public void setMinRange(int minRange) {
		this.minRange = minRange;
	}
	
	public int getMinRange() {
		return minRange;
	}
	
	public List<Vector2I> getAoe() {
		return aoe;
	}
	
	public boolean isMoveToTarget() {
		return isMoveToTarget;
	}
	
	public void setMoveToTarget(boolean isMoveToTarget) {
		this.isMoveToTarget = isMoveToTarget;
	}
	
	public boolean isLineRange() {
		return isLineRange;
	}
	
	public void setLineRange(boolean isLineRange) {
		this.isLineRange = isLineRange;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}
}
