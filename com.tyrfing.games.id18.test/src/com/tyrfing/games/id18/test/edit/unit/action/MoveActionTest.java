package com.tyrfing.games.id18.test.edit.unit.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class MoveActionTest {

	public static final Vector2I INITIAL_POS = new Vector2I(3, 4);
	public static final Vector2I TARGET_POS_1 = new Vector2I(3, 5);
	public static final int TARGET_POS_1_HEIGHT = 1;
	public static final int INITIAL_MOVE = 10;
	
	private Unit unit;
	
	@Before
	public void setup() {
		Field field = new Field(new Vector2I(10,10));
		
		field.getTileGrid().getItem(TARGET_POS_1).setHeight(TARGET_POS_1_HEIGHT);
		
		Battle battle = new Battle();
		battle.setField(field);
		unit = new Unit();
		unit.deploy(battle, INITIAL_POS, Vector2I.NEGATIVE_UNIT_X);
		unit.getStats().put(StatType.REMAINING_MOVE, INITIAL_MOVE);
	}
	
	@Test
	public void testExecute() {

		MoveAction moveAction1 = new MoveAction(unit, TARGET_POS_1, true);
		moveAction1.execute();
		
		assertEquals("Unit has moved to target position", TARGET_POS_1, unit.getFieldPosition());
		assertEquals("Unit now faces in move direction", Vector2I.UNIT_Y, unit.getFieldOrientation());
		assertEquals("Unit has lost a move point", INITIAL_MOVE - TARGET_POS_1_HEIGHT - 1, unit.getStats().get(StatType.REMAINING_MOVE).intValue());
		
		final Vector2I TARGET_POS_2 = new Vector2I(4, 5);
		MoveAction moveAction2 = new MoveAction(unit, TARGET_POS_2, false);
		moveAction2.execute();
		
		assertEquals("Unit has moved to target position", TARGET_POS_2, unit.getFieldPosition());
		assertEquals("Unit now faces in move direction", Vector2I.UNIT_X, unit.getFieldOrientation());
		assertEquals("Unit has not lost a move point", INITIAL_MOVE - TARGET_POS_1_HEIGHT - 1, unit.getStats().get(StatType.REMAINING_MOVE).intValue());
	}
	
	@Test
	public void testCanExecute() {
		MoveAction moveAction1 = new MoveAction(unit, TARGET_POS_1, true);
		
		assertTrue("Move action cann be executed", moveAction1.canExecute());
		unit.getStats().put(StatType.REMAINING_MOVE, 1);
		assertFalse("Move action cannot be executed", moveAction1.canExecute());
		
		final Vector2I TARGET_POS_2 = new Vector2I(20, 20);
		MoveAction moveAction2 = new MoveAction(unit, TARGET_POS_2, true);
		assertFalse("Move action cannot be executed", moveAction2.canExecute());
	}
	
	@Test
	public void testUndo() {
		MoveAction moveAction1 = new MoveAction(unit, TARGET_POS_1, true);
		moveAction1.execute();
		
		final Vector2I TARGET_POS_2 = new Vector2I(4, 5);
		MoveAction moveAction2 = new MoveAction(unit, TARGET_POS_2, false);
		moveAction2.execute();
		
		moveAction2.undo();
		
		assertEquals("Second move action has been reverted", TARGET_POS_1, unit.getFieldPosition());
		assertEquals("Second move action has been reverted", Vector2I.UNIT_Y, unit.getFieldOrientation());
		assertEquals("Second move action has been reverted", INITIAL_MOVE - TARGET_POS_1_HEIGHT - 1, unit.getStats().get(StatType.REMAINING_MOVE).intValue());
		
		moveAction1.undo();

		assertEquals("First move action has been reverted", INITIAL_POS, unit.getFieldPosition());
		assertEquals("First move action has been reverted", Vector2I.NEGATIVE_UNIT_X, unit.getFieldOrientation());
		assertEquals("Second move action has been reverted", INITIAL_MOVE, unit.getStats().get(StatType.REMAINING_MOVE).intValue());
	}
	
	@Test
	public void testCreateMovePathAction() {
		final List<Vector2I> PATH = Arrays.asList(new Vector2I(3, 5), new Vector2I(4, 5));
		CompoundAction movePathAction = MoveAction.createMovePathAction(unit, PATH, false);
		
		movePathAction.execute();
		movePathAction.continueExecute();
		
		assertEquals("Unit has moved to target position", PATH.get(1), unit.getFieldPosition());
		assertEquals("Unit has moved to target position", Vector2I.UNIT_X, unit.getFieldOrientation());
		
		movePathAction.undo();
		
		assertEquals("Unit has again at the initial position", INITIAL_POS, unit.getFieldPosition());
		assertEquals("Unit has moved to target position", Vector2I.NEGATIVE_UNIT_X, unit.getFieldOrientation());
	}

}
