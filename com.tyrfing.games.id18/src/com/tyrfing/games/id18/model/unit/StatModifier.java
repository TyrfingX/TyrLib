package com.tyrfing.games.id18.model.unit;

import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.tag.Tag;

public class StatModifier extends AModifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1437664452449921354L;
	public static final StatModifier OILED = new StatModifier("Oiled", StatType.SPEED, -50, false, Tag.OIL);
	public static final StatModifier BURNING = new StatModifier("Burning", StatType.HP, -5, true, Tag.FIRE);
	
	public static StatModifier createDamageModifier(int damage) {
		return new StatModifier("Damage", StatType.HP, -damage, true);
	}
	
	public static StatModifier createExhaustionModifier(int exhaustion) {
		return new StatModifier("Exhaustion", StatType.CONCENTRATION, -exhaustion, false);
	}
	
	private StatType stat;
	private int value;
	boolean isAbsolute;
	
	public StatModifier(String name, StatType stat, int value, boolean isAbsolute) {
		super(name);
		this.stat = stat;
		this.value = value;
		this.isAbsolute = isAbsolute;
	}
	
	public StatModifier(String name, StatType stat, int value, boolean isAbsolute, Tag... tags) {
		super(name, tags);
		this.stat = stat;
		this.value = value;
		this.isAbsolute = isAbsolute;
	}
	
	public StatType getStat() {
		return stat;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isAbsolute() {
		return isAbsolute;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatModifier) {
			StatModifier modifier = (StatModifier) obj;
			return getName().equals(modifier.getName());
		}
		return super.equals(obj);
	}
}
