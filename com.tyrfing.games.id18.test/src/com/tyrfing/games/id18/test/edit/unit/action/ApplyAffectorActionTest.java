package com.tyrfing.games.id18.test.edit.unit.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.unit.action.ApplyAffectorAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.tag.Tag;
import com.tyrfing.games.id18.model.unit.Affector;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class ApplyAffectorActionTest {

	public static final Vector2I TARGET_POS = new Vector2I(5,5);
	public static final Vector2I CASTER_POS = new Vector2I(3,5);
	public static final int INITIAL_ACTIONS = 5;
	
	private ActionStack actionStack;
	private Unit caster;
	private Unit receiver;
	private Field field;
	private Battle battle;
	
	@Before
	public void setup() {
		field = new Field(new Vector2I(10,10));
		BattleDomain battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		battle = battleDomain.getBattle();
		actionStack = battleDomain.getActionStack();
		
		caster = new Unit();
		receiver = new Unit();
		
		caster.getStats().put(StatType.REMAINING_ACTIONS, INITIAL_ACTIONS);
		
		caster.deploy(battle, CASTER_POS, Vector2I.UNIT_X);
		receiver.deploy(battle, TARGET_POS, Vector2I.NEGATIVE_UNIT_X);
	}
	
	@Test
	public void testExecute() {
		Affector affector = new Affector("Simple Affector");
		
		StatModifier exhaustion = StatModifier.createExhaustionModifier(-10);
		StatModifier damage = StatModifier.createDamageModifier(-50);
		
		affector.getCostModifiers().add(exhaustion);
		affector.getEffectModifiers().add(damage);
		
		ApplyAffectorAction useArteAction = new ApplyAffectorAction(caster, affector, TARGET_POS, true);
		actionStack.execute(useArteAction);
		
		assertTrue("Receiver has received damage", receiver.getModifiers().contains(damage));
		assertTrue("Caster has received exhaustion", caster.getModifiers().contains(exhaustion));	
		assertEquals("Caster has lost an action", INITIAL_ACTIONS - 1, caster.getStats().get(StatType.REMAINING_ACTIONS).intValue());	
		
		actionStack.undo();
		
		assertTrue("Receiver has no longer any damage", receiver.getModifiers().isEmpty());
		assertTrue("Caster has no longer any exhaustion", caster.getModifiers().isEmpty());	
		assertEquals("Caster has regained an action", INITIAL_ACTIONS, caster.getStats().get(StatType.REMAINING_ACTIONS).intValue());
	}
	
	
	@Test
	public void testCanExecute() {
		Affector affector = new Affector("Simple Affector");
		ApplyAffectorAction useArteAction = new ApplyAffectorAction(caster, affector, TARGET_POS, true);
		
		assertTrue("Action can be executed", useArteAction.canExecute());
		caster.getStats().put(StatType.REMAINING_ACTIONS, 0);
		assertFalse("Action cannot be executed", useArteAction.canExecute());
	}
	
	@Test
	public void testExecuteChainReaction() {
		receiver.getModifiers().add(StatModifier.OILED);
		
		Affector affector = new Arte("Fire Affector");
		
		StatModifier damage = StatModifier.createDamageModifier(-20);
		damage.getTags().add(Tag.FIRE);
		affector.getEffectModifiers().add(damage);
		
		ApplyAffectorAction useArteAction = new ApplyAffectorAction(caster, affector, TARGET_POS, false);
		actionStack.execute(useArteAction);
		
		assertTrue("Receiver has received damage", receiver.getModifiers().contains(damage));	
		assertFalse("Receiver has lost the oiled modifier", receiver.getModifiers().contains(StatModifier.OILED));
		assertTrue("Receiver has started burning", receiver.getModifiers().contains(StatModifier.BURNING));	
		
		actionStack.undo();
		
		assertEquals("Receiver has only 1 modifier", 1, receiver.getModifiers().size());
		assertTrue("Receiver has regained the oiled modifier", receiver.getModifiers().contains(StatModifier.OILED));
	}
	
	@Test
	public void testCanExecuteRange() {
		Affector affector = new Affector("Range Affector");
		affector.setMaxRange(4);
		affector.setMinRange(2);
		
		ApplyAffectorAction useArteActionInRange = new ApplyAffectorAction(caster, affector, TARGET_POS, false);
		
		assertTrue("Target is in range, can execute", useArteActionInRange.canExecute());
		
		final Vector2I OUTSIDE_MAX_RANGE = new Vector2I(9, 5);
		ApplyAffectorAction useArteActionOutSideMaxRange = new ApplyAffectorAction(caster, affector, OUTSIDE_MAX_RANGE, false);
		
		assertFalse("Target is outside max range, cannot execute", useArteActionOutSideMaxRange.canExecute());
		
		final Vector2I OUTSIDE_MIN_RANGE = new Vector2I(3, 5);
		ApplyAffectorAction useArteActionOutSideMinRange = new ApplyAffectorAction(caster, affector, OUTSIDE_MIN_RANGE, false);
		
		assertFalse("Target is outside min range, cannot execute", useArteActionOutSideMinRange.canExecute());
	}
	
	@Test
	public void testExecuteLineRange() {
		Affector affector = new Affector("Line Arte");
		affector.setLineRange(true);
		
		StatModifier damage = StatModifier.createDamageModifier(-50);
		affector.getEffectModifiers().add(damage);
		
		final Vector2I NOT_IN_LINE_POS = new Vector2I(1, 1);
		ApplyAffectorAction applyAffectorNotInLine = new ApplyAffectorAction(caster, affector, NOT_IN_LINE_POS, false);
		assertFalse("Target is not in line range, cannot execute", applyAffectorNotInLine.canExecute());
		
		final Vector2I BLOCKED_UNIT_POS = new Vector2I(7, 5);
		Unit blockedUnit = new Unit();
		blockedUnit.deploy(battle, BLOCKED_UNIT_POS, Vector2I.UNIT_X);
		
		ApplyAffectorAction applyAffectorBlockedUnit = new ApplyAffectorAction(caster, affector, BLOCKED_UNIT_POS, false);
		assertTrue("Target is in line range, can execute", applyAffectorBlockedUnit.canExecute());
		
		actionStack.execute(applyAffectorBlockedUnit);
		
		assertTrue("First unit in line range received the modifier", receiver.getModifiers().contains(damage));
		assertFalse("Blocked unit in line did not receive the modifier", blockedUnit.getModifiers().contains(damage));
	
		actionStack.undo();
		
		final Vector2I BLOCKING_TILE_POS = new Vector2I(4,5);
		field.getTileGrid().getItem(BLOCKING_TILE_POS).setHeight(1);
		
		ApplyAffectorAction applyAffectorBlockingTile = new ApplyAffectorAction(caster, affector, BLOCKED_UNIT_POS, false);
		actionStack.execute(applyAffectorBlockingTile);
		
		assertFalse("First unit in line range did not receive the modifier", receiver.getModifiers().contains(damage));
	}
	
	@Test
	public void testExecuteAoE() {
		Affector affector = new Affector("AoE Arte");
		Vector2I aoe1 = new Vector2I(-1, 0);
		Vector2I aoe2 = new Vector2I(1, 0);
		affector.getAoe().add(aoe1);
		affector.getAoe().add(aoe2);
		
		StatModifier damage = StatModifier.createDamageModifier(-50);
		affector.getEffectModifiers().add(damage);
		
		Unit receiver2 = new Unit();
		receiver2.deploy(battle, new Vector2I(5, 6), Vector2I.UNIT_X);
		
		ApplyAffectorAction applyAffectorRight = new ApplyAffectorAction(caster, affector, TARGET_POS, false);
		actionStack.execute(applyAffectorRight);
		
		assertTrue("Receiver1 has received the damage", receiver.getModifiers().contains(damage));
		assertTrue("Receiver2 has received the damage", receiver2.getModifiers().contains(damage));
		
		actionStack.undo();
		
		assertTrue("Receiver1 has no damage", receiver.getModifiers().isEmpty());
		assertTrue("Receiver2 has no damage", receiver2.getModifiers().isEmpty());
		
		receiver2.setFieldPosition(new Vector2I(2, 3));
		ApplyAffectorAction applyAffectorDown = new ApplyAffectorAction(caster, affector, new Vector2I(3, 3), false);
		actionStack.execute(applyAffectorDown);
		
		assertTrue("Receiver2 has received the damage", receiver2.getModifiers().contains(damage));
		
		actionStack.undo();
		
		assertTrue("Receiver2 has no damage", receiver2.getModifiers().isEmpty());
		
		receiver2.setFieldPosition(new Vector2I(1, 4));
		ApplyAffectorAction applyAffectorLeft = new ApplyAffectorAction(caster, affector, new Vector2I(1, 5), false);
		actionStack.execute(applyAffectorLeft);
		
		assertTrue("Receiver2 has received the damage", receiver2.getModifiers().contains(damage));
		
		actionStack.undo();
		
		assertTrue("Receiver2 has no damage", receiver2.getModifiers().isEmpty());
		
		receiver2.setFieldPosition(new Vector2I(4, 8));
		ApplyAffectorAction applyAffectorUp = new ApplyAffectorAction(caster, affector, new Vector2I(3, 8), false);
		actionStack.execute(applyAffectorUp);
		
		assertTrue("Receiver2 has received the damage", receiver2.getModifiers().contains(damage));
		
		actionStack.undo();
		
		assertTrue("Receiver2 has no damage", receiver2.getModifiers().isEmpty());
	}
	
	@Test
	public void testExecuteMoveToTarget() {
		Affector affector = new Affector("Move Affector");
		affector.setMoveToTarget(true);
	
		ApplyAffectorAction applyAffectorAction = new ApplyAffectorAction(caster, affector, TARGET_POS, false);
		actionStack.execute(applyAffectorAction);
		
		final Vector2I NEW_CASTER_POS = new Vector2I(4, 5);
		assertEquals("Caster has correct position", NEW_CASTER_POS, caster.getFieldPosition());
		assertEquals("Caster has correct orientation", Vector2I.UNIT_X, caster.getFieldOrientation());
	}
}
