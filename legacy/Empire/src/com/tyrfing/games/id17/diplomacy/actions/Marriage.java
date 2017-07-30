package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.MarriageMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class Marriage extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3402890370949273727L;
	public static final float BRIDE_PRICE_PERCENT = 3;
	public static final int RELATION_MARRIAGE = 30;
	public static final float RELATION_DURATION = 20 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public static final float RELATION_REFUSE = -10;
	public static final float RELATION_REFUSE_DURATION = 10 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;;
	
	public static final int COURT_POWER = 10;
	
	public static final int ID = 7;
	
	public Marriage() {
		super("Marriage", ID, 1, true);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.getHouseStat(receiver, House.HAS_MARRIAGE) == 0 && sender.isEnemy(receiver) == null;
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Increases fertility and relations.\n" + Util.getFlaggedText(Util.getSignedText(5), true) + " Influence\n\n";
		if (sender.getHouseStat(receiver, House.HAS_MARRIAGE) == 0 ) {
			disabledText += Util.getFlaggedText(" (1)", receiver.getHouseStat(sender, House.HAS_TRUCE) == 0 ) + " No ongoing marriage ";
		} else {
			disabledText += " (1) Marriage \n";
			StatModifier sm = receiver.getStatModifier("HAS_MARRIAGE", sender);
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}	

	@Override
	public void execute(House sender, House receiver, int[] options) {
		if (!isEnabled(sender, receiver)) return;
		
		sender.startMarriage(receiver);
		receiver.startMarriage(sender);

		int price = 0;
		
		price = options[1];
		
		if (options[0] > 0) {
			if (sender.getFemales() <= 0 || receiver.getMales() <= 0) return;
			// Female -> receiver family
			sender.changeFemales(-1);
			receiver.changeMales(-1);
			
			sender.addStatModifier(new StatModifier("MARRIAGE_POWER", House.COURT_POWER, sender, receiver, COURT_POWER, 1));
			
		} else {
			if (sender.getMales() <= 0 || receiver.getFemales() <= 0) return;
			sender.changeMales(-1);
			receiver.changeFemales(-1);
			
			receiver.addStatModifier(new StatModifier("MARRIAGE_POWER", House.COURT_POWER, receiver, sender, COURT_POWER, 1));
		}
		
		sender.changeGold(-price);
		receiver.changeGold(price);
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} else {
			message.sender.addStatModifier(new StatModifier("Refused Marriage", House.RELATION_STAT, message.sender, message.receiver, RELATION_REFUSE_DURATION, RELATION_REFUSE));
		}
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		return new MarriageMail(MarriageMail.MailType.MARRIAGE_OFFER_SEND, new Message(this, sender, receiver, null));
	}
	
	public static int getPrice(House receiver) {
		return (int) (receiver.getIncome() * BRIDE_PRICE_PERCENT);
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		return new MarriageMail(MarriageMail.MailType.MARRIAGE_OFFER_RESPONSE, message);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new MarriageMail(MarriageMail.MailType.MARRIAGE_OFFER_RECEIVED, message);
	}

}
