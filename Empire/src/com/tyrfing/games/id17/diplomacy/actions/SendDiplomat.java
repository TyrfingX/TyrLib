package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.DiplomatStatModifier;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class SendDiplomat extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8688262460370266930L;
	
	public static final int ID = 11;
	
	public SendDiplomat() {
		super("Send Diplomat", ID, 0, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 	   sender != receiver
				&& sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 0 
				&& sender.getMales() > 0 
				&& sender.isIndependend() 
				&& receiver.isIndependend()
				&& sender.isEnemy(receiver) == null;
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Enables establishment of various pacts.\n" + Util.getFlaggedText(Util.getSignedText(5), true) + " Influence\n\n";
		if (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) != 1) {
			disabledText += Util.getFlaggedText(" (1)", sender.getMales() > 0 ) + " We have at least 1 male ";
		}	else {
			disabledText += " (1) Already sent a diplomat \n";
			StatModifier sm = sender.getStatModifier("Diplomat", receiver);
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}
	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		if (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) != 0) return;
		
		sender.changeMales(-1);
		sender.addStatModifier(new DiplomatStatModifier(sender, receiver));
	}
	
	@Override
	public Mail getSendMail(Message message) {
		String msg = "House of " + message.sender.getLinkedName() + " has sent\na diplomat to our court.\n\nWe have become able to establish pacts.";
		return new HeaderedMail("Diplomat arrived", msg, message.sender, message.receiver);
	}


}
