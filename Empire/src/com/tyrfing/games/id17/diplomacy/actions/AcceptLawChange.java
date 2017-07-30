package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.Law;
import com.tyrfing.games.id17.laws.LawSet;

/**
 * OPTIONS:
 * 0: LAW ID
 * 1: LAW CATEGORY
 * 2: CATEGORY INTERNAL LAW ID
 * @author Media
 *
 */

public class AcceptLawChange extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2839903331460427962L;
	public static final int ID = 17;
	
	public AcceptLawChange() {
		super("Accept Law", ID, 0, false);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		receiver.changeHouseStat(sender, House.FAVOR_STAT, Law.FAVOR);
		sender.changeHouseStat(receiver, House.FAVOR_STAT, -Law.FAVOR);
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		execute(message.sender, message.receiver, message.options);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		Law law = LawSet.getLaw(message.options[0]);
		return new HeaderedMail(  "Law Change:\n" + law.name, 
								  "Our Lord Family, House " + message.sender.getLinkedName() + ",\n" +
								  "changed the law " + law.name + "\n" + 
								  "to " + law.options[message.sender.getLawSetting(message.options[0])].optionName + ". We gain\n" + 
								  "<#009030>20\\# favor for putting up with their whims.", message.sender, message.receiver);
	}	

}
