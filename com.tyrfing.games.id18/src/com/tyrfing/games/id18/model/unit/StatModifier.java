package com.tyrfing.games.id18.model.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tyrfing.games.id18.model.tag.Tag;
import com.tyrfing.games.tyrlib3.model.game.stats.AModifier;

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
	private List<Tag> tags;
	
	public StatModifier(String name, StatType stat, int value, boolean isAbsolute, Tag... tags) {
		super(name);
		this.stat = stat;
		this.value = value;
		this.isAbsolute = isAbsolute;
		this.tags = new ArrayList<Tag>(Arrays.asList(tags));
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
	
	public List<Tag> getTags() {
		return tags;
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
