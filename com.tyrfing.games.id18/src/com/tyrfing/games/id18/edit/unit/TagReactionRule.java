package com.tyrfing.games.id18.edit.unit;

import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.edit.unit.action.RemoveStatModifierAction;
import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.tag.IModifiable;
import com.tyrfing.games.id18.model.tag.TagReaction;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;

public class TagReactionRule implements IModifierReactionRule {
	
	private List<TagReaction> tagReactions;
	
	public TagReactionRule() {
		tagReactions = TagReaction.createDefaultTagReactionSystem();
	}
	
	@Override
	public void checkAndAppendActions(CompoundAction compoundAction, IModifiable modifiable, StatModifier modifier) {
		for (TagReaction tagReaction : tagReactions) {
			if (tagReaction.isApplicable(modifiable, modifier)) {
				IAction reactionAction = createReactionAction(modifiable, tagReaction);
				compoundAction.appendCurrentlyExecutingAction(reactionAction);
			}
		}
	}
	
	private IAction createReactionAction(IModifiable modifiable, TagReaction tagReaction) {
		CompoundAction compoundAction = new CompoundAction();
		for (AModifier removeModifier : modifiable.getModifiers()) {
			if (!Collections.disjoint(tagReaction.getRemovedModifiersWithTag(), removeModifier.getTags())) {
				RemoveStatModifierAction removeOiled = new RemoveStatModifierAction(modifiable, removeModifier);
				compoundAction.getActions().add(removeOiled);
			}
		}
		
		for (AModifier addModifier : tagReaction.getAddedModifiers()) {
			AddStatModifierAction receiveBurning = new AddStatModifierAction(modifiable, addModifier);
			compoundAction.getActions().add(receiveBurning);
		}
		return compoundAction;
	}
}
