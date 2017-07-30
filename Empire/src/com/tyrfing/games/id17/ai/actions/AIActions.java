package com.tyrfing.games.id17.ai.actions;

import java.util.ArrayList;
import java.util.List;


public class AIActions {
	
	public static final List<AIAction> actions = new ArrayList<AIAction>();
	
	
	public static final int BUILD_UNIT = 0;
	public static final int UPGRADE_BUILDING = 1;
	public static final int CHANGE_LAW = 2;
	public static final int START_TECH = 3;
	public static final int SETTLE_GUILD = 4;
	public static final int BUILD_ROAD = 5;
	
	static {
		actions.add(new BuildUnit());
		actions.add(new UpgradeBuilding());
		actions.add(new ChangeLaw());
		actions.add(new StartTeching());
		actions.add(new SettleGuild());
		actions.add(new BuildRoad());
	}
}
