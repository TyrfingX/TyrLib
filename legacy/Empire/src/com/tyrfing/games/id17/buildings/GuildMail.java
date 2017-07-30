package com.tyrfing.games.id17.buildings;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.gui.holding.ProductionGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.HoldingEntry;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;

public class GuildMail extends HeaderedMail {

	private Guild.TYPE selection;
	private Barony target;
	private Guild g;
	private Holding h;
	private List<Window> producedGoods = new ArrayList<Window>();
	
	public GuildMail(Guild g, Holding h) {
		super("Settle Guild", h.getOwner(), h.getOwner(), false);
		this.addRightColumnList("Guild");
		this.addLeftColumnList("");
		
		this.g = g;
		this.h = h;
		
		if (g.type == Guild.TYPE.Merchants) {
			target = h.getTradeNeighbour(0);
		}
		
		Guild.TYPE[] types =  Guild.TYPE.values();
		for (int i = 0; i < types.length; ++i) {
			Guild.TYPE type = types[i];
			GuildEntry entry = new GuildEntry(type, this);
			this.rightColumn.addItemListEntry(entry);
			entry.setReceiveTouchEvents(true);
			
			 if ((i == 0 && g.type == null) || (g.type  == type)) {
				 entry.highlight();
				 select(type);
			 }
		}
		
		this.addAcceptButton();
	}
	
	public void select(Guild.TYPE type) {
		
		for (int i = 0; i < producedGoods.size(); ++i) {
			WindowManager.getInstance().destroyWindow(producedGoods.get(i));
		}
		
		producedGoods.clear();
		
		 for (int i = 0; i < leftColumn.getCountEntries(); ++i) {
			 WindowManager.getInstance().destroyWindow(leftColumn.getEntry(i));
		 }
		 
		 leftColumn.clear();
		
		if (type == Guild.TYPE.Merchants) {
			 this.titleLabelLeft.setText("Export goods to:");
			 
			 List<Holding> holdings = h.getOwner().getHoldings();
			 final HeaderedMail mail = this;
			 
			 for (int i = 0; i < holdings.size(); ++i) {
				 if (holdings.get(i) instanceof Barony && holdings.get(i) != h) {
					 final Holding holding = holdings.get(i);
					 HoldingEntry entry = new HoldingEntry(holdings.get(i), this) {
							@Override
							protected void onClick() {
								mail.unhighlightLeftColumn();
								highlight();
								target = (Barony) holding;
							} 
					 };
					 leftColumn.addItemListEntry(entry);
					 entry.setReceiveTouchEvents(true);
					 
					 if ((i == 0 && target == null) || target == holdings.get(i)) {
						 entry.highlight();
						 target = (Barony) holdings.get(i);
					 }
				 }
			 }
			
		} else {
			this.titleLabelLeft.setText("Production");
			GoodProduction p = GoodProduction.createProduction(Guild.DATA.get(type));
			ProductionGUI.createGoodProductionGUI(0, p, this.getName(), left, producedGoods, ProductionGUI.PROD_BASE_POS.get(), h);
		}
		
		selection = type;
	}
	
	@Override
	public void onAccept() {
		remove();
		g.setupGuild(selection, target, h);
	}


}
