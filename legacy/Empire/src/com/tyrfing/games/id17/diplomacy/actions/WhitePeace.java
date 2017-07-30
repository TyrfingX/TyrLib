package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.WarMail;
import com.tyrfing.games.id17.gui.mails.WarMail.WarMailType;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;

public class WhitePeace extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5988944305348455912L;

	public WhitePeace() {
		super("White Peace", 14, 1, false);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		War war = sender.isEnemy(receiver);
		if (war != null) {
			if (sender == war.attackers.get(0) || sender == war.defenders.get(0)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		sender.isEnemy(receiver).end();
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			if (isEnabled(message.sender, message.receiver)) {
				execute(message.sender, message.receiver, message.options);
			}
		}
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		return new WarMail(WarMailType.WHITE_PEACE_SEND, message);
	}

	@Override
	public Mail getSendMail(Message message) {
		return new DiploYesNoMail("White Peace Offer", "It seems our enemy has grown tired\nof this war and has made an offer\nfor white peace.\n\nShould we accept?", message);
	}


}
