package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.gui.war.BuildUnitGUI;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class JustificationEntry extends DefaultItemListEntry {

	public static final ScaledVector1 SIZE_X = new ScaledVector1(HeaderedMail.COLUMN_SIZE.x, ScaleDirection.X, 1);
	public static final ScaledVector1 SIZE_Y = new ScaledVector1(BuildUnitGUI.SIZE_Y.x, ScaleDirection.Y, 0);
	public static final ScaledVector2 NAME_LABEL = new ScaledVector2(0.04f, 0.01f, 1);
	public static final ScaledVector2 SIGIL_POS = new ScaledVector2(0.01f, 0.0225f, 0);
	public static final ScaledVector2 SIGIL_SIZE = TabGUI.SIGIL_SIZE.multiply(0.4f);
	public static final ScaledVector2 HONOR_POS = new ScaledVector2(SIZE_X.x - SIGIL_SIZE.x - SIGIL_POS.x, SIGIL_POS.y, 1);
	public static final ScaledVector2 HONOR_LABEL_POS = new ScaledVector2(HONOR_POS.x - 0.044f, NAME_LABEL.y, 1);
	
	private WarMail mail;
	private WarJustification justification;
	
	public JustificationEntry(WarJustification justification, WarMail mail) {
		super(justification.toString() + Math.random(), new Vector2(SIZE_X.get(), SIZE_Y.get()), "");
		
		this.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());	
		this.mail = mail;
		this.justification = justification;
		
		String name = "DECLARE_WAR_JUSTIFICATION/" + justification.toString() + Math.random();
		
		Label nameLabel = (Label) WindowManager.getInstance().createLabel(name + "/NAME", NAME_LABEL, justification.toString());
		nameLabel.setColor(Color.BLACK);
		nameLabel.setInheritsAlpha(true);
		addChild(nameLabel);
		
		int honorCost = justification.getHonor(mail.getMessage().sender, mail.getMessage().receiver);
		
		if (justification.goal == WarGoal.LIBERATION) {
			honorCost += 100;
		}
		
		if (justification.getClaim() != null) {
			Window sigil = WindowManager.getInstance().createImageBox(name + "/CLAIMANT", SIGIL_POS, "SIGILS1", justification.getClaim().getSigilName(), SIGIL_SIZE);
			addChild(sigil);
		}
		
		if (honorCost != 0) {
			Label honorLabel = (Label) WindowManager.getInstance().createLabel(name + "/HONOR_LABEL", HONOR_LABEL_POS, Util.getFlaggedText(Util.getSignedText(honorCost), honorCost >= 0)+ "<img MAIN_GUI HONOR_ICON>");
			honorLabel.setInheritsAlpha(true);
			addChild(honorLabel);
		}
				
		setEnabled(true);
	}

	@Override
	protected void onClick() {
		mail.unhighlightLeftColumn();
		highlight();
		
		if (justification.getClaim() != null) {
			mail.selectLeft(justification.getClaim().getIndexOf(justification));
		} else {
			mail.selectLeft(WarGoal.NO_REASON);
		}
	}
}
