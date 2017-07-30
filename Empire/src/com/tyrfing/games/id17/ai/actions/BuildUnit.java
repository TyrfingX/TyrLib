package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.projects.UpgradeRegimentProject;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;

/**
 * OPTIONS:
 * 0: UnitType
 * 1: Barony ID
 * 2: Regiment position
 * 3: Levy or garrison (1 Levy, 0 garrison)
 * @author Sascha
 *
 */

public class BuildUnit extends AIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1922183866652123837L;

	public BuildUnit() {
		super("Build Unit");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		
		Barony barony = (Barony) World.getInstance().getHolding(options[1]);
		
		UnitType type = UnitType.values()[options[0]];
		
		if (!barony.getOwner().isUnitEnabled(type)) return false;
		
		Army army = (options[3] == 1) ? barony.getLevy() : barony.getGarrison();
		
		Regiment regiment = army.getRegiment(options[2]);
		
		int level = 0;
		if (regiment != null) {
			level = (int) (regiment.maxTroops / 100);
		}
		
		return !barony.hasActiveProject() && executor.getGold() >= UnitType.getPrice(type, level);
	}

	@Override
	public void execute(House executor, int[] options) {
		Barony barony = (Barony) World.getInstance().getHolding(options[1]);
		
		Army army = (options[3] == 1) ? barony.getLevy() : barony.getGarrison();
		UnitType type = UnitType.values()[options[0]];
		Regiment regiment = army.getRegiment(options[2]);
		int level = 0;
		if (regiment == null) {
			regiment = new Regiment(type, 0, 0, options[2]);
			army.addRegiment(regiment);
		}  else {
			level = (int) (regiment.maxTroops / 100);
		}
		
		int cost = (int) UnitType.getPrice(type, level);
		int prod = (int) UnitType.getProd(type, level);
		barony.startProject(new UpgradeRegimentProject(prod, regiment, army, cost));
	}

}
