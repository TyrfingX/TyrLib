package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.gui.MainGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class DisplayedTechStats extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3889001195962873899L;

	public final float progress;
	
	public DisplayedTechStats(House house) {
		if (house.techProject != null) {
			progress = house.techProject.getProgress();
		} else {
			progress = 0;
		}
	}
	
	@Override
	public void process(Connection c) {
		MainGUI mainGUI = World.getInstance().mainGUI;
		
		if (mainGUI.houseGUI.techGUI.isVisible()) {
			House h = mainGUI.houseGUI.techGUI.getDisplayed();
			if (h.techProject != null) {
				h.techProject.setProgress(progress);
			}
		}
	}
	
}
