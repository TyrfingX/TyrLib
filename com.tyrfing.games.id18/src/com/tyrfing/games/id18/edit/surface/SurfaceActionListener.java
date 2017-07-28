package com.tyrfing.games.id18.edit.surface;

import java.util.List;

import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.field.IFieldObject;
import com.tyrfing.games.id18.model.surface.Surface;
import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ACompoundedActionListener;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;

public class SurfaceActionListener extends ACompoundedActionListener {
	private Field field;
	
	public SurfaceActionListener(Field field) {
		this.field = field;
	}

	@Override
	protected void onPreExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction) {

	}

	@Override
	protected void onPostExecuteCompoundedAction(CompoundAction compoundAction, IAction compoundedAction) {
		if (compoundedAction instanceof MoveAction) {
			MoveAction moveAction = (MoveAction) compoundedAction;
			for (IFieldObject object : field.getObjects()) {
				if (object instanceof Surface) {
					Surface surface = (Surface) object;
					if (moveAction.getTargetPosition().equals(surface.getFieldPosition())) {
						Unit unit = moveAction.getUnit();
						IAction onContactReaction = createOnContactReaction(surface, unit);
						compoundAction.appendCurrentlyExecutingAction(onContactReaction);
					}
				}
			}
		}
	}
	
	private IAction createOnContactReaction(Surface surface, Unit unit) {
		CompoundAction compoundAction = new CompoundAction();
		List<AModifier> modifiers = surface.getModifiers();
		for (AModifier modifier : modifiers) {
			AddStatModifierAction modifierAction = new AddStatModifierAction(unit, modifier);
			compoundAction.appendCurrentlyExecutingAction(modifierAction);
		}
		return compoundAction;
	}
}
