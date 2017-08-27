package com.tyrfing.games.id18.model.surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tyrfing.games.id18.model.field.IFieldObject;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.model.game.stats.IModifiable;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public class Surface implements IFieldObject, IModifiable<StatModifier>, ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1191937640764722831L;
	private Vector2I fieldPosition;
	private List<StatModifier> statModifiers;
	
	public Surface(Vector2I fieldPosition, StatModifier... modifiers) {
		this.fieldPosition = fieldPosition;
		statModifiers = new ArrayList<StatModifier>(Arrays.asList(modifiers));
	}

	@Override
	public Vector2I getFieldPosition() {
		return fieldPosition;
	}

	@Override
	public List<StatModifier> getModifiers() {
		return statModifiers;
	}
}
