package com.tyrfing.games.id17.laws;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.houses.House;

public class ArmyCategory extends LawCategory {

	public ArmyCategory() {
		super("Army");
		laws = new Law[] {
				new Law("Conscription", 
						"Equally conscribe soldiers.",
						new int[] { House.CONSCRIPTION },
						new LawOption[] {
						new LawOption("None", "No Conscription", new float[] { 0 }),
						new LawOption("Low", Util.getFlaggedText("+0.05%", true) + " Conscription", new float[] { 0.0005f }),
						new LawOption("Medium", Util.getFlaggedText("+0.1%", true) + " Conscription", new float[] { 0.001f }),
						new LawOption("Max", Util.getFlaggedText("+0.2%", true) + " Conscription", new float[] { 0.002f }),
				}, 
				1, false),
				new Law("Mercenary reinforcements", 
						"Reinforce armies with mercenaries.",
						new int[] { House.MERCENARIES },
						new LawOption[] {
						new LawOption("None", "No Mercenary Support", new float[] { 0 }),
						new LawOption("Low",  Util.getFlaggedText("+25%", true) + " Mercenaries", new float[] { 0.25f }),
						new LawOption("Medium", Util.getFlaggedText("+50%", true) + " Mercenaries", new float[] { 0.5f }),
						new LawOption("High", Util.getFlaggedText("+100%", true) + " Mercenaries", new float[] { 1f }),
				}, 
				0, false),
		};
	}

}
