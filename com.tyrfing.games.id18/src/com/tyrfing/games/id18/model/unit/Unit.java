package com.tyrfing.games.id18.model.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.field.IFieldObject;
import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.tag.IModifiable;
import com.tyrfing.games.id18.model.tag.Tag;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class Unit implements IFieldObject, IModifiable {

	private Vector2I fieldPosition;
	private Vector2I fieldOrientation;
	
	private Field deployedField;
	private Faction faction;
	
	private List<AModifier> modifiers;
	private Map<StatType, Integer> stats;
	private List<Arte> artes;
	
	public Unit() {
		fieldPosition = new Vector2I();
		fieldOrientation = new Vector2I();
		modifiers = new ArrayList<AModifier>();
		stats = new HashMap<StatType, Integer>();
		artes = new ArrayList<Arte>();
		
		for (StatType statType : StatType.values()) {
			stats.put(statType, 0);
		}
	}
	
	@Override
	public Vector2I getFieldPosition() {
		return fieldPosition;
	}
	
	public void setFieldPosition(Vector2I fieldPosition) {
		this.fieldPosition = fieldPosition;
	}
	
	public Vector2I getFieldOrientation() {
		return fieldOrientation;
	}
	
	public void setFieldOrientation(Vector2I fieldOrientation) {
		this.fieldOrientation = fieldOrientation;
	}
	
	public void setDeployedField(Field field) {
		if (this.deployedField != null) {
			this.deployedField.getObjects().remove(this);
		} 
		
		this.deployedField = field;
		
		if (this.deployedField != null) {
			this.deployedField.getObjects().add(this);
		}
	}
	
	public Field getDeployedField() {
		return deployedField;
	}
	
	public Faction getFaction() {
		return faction;
	}
	
	public void setFaction(Faction faction) {
		this.faction = faction;
	}
	
	public void deploy(Battle battle, Vector2I fieldPosition, Vector2I fieldOrientation) {
		battle.getWaitingUnits().add(this);
		setDeployedField(battle.getField());
		setFieldPosition(fieldPosition);
		setFieldOrientation(fieldOrientation);
	}
	
	@Override
	public List<AModifier> getModifiers() {
		return modifiers;
	}
	
	public boolean hasTag(Tag tag) {
		for (AModifier modifier : modifiers) {
			if (modifier.getTags().contains(tag)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Map<StatType, Integer> getStats() {
		return stats;
	}
	
	public List<Arte> getArtes() {
		return artes;
	}

	public void startTurn() {
		getStats().put(StatType.REMAINING_MOVE, getStats().get(StatType.MOVE));
		getStats().put(StatType.REMAINING_ACTIONS, getStats().get(StatType.ACTIONS));
	}
}
