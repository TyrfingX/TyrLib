package com.tyrfing.games.id17.names;


public class HoldingNameGen extends NameGen {
	private final static String[] syllabies = new String[] {
			"fay", "zan", "ray", "ash", "en", "tri",
			"rock", "long", "len", "vi", "pla",
			"so", "lu", "ci", "fel", "cro", "ven", "ra",
			"ar", "lo", "ri", "pa", "lom", "la", "dea", "land", "worth", "lion",
			"castle", "gri", "sia", "ever", "wo", "nae", "kai", "sar"
	};
	
	private final static String[] prefixes = new String[] {
			"ark "
	};
	
	public HoldingNameGen() {
		super(prefixes, syllabies, 2, 4);
	}
}
