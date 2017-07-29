package com.tyrfing.games.id18.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public class Battle implements ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -523143721026487071L;
	private Field field;
	private List<Unit> waitingUnits;
	private List<IObjective> objectives;
	private List<Faction> factions;
	
	public Battle() {
		waitingUnits = new ArrayList<Unit>();
		objectives = new ArrayList<IObjective>();
		factions = new ArrayList<Faction>();
	}
	
	public void setField(Field field) {
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
	
	public List<Unit> getWaitingUnits() {
		return waitingUnits;
	}
	
	public Unit getCurrentUnit() {
		return waitingUnits.get(0);
	}
	
	public List<Faction> getFactions() {
		return factions;
	}
	
	public List<IObjective> getObjectives() {
		return objectives;
	}
	
	public boolean areObjectivesAchieved(Faction faction) {
		List<Faction> allSuperFactions = faction.getAllSuperFactionsInclusive();
		for (IObjective objective : objectives) {
			if (allSuperFactions.contains(objective.getFaction())) {
				if (objective.isAchieved(this)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public boolean isFinished() {
		for (IObjective objective : objectives) {
			if (objective.isAchieved(this)) {
				return true;
			}
		}
		
		return false;
	}
}
