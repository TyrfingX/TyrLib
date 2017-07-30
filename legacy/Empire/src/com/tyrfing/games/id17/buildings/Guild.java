package com.tyrfing.games.id17.buildings;

import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.ProdData;
import com.tyrfing.games.id17.world.World;


public class Guild extends Building {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6169145121998168875L;

	public enum TYPE {
		Smiths,
		Bakers,
		Merchants,
	};
	
	public static final Map<Guild.TYPE,ProdData> DATA = new HashMap<Guild.TYPE,ProdData>();
	
	static {

		DATA.put(Guild.TYPE.Smiths, new ProdData(	new String[] 	{ "Iron" },
													new int[]		{ 2 },
													new String[]	{ "Weaponry" },
													new int[] 		{ 3 }) );
		DATA.put(Guild.TYPE.Bakers, new ProdData(	new String[] 	{ "Flour" },
													new int[]		{ 1 },
													new String[]	{ "Bread" },
													new int[] 		{ 3 }) );
	}
	
	public Guild.TYPE type;
	public GoodProduction p;
	public Barony target;
	
	public Guild(int level) {
		super(Building.TYPE.Guild, Building.STATS.get(Building.TYPE.Guild), level);
	}
	
	public void setupGuild(Guild.TYPE type, Barony target, Holding h) {
		
		if (type != null && this.type != Guild.TYPE.Merchants) {
			h.removeProduction(p);
		} else if (this.type == Guild.TYPE.Merchants) {
			h.removeTradeNeighbour(target);
		}
		
		this.type = type;
		
		if (type == Guild.TYPE.Merchants) {
			this.target = target;
			h.addTradeNeighbour(target);
		} else {
			this.p = GoodProduction.createProduction(DATA.get(type), h);
			h.addProductionAdditive(p);
		}
		
		h.getOwner().changeGold(-Building.getPrice(Building.TYPE.Guild, h));
	}
	
	@Override
	public void createBuildMail(Holding h) {
		HeaderedMail m = new GuildMail(this, h);
		World.getInstance().getMainGUI().mailboxGUI.addMail(m, true);
	}
	
	public static boolean isProductive(Guild.TYPE type, Holding h) {
		if (type != Guild.TYPE.Merchants) {
			ProdData p = DATA.get(type);
			for (int i = 0; i < p.in.length; ++i) {
				Good g = h.getGood(p.in[i]);
				if (g == null) {
					return false;
				}
				
				if (g.getQuantity() < p.qIn[i]) {
					return false;
				}
			}
		} 
		
		return true;
	}

}
