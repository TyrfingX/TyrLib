package com.tyrfing.games.id17.buildings;

import java.util.Arrays;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.BuildingProject;
import com.tyrfing.games.id17.networking.BuildMessage;
import com.tyrfing.games.id17.world.World;

public class BuildingMail extends HeaderedMail {
	
	public final Building.TYPE type;
	public final Holding h;
	
	public BuildingMail(Building.TYPE type, Holding h) {
		super("Build: " + type.toString() + "\n" + "Expected time: " + String.valueOf((int)(Building.getProd(type, h)/h.getHoldingData().prod)) + "d", "BUILDINGS", type.toString());
		
		this.type = type;
		this.h = h;
		
		if (h.getOwner().getGold() < Building.getPrice(type, h)) {
			accept.disable();
		}
		
		int greatBuildingIndex = Arrays.binarySearch(Building.GREAT_BUILDINGS, type);
		if (greatBuildingIndex >= 0) {
			if (Building.BUILT_GREAT_BUILDINGS[greatBuildingIndex] != h && Building.BUILT_GREAT_BUILDINGS[greatBuildingIndex] != null) {
				accept.disable();
			}
		}
	}
	
	public BuildingMail(Building.TYPE type) {
		super("Build: " + type.toString(), "BUILDINGS", type.toString());
		
		this.type = type;
		this.h = null;
		
		accept.disable();
	}
	
	@Override
	public void onAccept() {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			h.startProject(new BuildingProject(type, h, h.getOwner()));
		} 
		
		EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new BuildMessage(type.ordinal(), h.getHoldingID()));
		
		remove();
		World.getInstance().getMainGUI().pickerGUI.holdingGUI.productionGUI.update();
	}
}
