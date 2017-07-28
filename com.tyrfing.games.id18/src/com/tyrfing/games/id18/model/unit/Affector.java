package com.tyrfing.games.id18.model.unit;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class Affector {
	private String name;
	
	private List<AModifier> effectModifiers;
	private List<AModifier> costModifiers;
	
	private int maxRange;
	private int minRange;
	
	private List<Vector2I> aoe;
	
	private boolean isMoveToTarget;
	private boolean isLineRange;
	
	public Affector(String name) {
		this.name = name;
		
		effectModifiers = new ArrayList<AModifier>();
		costModifiers = new ArrayList<AModifier>();
		aoe = new ArrayList<Vector2I>();
		aoe.add(new Vector2I());
	}
	
	public String getName() {
		return name;
	}
	
	public List<AModifier> getEffectModifiers() {
		return effectModifiers;
	}
	
	public List<AModifier> getCostModifiers() {
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
}
