package com.tyrfing.games.id17.houses.reputation;

import java.io.Serializable;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.houses.House;

public class Reputation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5755308121382482097L;

	public enum ReputationType {
		GOOD, NEUTRAL, BAD;
	}
	
	public final static int MAX_POINTS = 100;
	public final static int MIN_POINTS = 50;
	
	public final String name;
	public final int houseStatNeed;
	public final int houseStatAffect;
	public final float value;
	public final ReputationType type;
	
	public Reputation(String name, int houseStatNeed, int houseStatAffect, float value, ReputationType type) {
		this.name = name;
		this.houseStatNeed = houseStatNeed;
		this.houseStatAffect = houseStatAffect;
		this.value = value;
		this.type = type;
	}
	
	public void onGain(House house) {
		house.stats[houseStatAffect] += value;
	}
	
	public void OnLoss(House house) {
		house.stats[houseStatAffect] -= value;
	}

	public String getTooltip(House house) {
		int displayValue = (int) value;
		String suffix = "";
		
		if (Math.abs(value) <= 5) {
			displayValue = (int) (value * 100);
			suffix = "%";
		}
		
		String tooltip = 	name + " " + Math.round(house.stats[houseStatNeed]) + "/100\n"
				+	house.getAffectorString(houseStatNeed) + "\n"
				+ 	Util.getFlaggedText(House.STAT_NAMES[houseStatAffect] + " " + Util.getSignedText(displayValue) + suffix, value >= 0);
		
		if (name.equals("Tyrant")) {
			tooltip += "\n" + Util.getFlaggedText("Neighbours gain Liberation Justification", false);
		}
		
		return 	tooltip;
	}
}
