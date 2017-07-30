package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class DisplayedLawStats extends NetworkMessage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7829010306059748674L;
	
	public final boolean canPassLaws;
	public byte[] lawSettings;
	
	public DisplayedLawStats(House house) {
		canPassLaws = house.canPassLaws();
		
		lawSettings = new byte[house.lawSettings.length];
		System.arraycopy(house.lawSettings, 0, lawSettings, 0, lawSettings.length);
	}
	
	@Override
	public void process(Connection c) {
		House h = World.getInstance().mainGUI.houseGUI.lawGUI.getDisplayed();
		if (h != null) {
			
			boolean change = false;
			
			if (canPassLaws) {
				if (h.passedLawTime != 0) {
					change = true;
				}
				h.passedLawTime = 0;
			} else {
				if (h.passedLawTime == 1) {
					change = true;
				}
				h.passedLawTime = 1;
			}
			
			for (int i = 0; i < h.lawSettings.length; ++i) {
				if (h.lawSettings[i] != lawSettings[i]) {
					change = true;
				}
				h.lawSettings[i] = lawSettings[i];
			}
			
			if (change) {
				World.getInstance().mainGUI.houseGUI.lawGUI.update();
			}
			
		}
	}
}
