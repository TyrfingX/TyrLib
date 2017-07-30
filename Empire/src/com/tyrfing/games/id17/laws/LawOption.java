package com.tyrfing.games.id17.laws;

import com.tyrfing.games.id17.houses.House;

public class LawOption {
	public final String optionName;
	public final float[] values;
	protected Law law;
	protected int ID;
	public final String tooltip;
	public final boolean isTyrannical;
	
	public LawOption(String optionName, String tooltip, float[] values) {
		this.optionName = optionName;
		this.values = values;
		this.tooltip = tooltip;
		this.isTyrannical = false;
	}
	
	public LawOption(String optionName, String tooltip, float[] values, boolean isTyrannical) {
		this.optionName = optionName;
		this.values = values;
		this.tooltip = tooltip;
		this.isTyrannical = isTyrannical;
	}
	
	
	public void select(House house) {
		for (int i = 0; i < law.stats.length; ++i)  {
			house.stats[law.stats[i]] += values[i];
		}
		house.setLawSetting(law.ID, ID);
	}
	
	public void unselect(House house){
		for (int i = 0; i < law.stats.length; ++i)  {
			house.stats[law.stats[i]] -= values[i];
		}
	}
}
