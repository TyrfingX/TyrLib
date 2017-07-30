package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class TechEntry extends DefaultItemListEntry{
	
	private HeaderedMail mail;
	private int techIndex;
	
	public TechEntry(int index, HeaderedMail mail) {
		super("TechEntry"+Math.random(), new Vector2(HoldingEntry.SIZE_X.get(), HoldingEntry.SIZE_Y.get()), "");
		
		Technology t = World.getInstance().techTreeSet.trees[0].techs[index];
		
		this.mail = mail;
		this.techIndex = index;
		
		Label nameLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/TECH/" + index + "/NAME", JustificationEntry.NAME_LABEL, t.name);
		nameLabel.setColor(Color.BLACK);
		nameLabel.setInheritsAlpha(true);
		addChild(nameLabel);
		
		Window sigil = WindowManager.getInstance().createImageBox(	this.getName() + "/TECH_ICON", 
																	JustificationEntry.SIGIL_POS, 
																	"TECH", t.name, JustificationEntry.SIGIL_SIZE);
		addChild(sigil);
		
		setEnabled(true);
	}

	@Override
	protected void onClick() {
		mail.unhighlightRightColumn();
		highlight();
		mail.selectRight(techIndex);
	}

}
