package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.RebelArmy;

public class SupportRevolt extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9223365229278504561L;

	public final static int ID = 20;
	
	public final RebelArmy army;
	
	public static final float EXTRA_UNITS = 0.4f;
	public static final float PRICE = 3;
	
	public SupportRevolt(RebelArmy army) {
		super("Support Revolt", ID, 1, true);
		
		this.army = army;
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		receiver.changeGold(-getCosts());
		int extra = (int) (army.getTotalTroops() * EXTRA_UNITS);
		army.changeTroops(0, extra);
		army.getRegiment(0).maxTroops += extra;
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} 
	}
	
	@Override
	public Mail getSendMail(Message message) {
		int extra = (int) (army.getTotalTroops() * EXTRA_UNITS);
		HeaderedMail mail = new DiploYesNoMail("The <#009030>" + army.getTotalTroops() + "\\# man strong\n" + army.toString() + " have risen up!", 
											   "We can hire <#009030>" + extra + "\\# mercenaries to\nsupport them. This would cost us\n<#ff0000>" + getCosts() + "\\# gold.", 
											   message);
		mail.setIconName("Revolt");
		return mail;
	}
	
	public int getCosts() {
		return (int) (army.getTotalTroops() * EXTRA_UNITS * PRICE);
	}

}
