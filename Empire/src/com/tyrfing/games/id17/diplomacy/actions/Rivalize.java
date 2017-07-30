package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;

public class Rivalize extends DiploAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4695519849370616986L;
	public static final int ID = 25;
	private static final float MAX_DIFF = 0.2f;
	
	public static final int RELATION_HIT = -100;
	
	public Rivalize() {
		super("Rivalize", ID, 0, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 	   sender.getCountWars() == 0
				&& receiver.getCountWars() == 0
				&& sender.isIndependend()
				&& receiver.isIndependend()
				&& sender.getRival() == null
				&& validRealmSize(sender, receiver);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Initiates rivalry.\nPlot speed " + Util.getFlaggedText("+25%", true) + " but relations " +  Util.getFlaggedText(RELATION_HIT, false) + "\n\n";
		House rival = sender.getRival();
		if (rival != null) {
			disabledText += "We are already rivalizing " + rival.getLinkedName();
		}	else {
			disabledText += Util.getFlaggedText(" (1)", true ) + " No current rival \n";
			disabledText += Util.getFlaggedText(" (2)", sender.isIndependend() && receiver.isIndependend() &&  sender.getCountWars() == 0 && receiver.getCountWars() == 0 ) + " Both independend and not at war \n";
			disabledText += Util.getFlaggedText(" (3)",  validRealmSize(sender, receiver) ) + " Our realm size (" + sender.getAllHoldings().size() + ") is similar to theirs (" + receiver.getAllHoldings().size() + ")";
		}
	}
	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		if (sender.getRival() != null) return;
		sender.setRival(receiver);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		String msg = "House of " + message.sender.getLinkedName() + " considers us a rival!\nWe have to be wary of their plottings.";
		return new HeaderedMail("New Rival", msg, message.sender, message.receiver);
	}
	
	public static boolean validRealmSize(House sender, House receiver) {
		int countSenderHoldings = sender.getAllHoldings().size();
		int countReceiverHoldings = receiver.getAllHoldings().size();
		return Math.abs(countSenderHoldings - countReceiverHoldings) / sender.getAllHoldings().size() <= MAX_DIFF;
	}

}
