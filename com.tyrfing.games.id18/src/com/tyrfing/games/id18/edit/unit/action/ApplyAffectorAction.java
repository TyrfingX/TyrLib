package com.tyrfing.games.id18.edit.unit.action;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.field.IFieldObject;
import com.tyrfing.games.id18.model.tag.AModifier;
import com.tyrfing.games.id18.model.unit.Affector;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.math.Vector2I;
import com.tyrfing.games.tyrlib3.util.CollectionsHelper;

public class ApplyAffectorAction extends CompoundAction {
	
	private Unit unit;
	private Affector affector;
	private Vector2I target;
	private Field field;
	private boolean reduceRemainingActions;
	
	public ApplyAffectorAction(Unit unit, Affector affector, Vector2I target, boolean reduceRemainingActions) {
		this.unit = unit;
		this.affector = affector;
		this.target = target;
		this.reduceRemainingActions = reduceRemainingActions;
		
		field = unit.getDeployedField();
		
		for (AModifier modifier : affector.getCostModifiers()) {
			AddStatModifierAction receiveStatModifierAction = new AddStatModifierAction(unit, modifier);
			getActions().add(receiveStatModifierAction);
		}
		
		if (affector.isMoveToTarget()) {
			Vector2I posBeforeTarget = getPosBeforeTarget();
			MoveAction moveAction = new MoveAction(unit, posBeforeTarget, false);
			getActions().add(moveAction);
		}
		
		List<IFieldObject> receivers = getReceivers();
		
		for (IFieldObject receiver : receivers) {
			if (receiver instanceof Unit) {
				Unit receiverUnit = (Unit) receiver;
				for (AModifier modifier : affector.getEffectModifiers()) {
					AddStatModifierAction receiveStatModifierAction = new AddStatModifierAction(receiverUnit, modifier);
					getActions().add(receiveStatModifierAction);
				}
			}
		}
	}
	
	@Override
	public void execute() {
		if (reduceRemainingActions) {
			unit.getStats().put(StatType.REMAINING_ACTIONS, unit.getStats().get(StatType.REMAINING_ACTIONS) - 1);
		}
		
		super.execute();
	}
	
	@Override
	public void undo() {
		if (reduceRemainingActions) {
			unit.getStats().put(StatType.REMAINING_ACTIONS, unit.getStats().get(StatType.REMAINING_ACTIONS) + 1);
		}
		
		super.undo();
	}
	
	private List<IFieldObject> getReceivers() {
		List<IFieldObject> receivers = new ArrayList<IFieldObject>();
		
		Vector2I castDirection = getCastDistance();
		
		if (affector.isLineRange()) {
			castDirection.normalize();
			
			Vector2I fieldPosition = unit.getFieldPosition();
			Vector2I position = fieldPosition.add(castDirection);
			
			IFieldObject receiver = field.getFirstObjectInLine(position, castDirection);
			CollectionsHelper.addIfNotNull(receivers, receiver);
		} else {
			Vector2I axisAlignedCastDirection = castDirection.mapToUnitAxis();
			
			for (Vector2I aoeTarget : affector.getAoe()) {
				Vector2I fieldAoeTarget = rotateAoeTarget(aoeTarget, axisAlignedCastDirection);
				Vector2I.add(fieldAoeTarget, target, fieldAoeTarget);
				IFieldObject receiver = field.getObjectAt(fieldAoeTarget);
				CollectionsHelper.addIfNotNull(receivers, receiver);
			}
		}
		
		return receivers;
	}
	
	private Vector2I rotateAoeTarget(Vector2I aoeTarget, Vector2I direction) {
		int rotatedX = Math.abs(direction.y) * aoeTarget.x + direction.x * aoeTarget.y;
		int rotatedY = -direction.x * aoeTarget.x + direction.y * aoeTarget.y;
			
		return new Vector2I(rotatedX, rotatedY);
	}
	
	private Vector2I getCastDistance() {
		Vector2I fieldPosition = unit.getFieldPosition();
		Vector2I castDistance = fieldPosition.vectorTo(target);
		return castDistance;
	}
	
	private Vector2I getPosBeforeTarget() {
		Vector2I posBeforeTarget = getCastDistance();
		if (posBeforeTarget.x < 0) {
			posBeforeTarget.x++;
		}
		
		if (posBeforeTarget.x > 0) {
			posBeforeTarget.x--;
		}
		
		if (posBeforeTarget.y > 0) {
			posBeforeTarget.y--;
		}
		
		if (posBeforeTarget.y < 0) {
			posBeforeTarget.y++;
		}
		
		Vector2I.add(posBeforeTarget, unit.getFieldPosition(), posBeforeTarget);
		
		return posBeforeTarget;
	}
	
	@Override
	public boolean canExecute() {
		if (reduceRemainingActions) {
			if (unit.getStats().get(StatType.REMAINING_ACTIONS) == 0) {
				return false;
			}
		}
		
		Vector2I castDistance = getCastDistance();
		
		if (affector.getMaxRange() > 0) {
			if (castDistance.abs() > affector.getMaxRange()) {
				return false;
			}
		}
		
		if (affector.getMinRange() > 0) {
			if (castDistance.abs() < affector.getMinRange()) {
				return false;
			}
		}
		
		if (affector.isLineRange()) {
			if (castDistance.x != 0 && castDistance.y != 0) {
				return false;
			}
		}
		
		return super.canExecute();
	}
	
	@Override
	public String toString() {
		return "ApplyAffectorAction";
	}

	public Unit getUnit() {
		return unit;
	}

	public Vector2I getTarget() {
		return target;
	}

	public boolean isReduceRemainingActions() {
		return reduceRemainingActions;
	}

	public Affector getAffector() {
		return affector;
	}
}
