package com.tyrfing.games.id17.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.tyrfing.games.id17.ai.tasks.ArmyController;
import com.tyrfing.games.id17.ai.tasks.Task;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.category.PeaceCategory;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;

public class ArmyModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1033352131610898220L;
	
	public BehaviorModel model;
	protected List<ArmyController> armyControllers = new ArrayList<ArmyController>();
	protected Set<Barony> relevantBaronies = new HashSet<Barony>();
	protected Set<Barony> explorationTargets = new HashSet<Barony>();
	public List<Task> tasks = new ArrayList<Task>();
	
	public static final float EXPLORATION_PRIO = 100;
	
	public ArmyModel(BehaviorModel model) {
		this.model = model;
	}
	
	
	public void update() {
		
		if (armyControllers.size() > 0) {
			int countArmyControllers = armyControllers.size();
			tasks.clear();
			
			if (model.house.getCountWars() == 0) {
				Iterator<Barony> itr = explorationTargets.iterator();
				while (itr.hasNext()) {
					Barony barony = itr.next();
					Task task = new Task(EXPLORATION_PRIO, barony);
					task.exploration = true;
					tasks.add(task);
				}
			}
			
			Iterator<Barony> itr = relevantBaronies.iterator();
			while (itr.hasNext()) {
				Barony barony = itr.next();
				// Reevaluate conquer and attack targets
				if (barony.getOwner().isEnemy(model.house) != null) {
					if (barony.getOccupee() == null || barony.getOccupee().isEnemy(model.house) != null) {
						if (barony.getMainPositionedArmy() == null || barony.getMainPositionedArmy().getOwner().isEnemy(model.house) != null) {
							Task task = new Task(100 / barony.getGarrison().getMoral(), barony);
							tasks.add(task);
						}
						for (int i = 1, countSubHoldings = barony.getCountSubHoldings(); i < countSubHoldings; ++i) {
							Holding holding = barony.getSubHolding(i);
							if ((holding.getMainPositionedArmy() == null || holding.getMainPositionedArmy().getOwner().isEnemy(model.house) != null) && holding.isPillageableByArmy(armyControllers.get(0).army)) {
								Task task = new Task(75 / barony.getGarrison().getMoral(), holding);
								tasks.add(task);
							}
						}
					}
				} else if (barony.getOwner() == model.house || barony.getOccupee() == model.house) {
					// Reevaluate protection targets
					Task task = new Task(50 / barony.getGarrison().getMoral(), barony);
					tasks.add(task);
				}
				
				Army army = barony.getMainPositionedArmy();
				if (army != null && army.getOwner().isEnemy(model.house) != null) {
					// Reevaluate the threat fields
					float value = 10 * army.getTotalTroops() / (model.house.getTotalTroops()+1.f);
					Task task = new Task(-value, barony);
					tasks.add(task);
					
					Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(barony);
					for (int i = 0; i < neighbours.length; ++i) {
						task = new Task(-value, neighbours[i]);
						tasks.add(task);
					}
				}
			}
				
			
			for (int i = 0; i < countArmyControllers; ++i) {
				Holding current = armyControllers.get(i).army.getCurrentHolding();
				if (current != null) {
					Holding[] neighbours = World.getInstance().getMap().getNeighboursHolding(current);
					for (int j = 0; j < neighbours.length; ++j) {
						boolean noTask = true;
						for (int k = 0, countTasks = tasks.size(); k < countTasks; ++k) {
							if (tasks.get(k).holding == neighbours[j]) {
								noTask = false;
								break;
							}
						}
						if (noTask) {
							Task task = new Task(0, neighbours[j]);
							tasks.add(task);
						}
					}
				}
				
				armyControllers.get(i).act();
			}
			
			int loanSize = Loan.getLoanSize(model.house, model.house);
			if (model.house.getCountWars() > 0 && model.house.getGold() < -loanSize / 2 && Math.random() <= 0.2f) {
				House target = model.getGrantLoanHouse();
				if (target != null) {
					loanSize =  Loan.getLoanSize(model.house, target);
					model.actionModel.addExternalDecision(new Decision(	Diplomacy.getInstance().getAction(Diplomacy.LOANS_ID, 0), 
																		null,
																		new int[] { loanSize },
																		target, 
																		false ));
				}
			}
		
			for (int i = 0; i < model.house.getCountWars(); ++i) {
				War war = model.house.getWar(i);
				if (Math.random() <= 0.1f && war.getProgress(model.house) <= -1f && Diplomacy.getInstance().getAction(Diplomacy.REACE_ID, PeaceCategory.ADMIT_ID).isEnabled(model.house, war.getOther(model.house))) {
					model.actionModel.addExternalDecision(new Decision(	Diplomacy.getInstance().getCategory(Diplomacy.REACE_ID).getAction(PeaceCategory.ADMIT_ID), 
																		null,
																		null, 
																		war.getOther(model.house), 
																		false ));
				} else if (Math.random() <= 0.01f && isWhitePeaceAcceptable(war)) {
					model.actionModel.addExternalDecision(new Decision(	Diplomacy.getInstance().getCategory(1).getAction(1), 
																		null,
																		null,
																		war.getOther(model.house), 
																		false ));
					 
				} else if (Diplomacy.getInstance().getAction(1, 0).isEnabled(model.house, war.getOther(model.house))) {
					model.actionModel.addExternalDecision(new Decision(	Diplomacy.getInstance().getCategory(1).getAction(0), 
																		null,
																		null, 
																		war.getOther(model.house), 
																		false ));
				}
			}
		
		}
		
	}
	
	public void onControlHouse(House house) {
		List<Barony> baronies = house.getBaronies();
		
		if (armyControllers.size() == 0) {
			for (int i = 0; i < baronies.size(); ++i) {
				ArmyController controller = new ArmyController(this, baronies.get(i).getLevy());
				World.getInstance().getUpdater().addItem(controller);
				armyControllers.add(controller);
			}
		} else {
			for (int i = 0; i < baronies.size(); ++i) {
				ArmyController controller = armyControllers.get(i);
				World.getInstance().getUpdater().addItem(controller);
			}
		}
	}
	
	public void informNewBarony(Barony holding) {
		ArmyController controller = new ArmyController(this, holding.getLevy());
		armyControllers.add(controller);
		World.getInstance().getUpdater().addItem(controller);
		relevantBaronies.add(holding);
	}
	
	public void informWarStart(War war) {
		relevantBaronies.addAll(war.getOther(model.house.getSupremeOverlord()).getAllBaronies());
	}
	
	public void informWarEnd(War war) {
		List<Barony> baronies = war.getOther(model.house).getAllBaronies();
		for (int i = 0; i < baronies.size(); ++i) {
			if (!baronies.get(i).getOwner().haveSameOverlordWith(model.house)) {
				//relevantBaronies.remove(baronies.get(i));
			}
		}
	}
	
	public void informAddAlly(House house) {
		relevantBaronies.addAll(house.getAllBaronies());
	}
	
	public void informRemoveAlly(House house) {
		//relevantBaronies.removeAll(house.getAllBaronies());
	}
	
	public void requestExploration(Barony b) {
		explorationTargets.add(b);
	}
	
	public void dealWithExploration(Holding target) {
		explorationTargets.remove(target);
	}
	
	public void addController(Army army)  {
		ArmyController controller = new ArmyController(this, army);
		armyControllers.add(controller);
		World.getInstance().getUpdater().addItem(controller);
	}

	public void informLostBarony(Barony holding) {
		for (int i = 0; i < armyControllers.size(); ++i) {
			if (armyControllers.get(i).army.getHome() == holding) {
				World.getInstance().getUpdater().removeItem(armyControllers.get(i));
				armyControllers.remove(i);
				return;
			}
		}
		
		relevantBaronies.remove(holding);
	}
	
	public void destroy() {
		for (int i = 0; i < armyControllers.size(); ++i) {
			World.getInstance().getUpdater().removeItem(armyControllers.get(i));
		}
	}
	
	public void processWhitePeaceMessage(Message message) {
		War war = message.sender.isEnemy(model.house);
		if (war != null) {
			if (isWhitePeaceAcceptable(war)) {
				message.respond(1);
			} else {
				message.respond(0);
			}
		}
	}
	
	private boolean isWhitePeaceAcceptable(War war) {
		return (war.getProgress(model.house) < -0.8f && war.attackers.get(0) == model.house) || (war.getDuration() > 10 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY && war.getProgress(model.house) < 0.8f);
	}
	
}
