package com.tyrfing.games.id17.names;

public class HouseNameGen extends NameGen {
	private final static String[] syllabies = new String[] {
		"ta", "oak", "shield", "flo", "rence", "fay", "lin", "sa", "meria",
		"star", "dream", "sin", "dra", "king", "stone", "gold", "hen", "selt",
		"ky", "lan", "zer", "snow", "born", "sol", "ray", "crown", "well", "night", "fall",
		"tho", "rius", "fal", "conia", "tri", "sia", "auro", "eas", "elier", "con",
		"nister",  "fang", "nir", "rose", "garden", "kin", "sora", "ai", "ren",
	};

	private final static String[] prefixes = new String[] {
	};

	public HouseNameGen() {
		super(prefixes, syllabies, 2, 2);
	}
}
