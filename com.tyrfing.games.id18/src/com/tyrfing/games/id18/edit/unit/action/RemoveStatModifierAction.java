package com.tyrfing.games.id18.edit.unit.action;

import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.RemoveAction;
import com.tyrfing.games.tyrlib3.model.game.stats.IModifiable;

public class RemoveStatModifierAction extends RemoveAction<StatModifier> {

	private IModifiable<StatModifier> modifiable;
	private int oldValue;
	
	public RemoveStatModifierAction(IModifiable<StatModifier> modifiable, StatModifier modifier) {
		super(modifiable.getModifiers(), modifier);
		this.modifiable = modifiable;
	}

	@Override
	public void execute() {
		if (modifiable instanceof Unit) {
			Unit unit = (Unit) modifiable;
			
			StatModifier statModifier = (StatModifier) object;
			StatType stat = statModifier.getStat();
			int value = statModifier.getValue();
			oldValue = unit.getStats().getOrDefault(stat, 0);
			
			if (statModifier.isAbsolute()) {
				int newValue = oldValue - value;
				unit.getStats().put(stat, newValue);
			} else {
				float factor = 1 - value / 100.f;
				int newValue = (int) (oldValue * factor);
				unit.getStats().put(stat, newValue);
			}
		}
		
		super.execute();
	}
	
	@Override
	public void undo() {
		if (modifiable instanceof Unit) {
			Unit unit = (Unit) modifiable;
			StatModifier statModifier = (StatModifier) object;
			StatType stat = statModifier.getStat();
			unit.getStats().put(stat, oldValue);
		}
		
		super.undo();
	}
	
	public IModifiable<StatModifier> getModifiable() {
		return modifiable;
	}
	
	public StatModifier getModifier() {
		return (StatModifier) object;
	}
	
	@Override
	public String toString() {
		return "RemoveStatModifierAction";
	}
}
