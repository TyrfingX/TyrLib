package com.tyrfing.games.id17.ai;

import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.HandleRevolt;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.actions.PayLoan;
import com.tyrfing.games.id17.diplomacy.actions.RequestLoan;
import com.tyrfing.games.id17.diplomacy.actions.RequestResearchFunds;
import com.tyrfing.games.id17.diplomacy.actions.TradeAgreement;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.Village;
import com.tyrfing.games.id17.holdings.projects.RoadProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.Loan;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.util.Pair;

public class BehaviorModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8673030149210801821L;
	
	public static final float MARRIAGE_RELATION_WEIGHT = 1;
	public static final float MARRIAGE_INCOME_WEIGHT = 5;
	public static final float WAR_TARGET_RELATION_WEIGHT = 1;
	public static final float WAR_OVERLORD_RELATION_WEIGHT = 1;
	public static final float WAR_INCOME_WEIGHT = 40;
	public static final float WAR_EASY_WEIGHT = 100;
	public static final float REVOKE_RELATION_WEIGHT = 3;
	public static final float REVOKE_INCOME_WEIGHT = 1;
	public static final float REVOKE_ARMY_WEIGHT = 0.25f;
	public static final float TYRANNICAL = 0.1f;
	public static final float BUILD_PROB = 0.75f;
	public static final int MAX_DAYS_ROAD = 500;
	
	public static final float FRIENDS = 100;
	public static final float ENEMYS = -100;

	private static final float BARONY_WEIGHT = 10;
	
	public ObjectiveModel actionModel = new ObjectiveModel(this);
	public ArmyModel armyModel = new ArmyModel(this);
	
	public static final int NO_RESPONSE_MEMORIZED = -1000;
	protected List<Memory> memories = new ArrayList<Memory>();
	
	public int bufferMoney = 200;
	
	public House house;
	
	private float[] houseFactors;
	private float[] houseValues;
	
	
	public BehaviorModel() {
	}
	
	public void processMessage(Message message) {
		actionModel.processMessage(message);
	}
	
	public void takeAction() {
		actionModel.takeAction();
	}
	
	public House getHouse() {
		return house;
	}
	
	public House getMarriageCandidate() {
		int exploredHouses = house.getExploredHouses().size();
		float[] weights = new float[exploredHouses];
		float sum = 0;
		
		House candidate = null;
		
		for (int i = 0; i < weights.length; ++i) {
			House other = World.getInstance().getHouses().get(house.getExploredHouses().get(i));
			if (other != house) {
				float value = house.getRelation(other) * MARRIAGE_RELATION_WEIGHT + other.getIncome() * MARRIAGE_INCOME_WEIGHT;
				weights[i] = value + sum;
				sum += value;
				
				candidate = other;
			} 
		}
		
		float r = (float) Math.random();
		
		for (int i = 0; i < weights.length; ++i) {
			if (r <= weights[i] / sum) {
				return World.getInstance().getHouses().get(house.getExploredHouses().get(i));
			}
		}
		
		return candidate;
	}
	
	public House getRandomNeighbour() {
		if (house.neighbours.size() == 0) return null;
		return house.houseNeighbours.get((int) (Math.random() * house.houseNeighbours.size()));
	}
	
	public Holding revokeHolding() {
		
		int branches = house.getSubHouses().size();
		
		if (house.getMaxHoldings() < house.getHoldings().size() && branches > 0 && Math.random() <= TYRANNICAL) {
		
			int holdings = 0;
			
			for (int i = 0; i < house.getSubHouses().size(); ++i) {
				House subHouse = house.getSubHouses().get(i);
				holdings += subHouse.getHoldings().size();
			}
			
			float[] weights = new float[holdings];
			float sum = 0;
			int index = 0;
			
			for (int i = 0; i < branches; ++i) {
				House subHouse = house.getSubHouses().get(i);
				float relatonValue = -house.getRelation(subHouse) * REVOKE_RELATION_WEIGHT;
				for (int j = 0; j < subHouse.getHoldings().size(); ++j) {
					Holding holding = subHouse.getHoldings().get(j);
					float value = relatonValue + holding.holdingData.income * REVOKE_INCOME_WEIGHT;
					if (holding instanceof Barony) {
						value += ((Barony)holding).getLevy().getTotalTroops() * REVOKE_ARMY_WEIGHT;
					}
					weights[index++] = value + sum;
					sum += value;
				}
			}
			
			float r = (float) Math.random();
			index = 0;
			
			for (int i = 0; i < branches; ++i) {
				House subHouse = house.getSubHouses().get(i);
				for (int j = 0; j < subHouse.getHoldings().size(); ++j) {
					if (r <= weights[index++] / sum) {
						return subHouse.getHoldings().get(j);
					}
				}
			}
		
		}
		
		return null;
	}
	
	public Holding conquerTarget() {
		if (house.neighbours.size() == 0) return null;
		
		if (houseFactors == null) {
			houseFactors = new float[World.getInstance().getHouses().size()];
			houseValues = new float[World.getInstance().getHouses().size()];
		}
		
		House house = this.house.getSupremeOverlord();
		List<Holding> targets = new ArrayList<Holding>();

		Iterator<Barony> itr = house.neighbours.iterator();

		while(itr.hasNext()) {
			Barony barony = itr.next();
			for (int i = 0; i < barony.getCountSubHoldings(); ++i) {
				Holding subHolding = barony.getSubHolding(i);
				if (!subHolding.getOwner().isSubjectOf(house)) {
					targets.add(subHolding);
					
					int supOID = subHolding.getOwner().getSupremeOverlord().id;
					
					houseValues[supOID] = 0;
					houseFactors[supOID] = 0;
				}
			}
		}
		
		int countTargets = targets.size();
		float[] weights = new float[countTargets];
		float sum = 0;
		
		float totalTroops = house.getWeighedTotalTroopCount();
		
		for (int i = 0; i < countTargets; ++i) {
			Holding holding = targets.get(i);
			House supOverlord = holding.getOwner().getSupremeOverlord();
			
			if (houseFactors[supOverlord.id] == 0) {
				houseValues[supOverlord.id] = WAR_EASY_WEIGHT * totalTroops / (supOverlord.getWeighedTotalTroopCount()+1.f);
				houseFactors[supOverlord.id] = 1;
				
				if (house.getHouseStat(supOverlord, House.HAS_MARRIAGE) == 1) {
					houseFactors[supOverlord.id] /= 8;
				}
				if (house.getHouseStat(supOverlord, House.HAS_TRADE_AGREEMENT) == 1) {
					houseFactors[supOverlord.id] /= 8;
				}
				if (house.getHouseStat(supOverlord, House.HAS_DEFENSIVE_PACT) == 1) {
					houseFactors[supOverlord.id] /= 8;
				}
				if (house.getHouseStat(supOverlord, House.HAS_SPY) == 1) {
					houseFactors[supOverlord.id] *= 2;
				}
			}
			
			float factor = houseFactors[supOverlord.id];
			float easynessValue = houseValues[supOverlord.id];
			float relationValue = WAR_TARGET_RELATION_WEIGHT * -house.getRelation(holding.getOwner()) + WAR_OVERLORD_RELATION_WEIGHT * -house.getRelation(supOverlord);
			float targetValue = WAR_INCOME_WEIGHT * holding.holdingData.income;
			
			if (holding.holdingData.barony == holding) {
				factor *= BARONY_WEIGHT;
			}

			if (house.hasWarReason(holding) != WarGoal.NO_REASON) {
				factor *= 4;
			}
			
			float value = (relationValue + targetValue + easynessValue) * factor;
			
			if (value < 0) value = 0;
			
			weights[i] = value + sum;
			sum += value;
		}
		
		float r = (float) Math.random();
		
		for (int i = 0; i < countTargets; ++i) {
			if (r <= weights[i] / sum) {
				return targets.get(i);
			}
		}
		
		return null;
	}
	
	public float getRelationFactor(House house) {
		float relation = house.getRelation(house);
		if (relation > FRIENDS) {
			return 2f;
		} else if (relation < ENEMYS){
			return 0.1f;
		} else {
			return 0.5f;
		}
	}
	
	public float getResponseValue(int response, Message message) {
		
		memoryCheck: for (int i = 0; i < memories.size(); ++i) {
			Memory memory = memories.get(i);
			if (memory.action == message.action && memory.sender == message.sender) {
				if (memory.options != null) {
					for (int j = 0; j < memory.options.length; ++j) {
						if (memory.options[j] != message.options[j]) {
							break memoryCheck;
						}
					}
				}
				
				memory.timestamp = message.timeStamp;
				
				if (memory.response == 0) return 0;
			}
		}
		
		float res = getRelationFactor(message.sender);
		
		if (message.action instanceof InviteToIntrigue) {
			float relationSender = house.getRelation(message.sender);
			
			IntrigueProject project = message.sender.intrigueProject;
			if (project != null) {
			
				float relationReceiver = house.getRelation(message.sender.intrigueProject.receiver);
				
				if (relationReceiver > relationSender) return 0;
				
				res *= getRelationFactor(message.sender.intrigueProject.receiver);
				
				if (InviteToIntrigue.refusingCostsHonor(message.sender, message.receiver)) {
					res *= 2;
					if (house.isActive("Honorable")) {
						res *= 2;
					}
				} else {
					res *= 0.5f;
				}
				
				if (house.intrigueProject != null) {
					res *= 0.25f;
				}
				
				if (house.getSupremeOverlord() == message.sender) {
					res *= 2;
				}
			}
		} else if (message.action instanceof Marriage) {
			if (message.sender.isSubjectOf(message.receiver)) {
				res *= 8;
			} 
		} else if (message.action instanceof TradeAgreement || message.action instanceof DefensivePact) {
			if (message.sender.getHouseStat(house, House.HAS_MARRIAGE) == 1) {
				res *= 2;
			}
		} else if (message.action instanceof RequestResearchFunds) {
			if (house.isActive("Honorable") && message.sender.getHouseStat(house, House.FAVOR_STAT) >= RequestResearchFunds.BASE_FAVOR) {
				res *= 4;
			} else {
				res *= 2;
			}
		} else if (message.action instanceof HandleRevolt) {
			res = 1;
		} else if (message.action instanceof RequestLoan) {
			float money = Loan.getLoanSize(message.sender, message.receiver);
			res *= house.getGold() / money;
		} else if (message.action instanceof PayLoan) {
			res *= 2;
			if (house.isActive("Honorable")) {
				res *= 2;
			}
		}
		
		return res;
	}
	
	public House chooseIntriguePartner() {
		House target = house.intrigueProject.receiver;
		TIntArrayList houses = house.getExploredHouses();
		float[] weights = new float[houses.size()];
		
		float max = 0;
		
		for (int i = 0; i < weights.length; ++i) {
			House h = World.getInstance().getHouses().get(houses.getQuick(i));
			if (h.intrigueProject == null) {
				float power = h.getCourtPower(target);
				if (h.isSubjectOf(house)) {
					power *= 2;
				} else if (house.isSubjectOf(h)) {
					power /= 4;
				}
				max += power * power;
				weights[i] = max;
			} else {
				weights[i] = 0;
			}
		}
		
		float random = (float) (max * Math.random());
		for (int i = 0; i < weights.length; ++i) {
			if (random <= weights[i]) {
				return World.getInstance().getHouses().get(houses.getQuick(i));
			}
		}
		
		return null;
	}
	
	public House getGrantHoldingHouse() {
		
		if (house.getSubHouses().size() == 0) return null;
		
		House best = house.getSubHouses().get(0);
		for (int i = 1; i < house.getSubHouses().size(); ++i) {
			House subHouse = house.getSubHouses().get(i);
			if (subHouse.getHoldings().size() < best.getHoldings().size()) {
				best = subHouse;
			}
		}
		return best;
	}
	
	public Holding getGrantHoldingHolding(House subHouse) {
		
		if (house.getHoldings().size() == 0) return null;
		
		Holding best = house.getHoldings().get(0);
		for (int i = 1; i < house.getHoldings().size(); ++i) {
			Holding holding = house.getHoldings().get(i);
			if (!(holding instanceof Barony) && best instanceof Barony) {
				best = holding;
			} else if (best.holdingData.income < holding.holdingData.income) {
				if (!(holding instanceof Barony) || best instanceof Barony) {
					best = holding;
				}
			}
		}
		return best;
	}
	
	public int getDefensivePactPartner() {
		if (house.neighbours.size() == 0) return -1;
		
		Iterator<Barony> itr = house.neighbours.iterator();

		while(itr.hasNext()) {
			House target = itr.next().getOwner().getSupremeOverlord();
			if (	target.isIndependend() 
				&& 	target.getRelation(house) >= 0 
				&& 	house.getRelation(target) >= 0 
				&& 	Math.random() <= 0.5f 
				&& 	house.isEnemy(target) == null && house.getHouseStat(target, House.HAS_DEFENSIVE_PACT) != 1
				&& 	target != house) {
				return target.id;
			}
		}
		
		return -1;
	}
	
	public Technology getTechTarget() {
		
		// TODO Add proper heuristic for choosing tech including planning TODO
		
		List<Technology> techables = new ArrayList<Technology>();
		
		Technology[] techs = World.getInstance().techTreeSet.trees[0].techs;
		for (int i = 0; i < techs.length; ++i) {
			if (house.canResearch(techs[i])) {
				techables.add(techs[i]);
			}
		}
		
		if (techables.size() > 0) {
			return techables.get((int)(Math.random()*techables.size()));
		} else {
			return null;
		}
		
	}
	
	public House getGrantLoanHouse() {
		
		float money = Loan.getLoanSize(house, house);
		
		int exploredHouses = house.getExploredHouses().size();
		float[] weights = new float[exploredHouses];
		float res = 0;
		
		for (int i = 0; i < exploredHouses; ++i) {
			House h = World.getInstance().getHouses().get(house.getExploredHouses().get(i));
			if (h != house && h.getGold() >= money) {
				if (h.isSubjectOf(house)) {
					res += 1;
				} else if (!h.isIndependend()) {
					res += 0.1f;
				} else {
					res += 0.01f;
				}
				
				res *= h.getGold() / money;
				res *= this.getRelationFactor(h);
			}
			
			weights[i] = res;
		}
		
		double rnd = Math.random();
		
		for (int i = 0; i < exploredHouses; ++i) {
			House h = World.getInstance().getHouses().get(house.getExploredHouses().get(i));
			if (h != house && h.getGold() >= money && rnd <= weights[i] / res) {
				return h;
			}
		}
		
		return null;
	}

	public double getAggressiveness() {
		
		if (house.hasReputation("Tyrant"))  {
			return 0.3;
		}
		
		if (house.hasReputation("Warmongerer"))  {
			return 0.2;
		}
		
		if (house.hasReputation("Honorable"))  {
			return 0.01;
		}
		
		return 0.05;
	}

	public Barony getNeighbourExplorationTarget() {
		List<Barony> baronies = house.getAllBaronies();
		List<Barony> unexplored = new ArrayList<Barony>();
		
		for (int i = 0; i < baronies.size(); ++i) {
			Barony[] neighbours = World.getInstance().getMap().getNeighbours(baronies.get(i));
			for (int j = 0; j < neighbours.length; ++j) {
				if (!house.isVisible(neighbours[j].getIndex())) {
					unexplored.add(neighbours[j]);
				}
			}
		}
		
		if (unexplored.size() > 0) {
			Barony b = unexplored.get((int)(unexplored.size()*Math.random()));
			if (house.isVisible(b.getIndex())) {
				return null;
			} else {
				return b;
			}
		} 
		
		return null;
	}
	
	public Barony getWorldExplorationTarget() {
		Barony b = World.getInstance().getHoldings().get((int)(World.getInstance().getHoldings().size()*Math.random())).holdingData.barony;
		if (house.isVisible(b.getIndex())) {
			return null;
		} else {
			return b;
		}
	}
	
	
	public int recallReceivedResponse(DiploAction action, House receiver, int[] options) {
		memoryCheck: for (int i = 0; i < memories.size(); ++i) {
			Memory memory = memories.get(i);
			if (memory.action == action && memory.receiver == receiver) {
				if (memory.options != null) {
					for (int j = 0; j < memory.options.length; ++j) {
						if (memory.options[j] != options[j]) {
							break memoryCheck;
						}
					}
				}
				return memory.response;
			}
		}
	
		return NO_RESPONSE_MEMORIZED;
	}

	public Pair<Holding, Holding> getTargetRoad() {
		Pair<Holding, Holding> bestPair = null;
		float bestValue = 0;
		
		for (int i = 0; i < house.holdings.size(); ++i) {
			Holding h = house.holdings.get(i);
			if (h.holdingData.barony.getOwner() == house) {
				Holding[] n = World.getInstance().getMap().getNeighboursHolding(h);
				for (int j = 0; j < n.length; ++j) {
					if (RoadProject.canBuild(h, n[j])) {
						float degreeValue = 1f / (World.getInstance().getMap().getRoadMap().getPath(h.getHoldingID(), n[j].getHoldingID(), false).size()+1);
						float costFactor = 1f / (RoadProject.getCosts(h.holdingData.barony) * RoadProject.getExpectedDays(h));
						float goodsFactor = n[j].getCountSuppliedGoods()+1;
						float ownerFactor = n[j].getOwner().haveSameOverlordWith(house) ? 3 : 1;
						float value = ownerFactor * goodsFactor * degreeValue * costFactor;
						
						if (n[j] instanceof Village) {
							value *= 5;
						}
						
						if (value > bestValue) {
							bestValue = value;
							bestPair = new Pair<Holding, Holding>(h, n[j]);
						}
					}
				}
			}
		}
		
		if (bestPair != null) {
			if (RoadProject.getExpectedDays(bestPair.getFirst()) > MAX_DAYS_ROAD) {
				return null;
			}
		}
		
		return bestPair;
	}
}
