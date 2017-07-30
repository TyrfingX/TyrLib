package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.war.ArmyBuilderGUI;
import com.tyrfing.games.id17.gui.war.FormationWindow;
import com.tyrfing.games.id17.holdings.projects.UpgradeRegimentProject;
import com.tyrfing.games.id17.networking.UpgradeRegimentMessage;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;

public class UnitMail extends HeaderedMail {
	
	public final UnitType type;
	public final Army army;
	public final int pos;
	public final FormationWindow window;
	public final ArmyBuilderGUI gui;
	
	public UnitMail(UnitType type, Army army, int pos, FormationWindow window, ArmyBuilderGUI gui) {
		super("Build: " + type.toString() + "\n" + "Expected time: " + String.valueOf((int)(UnitType.getProd(type, 0)/army.getHome().getHoldingData().prod)) + "d", "UNIT_ICONS", type.toString());
		
		this.type = type;
		this.army = army;
		this.pos = pos;
		this.window = window;
		this.gui = gui;
		
		if (army.getOwner().getGold() < UnitType.getPrice(type, 0)) {
			accept.disable();
		}
	}
	
	public UnitMail(UnitType type) {
		super(type.toString(), "UNIT_ICONS", type.toString());
		
		this.type = type;
		this.army = null;
		this.pos = 0;
		this.window = null;
		this.gui = null;
		
		accept.disable();
		
	}
	
	@Override
	public void onAccept() {
		Regiment regiment = new Regiment(type, 0, 0, pos);
		army.addRegiment(regiment);
		window.setArmy(army, true);
		gui.hideUnitBuildMenu();
		int cost = (int) UnitType.getPrice(type, 0);
		int prod = (int) UnitType.getProd(type, 0);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			army.getHome().startProject(new UpgradeRegimentProject(prod, regiment, army, cost));
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new UpgradeRegimentMessage(army.getHome().getHoldingID(), regiment.formationPos, regiment.unitType.ordinal(), army == army.getHome().getLevy() ));
		}
		
		
		remove();
		World.getInstance().getMainGUI().pickerGUI.holdingGUI.productionGUI.update();
	}
}
