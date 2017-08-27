package com.tyrfing.games.id18.edit.unit;

import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.model.game.stats.IModifiable;

public interface IModifierReactionRule {
	public void checkAndAppendActions(CompoundAction compoundAction, IModifiable<StatModifier> modifiable, StatModifier modifier);
}
