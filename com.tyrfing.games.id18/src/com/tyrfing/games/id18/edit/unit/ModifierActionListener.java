package com.tyrfing.games.id18.edit.unit;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.model.tag.IModifiable;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.edit.ACompoundedActionListener;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;

public class ModifierActionListener extends ACompoundedActionListener {

	private List<IModifierReactionRule> reactionRules;
	
	public ModifierActionListener() {
		reactionRules = new ArrayList<IModifierReactionRule>();
	}
	
	public List<IModifierReactionRule> getReactionRules() {
		return reactionRules;
	}
	
	@Override
	protected void onPreExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction) {

	}

	@Override
	protected void onPostExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction) {
		if (compoundedAction instanceof AddStatModifierAction) {
			AddStatModifierAction addStatModifierAction = (AddStatModifierAction) compoundedAction;
			IModifiable modifiable = addStatModifierAction.getModifiable();
			StatModifier modifier = addStatModifierAction.getModifier();
			
			for (IModifierReactionRule reactionRule : reactionRules) {
				reactionRule.checkAndAppendActions(compoundAction, modifiable, modifier);
			}
		}
	}

}
