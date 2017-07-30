package com.tyrfing.games.id17.buildings;

import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.mails.JustificationEntry;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class GuildEntry extends DefaultItemListEntry {
	
	private Label nameLabel;
	private Guild.TYPE type;
	private GuildMail m;
	
	public GuildEntry(Guild.TYPE type, GuildMail m) {
		super("GuildList/" + type.toString() + "/" + Math.random(), 
		new Vector2(new Vector2(JustificationEntry.SIZE_X.get(), JustificationEntry.SIZE_Y.get())), "");
		
		this.type = type;
		this.m = m;
		
		nameLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/NAME", JustificationEntry.NAME_LABEL, type.toString());
		nameLabel.setColor(Color.BLACK);
		nameLabel.setInheritsAlpha(true);
		addChild(nameLabel);
		
		setEnabled(true);
	}
	
	@Override
	protected void onClick() {
		m.select(type);
		m.unhighlightRightColumn();
		highlight();
	}
}
