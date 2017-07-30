package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyProject;
import com.tyrfing.games.id17.world.World;

public class StartTeching extends AIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8033595258278696610L;

	/**
	 * OPTIONS:
	 * 0: Tech ID
	 */
	
	public StartTeching() {
		super("Start Teching");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		Technology t = World.getInstance().techTreeSet.trees[0].techs[options[0]];
		return executor.getGold() >= t.funds && !executor.hasResearched(t) && executor.canResearch(t);
	}

	@Override
	public void execute(House executor, int[] options) {
		Technology t = World.getInstance().techTreeSet.trees[0].techs[options[0]];
		executor.startTechnologyProject(new TechnologyProject(executor, t));
	}

}
