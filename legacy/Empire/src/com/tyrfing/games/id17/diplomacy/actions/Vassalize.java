package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class Vassalize extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7185588600658924734L;
	public static final int ID = 24;
	public static final int FAVOR = 500;
	public static final int MIN_PROTECTION_YEARS = 10;
	
	public Vassalize() {
		super("Vassalize", ID, 0, false);
		disabledText = "UNINITIALIZED";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		sender.getHouseStat(receiver, House.FAVOR_STAT) >= FAVOR
				&&	sender.isProtector(receiver)
				&&	sender.getStatModifier("PROTECTS").getDuration() >= MIN_PROTECTION_YEARS * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY
				&& 	sender.hasMarriage(receiver);
	}

	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = 	"They become our vassal family.\n" +
						"Once offered, they cannot decline.\n\n";
		int favor = (int) sender.getHouseStat(receiver, House.FAVOR_STAT);
		disabledText += Util.getFlaggedText("(1) ", favor >= FAVOR) + " Favor >= " + FAVOR + " (" + favor + ")\n";
		boolean isProtector = sender.isProtector(receiver);
		int protectYears = !isProtector ? 0 : (int) (sender.getStatModifier("PROTECTS").getDuration() / (World.DAYS_PER_YEAR * World.SECONDS_PER_DAY));
		disabledText += Util.getFlaggedText("(2)", isProtector && protectYears >= MIN_PROTECTION_YEARS) + " We protect them for at least " + MIN_PROTECTION_YEARS + " (" + protectYears + ") Years\n";
		disabledText += Util.getFlaggedText("(3) ", sender.hasMarriage(receiver)) + " Marriage";
	}	

	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		sender.changeFavor(receiver, -FAVOR);
		sender.addSubHouse(receiver);
		sender.updateFamily();
		receiver.updateFamily();
	}

	@Override
	public void respond(Message message) {
		super.respond(message);
		execute(message.sender, message.receiver, message.options);
	}

	@Override
	public Mail getSendMail(Message message) {
		String msg = "House of " + message.sender.getLinkedName() + " offers us\n" +
					 "to become their vassal family.\n" +
					 "We owe them too much favor to decline.";
		Mail mail = new DiploYesNoMail("Vassalization Offer", msg, message);
		mail.setIconName("Dynasty");
		return mail;
	}
}
