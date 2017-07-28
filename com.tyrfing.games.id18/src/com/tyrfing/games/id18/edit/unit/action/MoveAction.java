package com.tyrfing.games.id18.edit.unit.action;

import java.util.List;

import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.AAction;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class MoveAction extends AAction {

	private Unit unit;
	
	private Vector2I targetPosition;
	
	private Vector2I previousPosition;
	private Vector2I previousOrientation;
	
	private boolean reduceRemainingMove;
	
	public MoveAction(Unit unit, Vector2I targetPosition, boolean reduceRemainingMove) {
		this.unit = unit;
		this.targetPosition = targetPosition;
		this.reduceRemainingMove = reduceRemainingMove;
	}
	
	@Override
	public void execute() {
		previousPosition = unit.getFieldPosition();
		previousOrientation = unit.getFieldOrientation();
		
		Vector2I distanceVector = previousPosition.vectorTo(targetPosition);
		Vector2I targetOrientation = distanceVector.mapToUnitAxis();
		
		unit.setFieldPosition(targetPosition);
		unit.setFieldOrientation(targetOrientation);
		
		if (reduceRemainingMove) {
			int newValue = unit.getStats().get(StatType.REMAINING_MOVE) - distanceVector.abs();
			unit.getStats().put(StatType.REMAINING_MOVE, newValue);
		}
	}
	
	@Override
	public boolean canExecute() {
		if (reduceRemainingMove) {
			Vector2I distanceVector = unit.getFieldPosition().vectorTo(targetPosition);
			if (unit.getStats().get(StatType.REMAINING_MOVE) < distanceVector.abs()) {
				return false;
			}
		}
		
		Field field = unit.getDeployedField();
		
		if (!field.inBounds(targetPosition))  {
			return false;
		}
		
		if (field.getObjectAt(targetPosition) != null) {
			return false;
		}
		
		return super.canExecute();
	}

	@Override
	public void undo() {
		unit.setFieldPosition(previousPosition);
		unit.setFieldOrientation(previousOrientation);
		
		if (reduceRemainingMove) {
			Vector2I distanceVector = previousPosition.vectorTo(targetPosition);
			int newValue = unit.getStats().get(StatType.REMAINING_MOVE) + distanceVector.abs();
			unit.getStats().put(StatType.REMAINING_MOVE, newValue);
		}
	}
	
	public Vector2I getTargetPosition() {
		return targetPosition;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public static CompoundAction createMovePathAction(Unit unit, List<Vector2I> path, boolean reduceRemainingMove) {
		CompoundAction movePathAction = new CompoundAction();
		for (Vector2I point : path) {
			movePathAction.appendAction(new MoveAction(unit, point, reduceRemainingMove));
		}
		return movePathAction;
	}
	
	@Override
	public String toString() {
		return "MoveAction";
	}
}
