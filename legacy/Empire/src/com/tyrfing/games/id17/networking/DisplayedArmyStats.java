package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class DisplayedArmyStats extends NetworkMessage {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2671894513065164993L;
	public final float[] reinf;
	public final float ownerHonor;
	public final float ownerGold;
	public final int maint;
	public final float raisedTime;
	
	public DisplayedArmyStats(Army army) {
		this.reinf = new float[army.reinf.length];
		for (int i = 0; i < reinf.length; ++i) {
			this.reinf[i] = army.reinf[i];
		}
		ownerHonor = army.getOwner().getHonor();
		ownerGold = army.getOwner().getGold();
		maint = army.maint;
		raisedTime = army.raisedTime;
	}
	
	@Override
	public void process(Connection c) {
		Army a = World.getInstance().getMainGUI().pickerGUI.armyGUI.getDisplayed();
		if (a != null) {
			a.reinf = reinf;
			a.reinforcements = 0;
			for (int i = 0; i < reinf.length; ++i) {
				a.reinforcements += reinf[i];
			}
			
			a.getOwner().honor = ownerHonor;
			a.getOwner().gold.value = ownerGold;
			a.maint = maint;
			a.raisedTime = raisedTime;
		}
	}
}
