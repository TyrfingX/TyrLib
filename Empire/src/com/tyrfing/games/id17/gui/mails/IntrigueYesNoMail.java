package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.world.World;

public class IntrigueYesNoMail extends YesNoMail {
	private IntrigueProject project;
	
	public IntrigueYesNoMail(String title, String text, IntrigueProject project, House sender, House receiver) {
		super(title, text, sender, receiver, World.getInstance().getWorldTime());
		this.project = project;
		this.iconName = "Intrigue";
	}
	
	@Override
	protected void onAccept() {
		responded = true;
		project.acceptInvite(receiver);
		this.remove();
	}
	
	@Override
	protected void onReject() {
		responded = true;
		project.rejectInvite(receiver);
		this.remove();
	}
}
