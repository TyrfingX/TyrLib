package com.tyrfing.games.id18.test.edit.surface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.surface.Surface;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class SurfaceActionListenerTest {

	public static final Vector2I INITIAL_POS = new Vector2I(4, 5);
	public static final Vector2I SURFACE_POS = new Vector2I(5, 5);
	public static final List<Vector2I> PATH = Arrays.asList(new Vector2I(5,5), new Vector2I(5, 6));
	
	private ActionStack actionStack;
	private Unit unit;
	private Surface surface;
	
	@Before
	public void setup() {
		Field field = new Field(new Vector2I(10, 10));
		BattleDomain battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		surface = new Surface(field, new Vector2I(5, 5), StatModifier.OILED);
		field.getObjects().add(surface);
		
		actionStack = battleDomain.getActionStack();
		
		unit = new Unit();
		unit.deploy(battleDomain.getBattle(), INITIAL_POS, Vector2I.UNIT_X);
	}
	
	@Test
	public void testOnPostExecuteCompoundedAction() {
		IAction action = MoveAction.createMovePathAction(unit, PATH, false);
		actionStack.execute(action);
		
		assertEquals("Unit has obtained one modifier", 1, unit.getModifiers().size());
		assertTrue("Unit has obtained on contact modifier from the surface", unit.getModifiers().contains(StatModifier.OILED));
	}
	
	@Test
	public void testSurfaceTransformation() {
		CompoundAction compoundAction = new CompoundAction();
		IAction action = new AddStatModifierAction(surface, StatModifier.BURNING);
		compoundAction.appendAction(action);	
		
		actionStack.execute(compoundAction);
		
		assertTrue("Surface is now a burning surface", surface.getModifiers().contains(StatModifier.BURNING));
		assertFalse("Surface is no longer an oil surface", surface.getModifiers().contains(StatModifier.OILED));
	}
}
