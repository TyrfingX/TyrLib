package com.tyrfing.games.id17.laws;

public class LawSet {
	
	public static final int ECONOMONY = 0;
	
	public static LawCategory[] categories;
	
	public static void init() {
		Law.COUNT_LAWS = 0;
		categories =  new LawCategory[] { new EconomyCategory(),
										  new VasallCategory(),
										  new ArmyCategory()
		};
	}
	
	public static Law getLaw(int ID) {
		
		for (int i = 0; i < categories.length; ++i) {
			if (ID < categories[i].laws.length) {
				return categories[i].laws[ID];
			} else {
				ID -= categories[i].laws.length;
			}
		}
		
		return null;
	}
}
