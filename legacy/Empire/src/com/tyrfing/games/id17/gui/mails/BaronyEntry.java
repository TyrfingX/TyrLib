package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.input.InputManager;

public class BaronyEntry extends HoldingEntry {	
	private Barony barony;
	
	private boolean addedSubHoldings;
	
	public BaronyEntry(Barony barony, HeaderedMail mail) {
		super(barony, mail);
		
		this.barony = barony;
		
		nameLabel.setText(holding.getName());
	}

	@Override
	protected void onClick() {
		
		mail.unhighlightRightColumn();
		highlight();
		mail.selectRight(barony.getHoldingID());
		
		addSubHoldings();
	}
	
	public void addSubHoldings() {
		ItemList holdings = mail.rightColumn;
		
		if (!addedSubHoldings) {
			for (int i = 1; i < barony.getCountSubHoldings(); ++i) {
				Holding holding = barony.getSubHolding(i);
				if (holding.getOwner().haveSameOverlordWith(barony.getOwner())) {
					HoldingEntry entry = new HoldingEntry(holding, mail);
					holdings.addItemListEntry(entry, position);
					entry.setReceiveTouchEvents(true);
				}
			}
			
			addedSubHoldings = true;
		} 
		
		holdings.correctOffset();
		InputManager.getInstance().sort();
	}



}
