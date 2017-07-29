package com.tyrfing.games.id18.edit.battle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.edit.network.ActionSerializer;
import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.Domain;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.model.game.IUpdateable;
import com.tyrfing.games.tyrlib3.model.networking.Connection;
import com.tyrfing.games.tyrlib3.model.resource.Resource;

public class BattleDomain extends Domain implements IUpdateable, IActionRequester {
	private List<AFactionActionProvider> factionActionProviders;
	private boolean isFinished;
	private boolean nextAction;
	private ActionSerializer actionSerializer;
	
	public BattleDomain(Battle battle) {
		ActionStack actionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		setActionStack(actionStack);
		
		Resource battleResource = new Resource();
		battleResource.setURI("saves");
		battleResource.setName("battle.sav");
		battleResource.getSaveables().add(battle);
		getResources().add(battleResource);
		
		actionSerializer = new ActionSerializer(battle);
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
		if (action.canExecute()) {
			broadcastAction(action);
			getActionStack().execute(action);
			nextAction = true;
		}
	}
	
	private void broadcastAction(IAction action) {
		Serializable serializable = actionSerializer.toNetworkMessage(action);
		Faction currentFaction = getBattle().getCurrentUnit().getFaction();
		for (AFactionActionProvider factionActionProvider : getFactionActionProviders()) {
			if (factionActionProvider instanceof NetworkActionProvider) {
				NetworkActionProvider networkActionProvider = (NetworkActionProvider) factionActionProvider;
				if (!currentFaction.equals(factionActionProvider.getFaction())) {
					Connection connection = networkActionProvider.getConnection();
					if (connection.getNetwork().isHost()) {
						connection.send(serializable);
					}
				}
			}
		}
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
