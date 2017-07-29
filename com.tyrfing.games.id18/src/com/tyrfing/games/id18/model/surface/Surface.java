package com.tyrfing.games.id18.model.surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tyrfing.games.id18.model.field.IFieldObject;
import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.tag.IModifiable;
import com.tyrfing.games.tyrlib3.math.Vector2I;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public class Surface implements IFieldObject, IModifiable, ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1191937640764722831L;
	private Vector2I fieldPosition;
	private List<AModifier> statModifiers;
	
	public Surface(Vector2I fieldPosition, AModifier... modifiers) {
		this.fieldPosition = fieldPosition;
		statModifiers = new ArrayList<AModifier>(Arrays.asList(modifiers));
	}

	@Override
	public Vector2I getFieldPosition() {
		return fieldPosition;
	}

	@Override
	public List<AModifier> getModifiers() {
		return statModifiers;
	}
}
