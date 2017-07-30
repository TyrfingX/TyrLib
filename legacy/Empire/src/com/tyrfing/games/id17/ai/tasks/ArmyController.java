package com.tyrfing.games.id17.ai.tasks;

import java.io.Serializable;

import com.tyrfing.games.id17.ai.ArmyModel;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;

public class ArmyController implements IUpdateable, Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8755131725034105072L;

	public enum STATE {
		RETREATING,
		FIGHTING,
		HEALING,
		PLUNDERING,
		EXPLORING
	}
	
	public final Army army;
	
	private Holding target;
	private STATE state = STATE.HEALING;
	private ArmyModel armyModel;
	private boolean exploration;
	
	public ArmyController(ArmyModel armyModel, Army army) {
		this.army = army;
		this.armyModel = armyModel;
	}
	
	public void act() {
		
		if (state == STATE.HEALING && !exploration) {
			checkReadyForFight();
		} else if (state == STATE.HEALING && exploration) {
			checkReadyForExploration();
		} else {
			if (army.getTotalTroops() < army.getTotalTroopsMax() * 0.25f || (armyModel.model.getHouse().getCountWars() == 0 && !exploration)) {
				state = STATE.RETREATING;
			}
		}
		
		if ((army.isRaised() || enoughTroops()) && !army.isTravelling() && !exploration) {
			Task bestTask = null;
			float value = -Float.MAX_VALUE;
			float powerFactor = army.getTotalTroops() / (armyModel.model.house.getTotalTroops()+1.f);
			
			for (int i = 0; i < armyModel.tasks.size(); ++i) {
				Task task = armyModel.tasks.get(i);
				if (!task.beingServed) {
					float localValue = task.value;
					if ((task.holding.getMainPositionedArmy() != null && task.holding.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null)) {
						localValue *= powerFactor;
					}
					
					if (task.holding instanceof Barony) {
						localValue *= army.getMoral()+0.01f;
					} else {
						localValue /= army.getMoral()+0.01f;
					}
					
					if (localValue > value) {
						value = localValue;
						bestTask = task;
					}
				}
			}
			
			if (bestTask != null) {
				if (value >= 0) {
					bestTask.beingServed = true;
					target = bestTask.holding;
					exploration = bestTask.exploration;
					
					if (bestTask.exploration) {
						armyModel.dealWithExploration(target);
					}
				}
			} 
		}
		
	}
	
	@Override
	public void onUpdate(float time) {
		if (state == STATE.FIGHTING) {
			if (!army.isRaised()) {
				if (army.getHome() != null) {
					army.getHome().raiseArmy();
				}
			} else if (target != null && army.getCurrentHolding() != target && !army.isTravelling() && !army.isFighting() && (target.getMainPositionedArmy() == null || target.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null)) {
				army.moveTo(target);
			} else if (target != null && target.isPillageableByArmy(army) && army.getCurrentHolding() == target && !army.isFighting() && !army.isTravelling()) {
				state = STATE.PLUNDERING;
				army.pillage();
			}
		} else if (state == STATE.RETREATING) {
			if (army.isRaised() && army.getCurrentHolding() == army.getHome()) {
				army.unraise();
				state = STATE.HEALING;
			} else if (!army.isRaised()) {
				state = STATE.HEALING;
			} else if (target != null && army.getCurrentHolding() != target && !army.isTravelling() && !army.isFighting()) {
				if (target.getMainPositionedArmy() == null || target.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null) {
					army.moveTo(target);
				} else {
					Holding[] neighbours =  World.getInstance().getMap().getNeighboursHolding(army.getCurrentHolding());
					if (neighbours.length > 0) {
						Holding rndNeighbour = neighbours[(int)(neighbours.length*Math.random())];
						if (rndNeighbour.getMainPositionedArmy() == null || rndNeighbour.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null) {
							army.moveTo(rndNeighbour);
						}
					}
				}
			}
		} else if (state == STATE.PLUNDERING) {
			if (!army.isPillaging()) {
				state = STATE.FIGHTING;
			}
		} else if (state == STATE.EXPLORING) {
			if (!army.isRaised()) {
				if (army.getHome() != null) {
					army.getHome().raiseArmy();
				}
			} else if (target != null && army.getCurrentHolding() != target && !army.isTravelling() && !army.isFighting()) {
				if (target.getMainPositionedArmy() == null || target.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null) {
					army.moveTo(target);
				} else {
					Holding[] neighbours =  World.getInstance().getMap().getNeighboursHolding(army.getCurrentHolding());
					if (neighbours.length > 0) {
						Holding rndNeighbour = neighbours[(int)(neighbours.length*Math.random())];
						if (rndNeighbour.getMainPositionedArmy() == null || rndNeighbour.getMainPositionedArmy().getOwner().isEnemy(army.getOwner()) != null) {
							army.moveTo(rndNeighbour);
						}
					}
				}
			} else if (army.getCurrentHolding() == target && exploration) {
				exploration = false;
				target = army.getHome();
				state = STATE.RETREATING;
			} 
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	private void checkReadyForFight() {
		if (armyModel.model.getHouse().getCountWars() > 0 && enoughTroops()) {
			state = STATE.FIGHTING;
		} 
	}
	
	public void checkReadyForExploration() {
		if (enoughTroops()) {
			state = STATE.EXPLORING;
		}
	}
	
	private boolean enoughTroops() {
		return army.getTotalTroops() > army.getTotalTroopsMax() * 0.75f;
	}
}
