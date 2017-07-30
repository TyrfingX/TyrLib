package com.tyrfing.games.id17.houses.reputation;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.reputation.Reputation.ReputationType;

public class ReputationSet {
	public static final Reputation[] reputations = new Reputation[] {
		new Reputation("Honorable", House.HONORABLE, House.DIPLOMATIC_REPUTATION, 20, ReputationType.GOOD),
		new Reputation("Intellectual", House.PROGRESS, House.RESEARCH_MULT, 0.25f, ReputationType.GOOD),
		new Reputation("Wealthy", House.WEALTH, House.INCOME_MULT, 0.25f, ReputationType.GOOD),
		//new Reputation("Builder", House.CONSTRUCTION, House.PROD_COST_MULT, -0.3f, ReputationType.GOOD),
		new Reputation("Warmongerer", House.WARMONGERER, House.DIPLOMATIC_REPUTATION, -40, ReputationType.BAD),
		new Reputation("Tyrant", House.TYRANNY, House.SCORE_MULT, 2f, ReputationType.BAD),
	};
}
