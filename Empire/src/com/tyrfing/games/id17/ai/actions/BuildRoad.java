package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.holdings.projects.RoadProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

/**
 * OPTIONS:
 * 0: Holding ID From
 * 1: Holding ID To
 * @author Sascha
 *
 */

public class BuildRoad extends AIAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2018898733381330326L;

	public BuildRoad() {
		super("Build Road");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		return sIsEnabled(executor, options);
	}
	
	public static boolean sIsEnabled(House executor, int[] options) {
		return 	RoadProject.canBuild(World.getInstance().getHolding(options[0]), World.getInstance().getHolding(options[1]));
	}

	@Override
	public void execute(House executor, int[] options) {
		World.getInstance().getHoldings().get(options[0]).startProject(new RoadProject(		World.getInstance().getHolding(options[0]), 
																							World.getInstance().getHolding(options[1])));
	}

}
