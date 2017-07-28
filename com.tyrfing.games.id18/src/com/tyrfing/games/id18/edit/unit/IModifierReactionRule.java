package com.tyrfing.games.id18.edit.unit;

import com.tyrfing.games.id18.model.tag.IModifiable;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;

public interface IModifierReactionRule {
	public void checkAndAppendActions(CompoundAction compoundAction, IModifiable modifiable, StatModifier modifier);
}
