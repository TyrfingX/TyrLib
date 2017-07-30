package com.tyrfing.games.id17.laws;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.TaxUnrest;

public class EconomyCategory extends LawCategory {
	
	public static final UnrestSource[] HIGH_TAXES = new UnrestSource[] {
		new TaxUnrest(0.0002f),
		new TaxUnrest(0.0008f),
		new TaxUnrest(0.0032f),
	};
	
	public EconomyCategory() {
		super("Economy");
		laws = new Law[] {
				new Law("Population Taxes", 
						"Impacts locational attractivity of our\nholdings and unrest.",
						new int[] { House.POP_TAXES },
						new LawOption[] {
						new LawOption("None", " No Taxes ", new float[] { 0 }),
						new LawOption(
							"Low", 
							Util.getFlaggedText("+10%", true) + " Tax Income \n" +
							Util.getFlaggedText("+" + revolRiskTaxesPop(0) + "%", false) + " Unrest per 1k Pop", 
							new float[] { 0.1f }
						),
						new LawOption(
							"Medium", 
							Util.getFlaggedText("+30%", true) + " Tax Income  \n" +
							Util.getFlaggedText("+" + revolRiskTaxesPop(1) + "%", false) + " Unrest per 1k Pop",
							new float[] { 0.3f }
						),
						new LawOption(
							"High", 
							Util.getFlaggedText("+80%", true) + " Tax Income \n" + 
							Util.getFlaggedText("+" + revolRiskTaxesPop(2) + "%", false) + " Unrest per 1k Pop\n" + 
							Util.getFlaggedText("+10", false) + " Tyranny",
							new float[] { 0.8f }, true
						),
				}, 
				1, false),
				new Law("Trade Taxes", 
						"Impacts locational attractivity of our\nholdings for traders.",
						new int[] { House.TRADE_TAXES },
						new LawOption[] {
						new LawOption("None", " No taxes on trade goods ", new float[] { 0 }),
						new LawOption(
								"Low", 
								Util.getFlaggedText("+10%", true) + " Tax Income \n" +
								Util.getFlaggedText("+" + revolRiskTaxesPop(0) + "%", false) + " Unrest per 1k Pop", 
								new float[] { 0.1f }
							),
							new LawOption(
								"Medium", 
								Util.getFlaggedText("+20%", true) + " Tax Income  \n" +
								Util.getFlaggedText("+" + revolRiskTaxesPop(1) + "%", false) + " Unrest per 1k Pop",
								new float[] { 0.2f }
							),
							new LawOption(
								"High", 
								Util.getFlaggedText("+30%", true) + " Tax Income \n" + 
								Util.getFlaggedText("+" + revolRiskTaxesPop(2) + "%", false) + " Unrest per 1k Pop\n" + 
								Util.getFlaggedText("+10", false) + " Tyranny" ,
								new float[] { 0.3f }, true
							),
				}, 
				0, false),
		};
	}
	
	private float revolRiskTaxesPop(int setting) {
		float revoltees = HIGH_TAXES[setting].strength * 1000;
		return ((int)(HIGH_TAXES[setting].probability * revoltees*1000))/(10.f);
	}
}
