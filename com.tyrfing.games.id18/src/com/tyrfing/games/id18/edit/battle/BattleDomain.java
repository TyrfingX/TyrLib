package com.tyrfing.games.id18.edit.battle;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.Domain;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.resource.Resource;

public class BattleDomain extends Domain implements IUpdateable, IActionRequester {
	private List<AFactionActionProvider> factionActionProviders;
	private boolean isFinished;
	private boolean nextAction;
	
	public BattleDomain(Battle battle) {
		ActionStack actionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		setActionStack(actionStack);
		
		Resource battleResource = new Resource();
		battleResource.setURI("saves");
		battleResource.setName("battle.sav");
		battleResource.getSaveables().add(battle);
		getResources().add(battleResource);
		
		factionActionProviders = new ArrayList<AFactionActionProvider>();
	}
	
	public List<AFactionActionProvider> getFactionActionProviders() {
		return factionActionProviders;
	}
	
	public AFactionActionProvider getFactionActionProvider(Faction faction) {
		for (AFactionActionProvider factionActionProvider : factionActionProviders) {
			if (factionActionProvider.getFaction().equals(faction)) {
				return factionActionProvider;
			}
		}
		
		return null;
	}
	
	public Battle getBattle() {
		return (Battle) getResources().get(0).getSaveables().get(0);
	}
	
	public void startBattle() {
		isFinished = false;
		
		Unit unit = getBattle().getCurrentUnit();
		unit.startTurn();
		
		nextAction();
	}
	
	@Override
	public void onProvideRequest(IAction action) {
		getActionStack().execute(action);
		nextAction = true;
	}
	
	private void nextAction() {
		nextAction = false;
		Unit currentUnit = getBattle().getCurrentUnit();
		Faction faction = currentUnit.getFaction();
		
		AFactionActionProvider actionProvider = getFactionActionProvider(faction);
		actionProvider.requestAction(this);
	}
	
	public void finishBattle() {
		isFinished = true;
	}
	
	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public void onUpdate(float time) {
		getActionStack().onUpdate(time);
		
		if (nextAction) {
			nextAction();
		}
		
		if (getBattle().isFinished()) {
			finishBattle();
		}
	}
}
