package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.BuildingProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

/**
 * OPTIONS:
 * 0: Holding ID
 * 1: Building Type ID
 * @author Sascha
 *
 */

public class UpgradeBuilding extends AIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4341364021493901122L;

	public UpgradeBuilding() {
		super("Upgrade Building");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		return sIsEnabled(executor, options);
	}
	
	public static boolean sIsEnabled(House executor, int[] options) {
		Building.TYPE type = Building.VALUES[options[1]];
		Holding holding = World.getInstance().getHolding(options[0]);
		return 		holding.getOwner() == executor
				&&	executor.getGold() >= Building.getPrice(type, holding) 
				&& !holding.hasActiveProject()
				&&	Building.isBuildableInHolding(type, holding);
	}

	@Override
	public void execute(House executor, int[] options) {
		World.getInstance().getHoldings().get(options[0]).startProject(new BuildingProject(	Building.VALUES[options[1]], 
																							World.getInstance().getHolding(options[0]), 
																							executor));
	}

}
