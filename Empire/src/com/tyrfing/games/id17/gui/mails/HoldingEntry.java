package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.war.BuildUnitGUI;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class HoldingEntry extends DefaultItemListEntry{

	public static final ScaledVector1 SIZE_X = new ScaledVector1(HeaderedMail.COLUMN_SIZE.x, ScaleDirection.X, 1);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(BuildUnitGUI.SIZE_Y.x, ScaleDirection.Y, 0);
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.02f, 0.0f, 1);
	
	protected HeaderedMail mail;
	public final Holding holding;
	protected boolean right;
	public final Label nameLabel;
	
	public HoldingEntry(Holding holding, HeaderedMail mail) {
		this(holding, mail, true);
	}
	
	public HoldingEntry(Holding holding, HeaderedMail mail, boolean right) {
		super(holding.getFullName(), new Vector2(SIZE_X.get(), SIZE_Y.get()), "");
		
		this.mail = mail;
		this.holding = holding;
		
		nameLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/DECLARE_WAR_FOR_HOLDING/" + holding.getFullName() + "/NAME", NAME_LABEL, "- " + holding.getName());
		nameLabel.setColor(Color.BLACK);
		nameLabel.setInheritsAlpha(true);
		addChild(nameLabel);
		
		setEnabled(true);
		
		this.right = right;
	}

	@Override
	protected void onClick() {
		if (right) {
			mail.unhighlightRightColumn();
		} else {
			mail.unhighlightLeftColumn();
		}
		
		highlight();
		
		if (right) {
			mail.selectRight(holding.getHoldingID());
		} else {
			mail.selectLeft(holding.getHoldingID());
		}
	}

}
