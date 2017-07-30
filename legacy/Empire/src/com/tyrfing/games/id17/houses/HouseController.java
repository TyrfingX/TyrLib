package com.tyrfing.games.id17.houses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.ChatListener;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.util.Color;

public abstract class HouseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2680895714949707615L;
	protected House house;
	protected List<House> subFactions = new ArrayList<House>();
	
	public boolean hasJoined;
	public int playerID;
	
	public static final Color DEFAULT_STRATEGIC_COLOR = Color.fromRGBA(238, 205, 172, 255);
	
	public void control(House house) {
		if (!house.isMarauder()) {
			this.house = house;
		} else {
			this.addSubFaction(house);
		}
	}
	
	public House getHouse() {
		return house;
	}
	
	public abstract void informMessage(Message message);
	public abstract void informNewHolding(Holding holding);
	public abstract void informLostHolding(Holding holding);
	public void informWarStart(War war) {}
	public void informWarEnd(War war) {}
	public void informAddAlly(House house) {}
	public void informRemoveAlly(House house) {}
	
	public abstract void destroy();
	
	public void unmarkControlledHouse() {
		if (this.house != null) {
			for (int i = 0; i < this.house.getHoldings().size(); ++i) {
				Entity e = this.house.getHoldings().get(i).holdingData.worldEntity;
				((DefaultMaterial3)e.getSubEntity(0).getMaterial()).setColor(Color.BLACK);
			}
		}
	}
	
	public void markControlledHouse() {
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Entity e = house.getHoldings().get(i).holdingData.worldEntity;
			((DefaultMaterial3)e.getSubEntity(0).getMaterial()).setColor(ChatListener.chatColors[playerID]);
		}
	}

	public Color getStrategicColor() {
		return DEFAULT_STRATEGIC_COLOR;
	}
	
	public void addSubFaction(House subFaction) {
		subFaction.gold = house.gold;
		this.subFactions.add(subFaction);
		
		for (int i = 0; i < house.getCountVisibleBaronies(); ++i) {
			subFaction.addVisibleBarony(World.getInstance().getBarony(house.getVisibleBarony(i)), false);
		}
	}
	
	public int getCountSubFactions() {
		return subFactions.size();
	}
	
	public House getSubFaction(int index) {
		return subFactions.get(index);
	}
	
	public void removeSubFaction(House owner) {
		subFactions.remove(owner);
	}
	
	public War isEnemy(House other) {
		War war = house.isEnemy(other);
		if (war != null) return war;
		
		for (int i = 0; i < subFactions.size(); ++i) {
			war = subFactions.get(i).isEnemy(other);
			if (war != null) return war;
		}
		
		return null;
	}

	public boolean isSubjectOf(House other) {
		if (house.isSubjectOf(other)) return true;
		
		for (int i = 0; i < subFactions.size(); ++i) {
			if (subFactions.get(i).isSubjectOf(other)) return true;
		}
		
		return false;
	}
	
}
