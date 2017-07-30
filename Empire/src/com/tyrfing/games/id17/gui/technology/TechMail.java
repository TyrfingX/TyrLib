package com.tyrfing.games.id17.gui.technology;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.TechnologyEvent;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyProject;
import com.tyrfing.games.id17.world.World;

public class TechMail extends HeaderedMail {
	
	private final Technology t;
	private final House house;
	
	public TechMail(Technology t, House house) {
		super("Research: " + t.name + "\n" + "Expected time: " + (int)(t.scienceMax/house.getResearch()) + "d", "TECH", t.name);
		
		this.t = t;
		this.house = house;
		
		if (house.getGold() < t.funds || house.hasResearched(t) ||!house.canResearch(t)) {
			accept.disable();
		}
	}
	
	@Override
	public void onAccept() {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			house.startTechnologyProject(new TechnologyProject(house, t));
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new TechnologyEvent(t.ID, house.id, -1));
		}
		remove();
	}
	
	public static Mail createMail(Technology tech, House displayed) {
		TechMail m = new TechMail(tech, displayed);
		m.addMainLabel(tech.researchDesc + "\nFunds: " +  tech.funds + "<img MAIN_GUI GOLD_ICON>, Research: " + tech.scienceMax + "<img MAIN_GUI TECH_ICON_BIG>\n\nDiscovery Bonus:\n" + tech.discoverDesc);
		World.getInstance().getMainGUI().mailboxGUI.addMail(m, true);
		return m;
	}
}