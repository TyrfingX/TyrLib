package com.tyrfing.games.id17.ai;

import gnu.trove.stack.TFloatStack;
import gnu.trove.stack.array.TFloatArrayStack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.tyrfing.games.id17.ai.actions.AIAction;
import com.tyrfing.games.id17.ai.actions.Execution;
import com.tyrfing.games.id17.ai.objectives.EstablishDefensivePact;
import com.tyrfing.games.id17.ai.objectives.ExpandArmy;
import com.tyrfing.games.id17.ai.objectives.ExpandHoldings;
import com.tyrfing.games.id17.ai.objectives.Explore;
import com.tyrfing.games.id17.ai.objectives.ImproveHoldings;
import com.tyrfing.games.id17.ai.objectives.ImproveRoads;
import com.tyrfing.games.id17.ai.objectives.MakeMoney;
import com.tyrfing.games.id17.ai.objectives.Objective;
import com.tyrfing.games.id17.ai.objectives.ReduceHoldingCount;
import com.tyrfing.games.id17.ai.objectives.SeekIndependence;
import com.tyrfing.games.id17.ai.objectives.Tech;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.actions.WhitePeace;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;


public class ObjectiveModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5018558412438776917L;
	
	protected BehaviorModel model;
	private Stack<Objective> objectives = new Stack<Objective>();
	private TFloatStack spentTime = new TFloatArrayStack();
	private Message waitingForResponse;
	private Decision executingDecision;
	private Stack<Decision> externalDecisions = new Stack<Decision>();
	private static final Map<String, Integer> GENERATED_OBJECTIVE_COUNTS = new HashMap<String, Integer>();
	
	private long lastTime;
	
	public ObjectiveModel(BehaviorModel model) {
		this.model = model;
	}
	
	public Objective getCurrentObjective() {
		return objectives.peek();
	}

	public void takeAction() {
		
		float passedTime = 0;
		
		if (lastTime != 0) {
	    	long time = System.nanoTime();
	    	long diff = time - lastTime;
	    	
	    	lastTime = System.nanoTime();
	    	
	    	passedTime = diff / OpenGLRenderer.BILLION;
	    } else {
	    	lastTime = System.nanoTime();
	    }
		
		if (World.getInstance().isPaused()) return;
		
		if (!externalDecisions.isEmpty()) {
			Decision decision = externalDecisions.pop();
			Message m = new Message((DiploAction)decision.action, model.house, decision.target, decision.options);
			AIThread.getInstance().addMessage(m);
			return;
		}
		
		if (waitingForResponse == null) {
			if (objectives.isEmpty()) {
				generateObjective();
			}
			
			if (!objectives.isEmpty()) {
			
				Objective objective = objectives.peek();
				float currentlySpentTime = spentTime.pop();
				currentlySpentTime += passedTime;
				
				if (currentlySpentTime >= objective.maxTime) {
					objectives.pop();
					return;
				} else {
					spentTime.push(currentlySpentTime);
				}
				
				Decision decision = objective.achieve();
				
				if (!objective.hasFailed() && decision != null) {
					if (decision.action != null) {
						if (decision.action instanceof DiploAction) {
							if (((DiploAction)decision.action).isEnabled(model.house, decision.target)) {
								if (((DiploAction)decision.action).getResponses() > 0) {
									executingDecision = decision;
									waitingForResponse = new Message((DiploAction)decision.action, model.house, decision.target, decision.options);
									AIThread.getInstance().addMessage(waitingForResponse);
								} else {
									Message m = new Message((DiploAction)decision.action, model.house, decision.target, decision.options);
									AIThread.getInstance().addMessage(m);
									
									if (decision.achievesObjective) {
										objectives.pop();
										spentTime.pop();
									}
									
									if (decision.objective != null) {
										objectives.push(decision.objective);
										spentTime.push(0f);
									}
								}
							}
						} else if (decision.action instanceof AIAction){
							AIThread.getInstance().addExecution(new Execution(((AIAction)decision.action), model.house, decision.options));
						} else if (decision.action instanceof IntrigueAction) {
							AIThread.getInstance().addIntrigue(new IntrigueProject(((IntrigueAction)decision.action), model.house, decision.target, decision.options));
						} else {
							
						}
					} else {
						if (decision.achievesObjective) {
							objectives.pop();
							spentTime.pop();
						}
						
						if (decision.objective != null) {
							objectives.push(decision.objective);
							spentTime.push(0f);
						}
					}
					
				}
			
			}
		}
		
	}
	
	private void generateObjective() {
		if (model.house.isIndependend()) {
			
			//Objective o = new ExpandArmy(model, new int[] { World.getInstance().getIndexOfHolding(model.house.getBaronies().get(0)) } , 100);
			if (model.house.getSubHouses().size() > 0 && model.house.getHoldings().size() > model.house.getMaxHoldings()) {
				Objective o = new ReduceHoldingCount(model, new int[] { model.house.getMaxHoldings() } , 30);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_ReduceHoldingCount");
			} else if (Math.random() <= model.house.getCountSubHoldings() / (model.house.getTotalCountRoads()+1) && model.getTargetRoad() != null) {
				Objective o = new ImproveRoads(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);		
				countGeneration("HIGH_ImproveRoads");
			} else if (Math.random() <= 0.05f && model.getNeighbourExplorationTarget() != null) {
				Objective o = new Explore(model, new int[] { model.getNeighbourExplorationTarget().getIndex() } , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_Explore_NEIGHBOUR");
			} else if (Math.random() <= 0.5f) {
				List<Barony> baronies = model.house.getBaronies();
				Barony barony = baronies.get((int)(Math.random() * baronies.size()));
				Objective o = new ExpandArmy(model, new int[] { barony.getHoldingID() } , 10);
				objectives.push(o);
				spentTime.push(0f);		
				countGeneration("HIGH_ExpandArmy");
			} else if (Math.random() <= 0.15f) {
				Objective o = new ExpandHoldings(model, null , 30);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_ExpandHoldings");
			} else if (Math.random() <= 0.8f) {
				Objective o = new ImproveHoldings(model, null , 20);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_ImproveHoldings");
			} else if (Math.random() <= 0.05f && model.house.getGold() >= 300 && model.getNeighbourExplorationTarget() != null) {
				Objective o = new Explore(model, new int[] { model.getNeighbourExplorationTarget().getIndex() } , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_Explore_WORLD");
			} else if (Math.random() <= 0.7f && model.house.techProject == null) {
				Objective o = new Tech(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("HIGH_Tech");
			} else {
				int id = model.getDefensivePactPartner();
				if (id != -1) {
					Objective o = new EstablishDefensivePact(model, new int[] { id } , 100);
					objectives.push(o);
					spentTime.push(0f);
					countGeneration("HIGH_EstablishDefensivePact");
				} else {
					Objective o = new ImproveHoldings(model, null , 10);
					objectives.push(o);
					spentTime.push(0f);
					countGeneration("HIGH_ImproveHoldings");
				}
			}
		} else if (model.house.getBaronies().size() > 0) {
			if (Math.random() <= 0.5f && model.house.techProject == null) {
				Objective o = new Tech(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_Tech");
			} else if (Math.random() <= Math.sqrt(model.house.getCountSubHoldings()) / (model.house.getTotalCountRoads()+1)  && model.getTargetRoad() != null) {
				Objective o = new ImproveRoads(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);		
				countGeneration("MID_ImproveRoads");
			} else if (Math.random() <= 0.6f) {
				Objective o = new ImproveHoldings(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_ImproveHoldings");
			} else if (Math.random() <= 0.1f) {
				Objective o = new ExpandHoldings(model, null , 50);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_ExpandHoldings");
			} else if (Math.random() <= 0.01f) {
				Objective o = new MakeMoney(model, new int[] { 1000 } , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_MakeMoney");
			} else if (Math.random() <= 0.15f) {
				Objective o = new Explore(model, new int[] { model.getWorldExplorationTarget().getIndex() } , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_Explore");
			} else if (model.house.getRelation(model.house.getSupremeOverlord()) < -50 && Math.random() <= 0.1f) {
				Objective o = new SeekIndependence(model, null , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_SeekIndependence");
			} else {
				Objective o = new MakeMoney(model, new int[] { 1000 } , 10);
				objectives.push(o);
				spentTime.push(0f);
				countGeneration("MID_MakeMoney");
			}
		} else if (Math.random() <= 0.9f) {
			Objective o = new ImproveHoldings(model, null , 10);
			objectives.push(o);
			spentTime.push(0f);
			countGeneration("LOW_ImproveHoldings");
		} else if (Math.random() <= 0.7f && model.house.techProject == null) {
			Objective o = new Tech(model, null , 10);
			objectives.push(o);
			spentTime.push(0f);
			countGeneration("LOW_Tech");
		} else if (Math.random() <= 0.05f) {
			Objective o = new ExpandHoldings(model, null , 50);
			objectives.push(o);
			spentTime.push(0f);
			countGeneration("LOW_ExpandHoldings");
		} else  {
			Objective o = new MakeMoney(model, new int[] { 200 } , 50);
			objectives.push(o);
			spentTime.push(0f);
			countGeneration("LOW_MakeMoney");
		} 
		
		
	}
	
	public void processMessage(Message message) {
		if (waitingForResponse != message) {
			
			if (message.action instanceof WhitePeace) {
				model.armyModel.processWhitePeaceMessage(message);
			} else {
				float value = getResponseValue(1, message);

				if (message.action instanceof InviteToIntrigue && model.house.intrigueProject != null) {
					value = 0;
				}
				
				int response = (0.5f <= value) ? 1 : 0;
				
				model.memories.add(new Memory(message.action, message.sender, message.receiver, message.options, message.timeStamp, response));
				
				if (message.action.getResponses() > 0 && message.response == -1) {
					message.respond(response);
				} else if (message.response == -2) { // This is actually a message which has to be sent!
					message.options = new int[] { response };
					AIThread.getInstance().addMessage(message);
				} 
			}

		} else {
			if (executingDecision.achievesObjective) {
				objectives.pop();
				spentTime.pop();
			}
			
			if (executingDecision.objective != null) {
				objectives.push(executingDecision.objective);
				spentTime.push(0f);
			}
			
			waitingForResponse = null;
		}
	}
	
	public void addExternalDecision(Decision decision) {
		externalDecisions.add(decision);
	}
	
	public float getResponseValue(int response, Message message) {
		float res = model.getResponseValue(response, message);
		Stack<Objective> stack = new Stack<Objective>();
		for (int i = 0; i < stack.size(); ++i) {
			Objective o = stack.get(i);
			res *= o.getResponseValue(response, message);
		}
		
		return res;
	}
	
	private void countGeneration(String objective) {
		if (!GENERATED_OBJECTIVE_COUNTS.containsKey(objective)) {
			GENERATED_OBJECTIVE_COUNTS.put(objective, 1);
		} else {
			GENERATED_OBJECTIVE_COUNTS.put(objective, GENERATED_OBJECTIVE_COUNTS.get(objective)+1);
		}
	}
	
	public static void printGenerationCount() {
		int total = 0;
		System.out.println("----------------------------------------------------");
		for (String objective : GENERATED_OBJECTIVE_COUNTS.keySet()) {
			Integer count = GENERATED_OBJECTIVE_COUNTS.get(objective);
			total += count;
			System.out.println(objective + ": " + count);
		}
		System.out.println("");
		System.out.println("TOTAL: " + total);
		System.out.println("----------------------------------------------------");
	}
	
}
