package com.tyrfing.games.id17.technology;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.projects.IProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.TechnologyEvent;

public class TechnologyProject implements IProject  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6742803798916696161L;
	public final Technology tech;
	private House house;
	
	private float progress;
	
	private boolean finished;
	
	public TechnologyProject(House house, Technology tech) {
		this.tech = tech;
		this.house = house;
		
		house.changeGold(-tech.funds);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TechnologyEvent(tech.ID, house.id, -1));
		}
	}
	
	@Override
	public void onUpdate(float time) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			progress += house.getResearch() * time;
			if (progress >= tech.scienceMax) {
				progress = tech.scienceMax;
				finish();
			}
		}
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public float getProgress() {
		return progress / tech.scienceMax;
	}

	@Override
	public void abort() {
		house.changeGold(tech.funds);
		finished = true;
	}

	@Override
	public String getIconAtlasName() {
		return null;
	}

	@Override
	public String getIconRegionName() {
		return null;
	}
	
	@Override
	public void setProgress(float progress) {
		this.progress = progress*tech.scienceMax;
	}

	@Override
	public void finish() {
		finished = true;
		house.techProject = null;
		tech.onResarch(house);
	}

	public int getEstimatedRemainingDays() {
		return (int) ((tech.scienceMax-progress) / house.getResearch());
	}

}
