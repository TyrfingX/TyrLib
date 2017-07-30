package com.tyrfing.games.id17.laws;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.houses.House;

public class VasallCategory extends LawCategory {

	public VasallCategory() {
		super("Vassal");
		laws = new Law[] {
				new Law("Vassal Taxes", 
						"Impacts relations with branch families.\nThey may not acknowledge increasing\nthis law. If they do,\nwe would owe them <#009030>" +  Law.FAVOR + "\\# favor.",
						new int[] { House.VASSAL_TAXES },
						new LawOption[] {
						new LawOption("None"," No Vassal taxes ", new float[] { 0 }),
						new LawOption("20%", Util.getFlaggedText("+20%", true) + " Vassal Taxes ", new float[] { 0.2f }),
						new LawOption("40%", Util.getFlaggedText("+40%", true) + " Vassal Taxes ", new float[] { 0.4f }),
						new LawOption("80%", Util.getFlaggedText("+80%", true) + " Vassal Taxes ", new float[] { 0.8f }),
				}, 
				0, true),
				new Law("Vassal Troop Support", 
						"Impacts relations with branch families.\nThey may not acknowledge increasing\nthis law. If they do,\nwe would owe them <#009030>" +  Law.FAVOR + "\\# favor.",
						new int[] { House.VASSAL_ARMY },
						new LawOption[] {
						new LawOption("None", " No reinforcement supports from Vassals ", new float[] { 0 }),
						new LawOption("20%", Util.getFlaggedText("+20%", true) + " Vassal Reinforcements support our armies ", new float[] { 0.2f }),
						new LawOption("40%", Util.getFlaggedText("+40%", true) + " Vassal Reinforcements support our armies ", new float[] { 0.4f }),
						new LawOption("80%", Util.getFlaggedText("+80%", true) + " Vassal Reinforcements support our armies ", new float[] { 0.8f }),
				}, 
				0, true),
		};
	}

}
