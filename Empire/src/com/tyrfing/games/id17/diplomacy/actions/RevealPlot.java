package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

public class RevealPlot extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7786612653542224175L;
	public static final float BETRAYED_RELATION = -50;
	public static final float MAX_HELPED_RELATION = 40;
	public static final float HELPED_RELATION = 20;
	public static final float DURATION_HELPFED = SendGift.RELATION_DURATION;
	public static final float DURATION_BETRAYED = DeclareWar.RELATION_DECLARE_WAR_DURATION;
	public static final float HONOR_COST = -10;
	
	public static final int ID = 15;
	
	public RevealPlot() {
		super("Reveal Plot", ID, 0, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.intrigueProject != null && sender.intrigueProject.receiver == receiver && sender.intrigueProject.supporters.get(0) != sender;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		sender.intrigueProject.reveal();
		
		sender.changeHonor(HONOR_COST);
		float relation = receiver.getModifierValue("Showed plot", sender);
		relation = Math.min(HELPED_RELATION, MAX_HELPED_RELATION - relation);
			
		receiver.addStatModifier(new StatModifier("Showed plot", House.RELATION_STAT, receiver, sender, DURATION_HELPFED, relation ));
		House initiator = sender.intrigueProject.supporters.get(0);
		initiator.addStatModifier(new StatModifier("Revealed plot", House.RELATION_STAT, initiator, sender, DURATION_BETRAYED, BETRAYED_RELATION ));
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (initiator == World.getInstance().getPlayerController().getHouse()) {
				Mail mail = new HeaderedMail("Plot revealed!", "\"My Lord, the traitorus House " +  sender.getLinkedName() + "\nhas betrayed our trust and revealed\nour plot to House " + receiver.getName() + ".\n\n", sender, initiator);
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
		
		sender.intrigueProject.removeSupporter(sender);
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		final RevealPlot request = this;
		House initiator = sender.intrigueProject.supporters.get(0);
		float relation = (int) receiver.getModifierValue("Showed plot", sender);
		relation = (int) Math.min(HELPED_RELATION, MAX_HELPED_RELATION - relation);
		HeaderedMail mail = new HeaderedMail(	"Intrigue: Reveal plot", 
												"We would betray House " + initiator.getLinkedName() + " and\n" + 
												"reveal their plot to the " + receiver.getLinkedName() + ".\n" + 
												"Our relations would change as follows:\n" + 
												receiver.getName() + ": <#009030>+" + relation + "\\# for 5 years.\n" + 
												initiator.getName() + ": <#ff0000>" + BETRAYED_RELATION + "\\# for 100 years\n"+ 
												"This action is considered dishonerable.", sender, receiver) {
			@Override 
			public void onAccept() { 
				Message message = new Message(request, sender, receiver, null);
				message.action.send(message.sender, message.receiver, null);
				remove();
			}
		};
		mail.addAcceptButton();
		return mail;
	}
	
	@Override
	public Mail getSendMail(Message message) {
		String msg = "House " + message.sender.getLinkedName() + " has revealed that the\nfilthy " + message.sender.intrigueProject.supporters.get(0).getName() + " are plotting against us:\n" + message.sender.intrigueProject.toString();
		return new HeaderedMail("Intrigue: Plot shown", msg, message.sender, message.receiver);
	}

}
