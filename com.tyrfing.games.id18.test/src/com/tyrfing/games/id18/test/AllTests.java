package com.tyrfing.games.id18.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.test.edit.ai.AiTest;
import com.tyrfing.games.id18.test.edit.battle.BattleDomainTest;
import com.tyrfing.games.id18.test.edit.battle.action.DefeatUnitActionTest;
import com.tyrfing.games.id18.test.edit.battle.action.DeployActionTest;
import com.tyrfing.games.id18.test.edit.battle.action.EndTurnActionTest;
import com.tyrfing.games.id18.test.edit.surface.SurfaceActionListenerTest;
import com.tyrfing.games.id18.test.edit.unit.action.AddStatModifierActionTest;
import com.tyrfing.games.id18.test.edit.unit.action.ApplyAffectorActionTest;
import com.tyrfing.games.id18.test.edit.unit.action.MoveActionTest;
import com.tyrfing.games.id18.test.edit.unit.action.RemoveStatModifierActionTest;

@RunWith(Suite.class)
@SuiteClasses({
	MoveActionTest.class,
	AddStatModifierActionTest.class,
	RemoveStatModifierActionTest.class,
	SurfaceActionListenerTest.class,
	ApplyAffectorActionTest.class,
	DeployActionTest.class,
	EndTurnActionTest.class,
	AiTest.class,
	DefeatUnitActionTest.class,
	BattleDomainTest.class,
	NetworkActionProvider.class
})
public class AllTests {

}
