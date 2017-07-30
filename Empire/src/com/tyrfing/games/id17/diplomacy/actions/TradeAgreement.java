package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.world.World;

public class TradeAgreement extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5479113465139140620L;
	public static final int MIN_RELATIONS = 40;
	public static final int MAX_RELATION_BOOST = 40;
	public static final float RELATION_BOOST_SPEED = 0.02f;
	
	public static final int ID = 13;
	
	public TradeAgreement() {
		super("Trade Agreement", ID, 2, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		receiver.getRelation(sender) >= MIN_RELATIONS 
				&& 	receiver.getHouseStat(sender, House.HAS_TRADE_AGREEMENT) == 0
				&&  sender.isIndependend()
				&&  receiver.isIndependend()
				&&  sender.isEnemy(receiver) == null
				&&  (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 1 || receiver.getHouseStat(sender, House.HAS_DIPLOMAT) == 1);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Trade Goods can pass between borders.\n" + Util.getFlaggedText(Util.getSignedText(5), true) + " Influence\n\n";
		if (receiver.getHouseStat(sender, House.HAS_TRADE_AGREEMENT) != 1 ) {
			disabledText += Util.getFlaggedText(" (1)", receiver.getRelation(sender) >= MIN_RELATIONS ) + " Relations > " + MIN_RELATIONS + " (" + (int)receiver.getRelation(sender) + ") \n";
			boolean diplomat = (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 1 || receiver.getHouseStat(sender, House.HAS_DIPLOMAT) == 1);
			disabledText += Util.getFlaggedText(" (2)", diplomat ) + " Diplomat at either court ";
		} else {
			disabledText += " (1) In effect until diplomatic relations end \n";
			StatModifier sm = receiver.getStatModifier("Diplomat", sender);
			if (sm == null) {
				sm = sender.getStatModifier("Diplomat", receiver);
			}
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}	

	@Override
	public void execute(House sender, House receiver, int[] options) {
		
		sender.addStatModifier(new StatModifier("HAS_TRADE_AGREEMENT", House.HAS_TRADE_AGREEMENT, sender, receiver, -1, 1));
		receiver.addStatModifier(new StatModifier("HAS_TRADE_AGREEMENT", House.HAS_TRADE_AGREEMENT, receiver,  sender, -1, 1));
		
		receiver.addStatModifier(new VaryingStatModifier("Trade Agreement", House.RELATION_STAT, receiver, sender, -1, 0, RELATION_BOOST_SPEED, MAX_RELATION_BOOST));
		sender.addStatModifier(new VaryingStatModifier("Trade Agreement", House.RELATION_STAT, sender, receiver, -1, 0, RELATION_BOOST_SPEED, MAX_RELATION_BOOST));
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} 
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		String msg = "";
		if (message.response > 0) {
			msg = "House of " + message.receiver.getLinkedName() + " has accepted\nour offer for a trade agreement.\n\nThe agreement will be upheld as\nlong as we maintain diplomatic relations.";
		} else {
			msg = "Fools! They have refused our offer\nto establish a trade agreement.";
		}
		Mail mail = new HeaderedMail("Trade Agreement", msg, message.sender, message.receiver);
		mail.setIconName("Economy");
		return mail;
	}

	@Override
	public Mail getSendMail(Message message) {
		String msg = "We have received an offer for a\ntrade agreement with\nHouse of " + message.sender.getLinkedName() + ".";
		Mail mail = new DiploYesNoMail("Trade Agreement", msg, message);
		mail.setIconName("Economy");
		return mail;
	}

}
