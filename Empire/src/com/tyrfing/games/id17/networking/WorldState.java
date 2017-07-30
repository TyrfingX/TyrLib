package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.IProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class WorldState extends NetworkMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7073467249878621561L;
	
	public float timeStamp;
	public float gold;
	public float honor;
	public float taxIncome;
	public float tradeIncome;
	public float armyMaint;
	public float holdingMaint;
	public int males;
	public int females;
	public int countMarriages;
	
	public short visibleProjectCount;
	public float[] projectProgress;
	public short[] projectHolding;
	
	public float[] moral;
	public float[][] troops;
	
	public short[] points;
	public float[] income;
	public float techProgress;
	
	@Override
	public void process(Connection c) {
		World world = World.getInstance();
		world.worldTime = timeStamp;
		
		House h = World.getInstance().getPlayerController().getHouse();
		h.gold.value = gold;
		h.honor = honor;
		h.males = males;
		h.females = females;
		h.taxIncome = taxIncome;
		h.tradeIncome = tradeIncome;
		h.armyMaint = armyMaint;
		h.holdingMaint = holdingMaint;
		h.finalIncome = taxIncome + tradeIncome + armyMaint + holdingMaint;
		h.countMarriages = countMarriages;
		
		next: for (int i = 0; i < World.getInstance().getHoldings().size(); ++i) {
			Holding holding = World.getInstance().getHolding(i);
			for (int j = 0; j < visibleProjectCount; ++j) {
				if (projectHolding[j] == i) {
					IProject p =  holding.getProject();
					if (p != null) {
						holding.displayProject();
						p.setProgress(projectProgress[j]);
					}
					continue next;
				}
				
			}
			holding.hideProject();
		}
		
		if (moral.length != World.getInstance().armies.size()) {
			System.out.println("Network Error in WorldState::synchronize: Unsychnronous state!");
			EmpireFrameListener.MAIN_FRAME.getNetwork().close();
		}
		
		for (int i = 0; i < moral.length; ++i) {
			Army army = World.getInstance().armies.get(i);
			army.setMoral(moral[i]);
			float totalTroops = 0;
			for (int j = 0; j < Army.MAX_REGIMENTS; ++j) {
				Regiment r = army.getRegiment(j);
				if (r != null) {
					if (r.unitType != UnitType.Walls) {
						totalTroops += troops[i][j];
					}
					r.troops = troops[i][j];
				}
			}

			army.totalTroops = totalTroops;
		}
		
		for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
			World.getInstance().getHouses().get(i).points = points[i];
			World.getInstance().getHouses().get(i).income = income[i];
		}
		
		if (h.techProject != null) {
			h.techProject.setProgress(techProgress);
		}
	}
	
	public String toString() {
		return "WorldState: " + timeStamp + "," + gold + "," + honor + "," + males + "," + females;
	}
}
