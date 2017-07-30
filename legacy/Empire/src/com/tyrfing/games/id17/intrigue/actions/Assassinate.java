package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;

public class Assassinate extends IntrigueAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3012584422740427814L;
	public static final int MAX_POINTS = 100;
	
	public Assassinate() {
		super("Assassinate");
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		return new HeaderedMail("Intrigue: Assassinate", 
								"Have our spy assassinate a <link=member>house member\\l.\n" +
								"House " + receiver.getLinkedName() + " gets -1<img MAIN_GUI MALE_ICON> or -1<img MAIN_GUI FEMALE_ICON>\n" +
								"but our spy will be lost.\n\n" + 
								"Requires a spy and only works on neighbours\n" + 
								"and realm members.", sender, receiver) {
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
				this.addAcceptButton();
				accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						remove();
						startProject(sender, receiver, new int[] { 0 });
					}
				});
			}
		};
	}

	@Override
	public Mail getSuccessMail(House sender, House receiver, int[] options) {
		Mail mail = new HeaderedMail("Intrigue: Assassinate", "Successfully assassinated a <link=member>house member\\l\n" +
				"of House " + receiver.getLinkedName() + "!\n" + 
				"Sadly, our loyal spy has been\nkilled in the process.", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail = new YesNoMail(	"Intrigue: Assassinate", 
				"We have received an offer from\nhouse of " + 
				intrigueSender.getLinkedName() + " to support their efforts in assassinating\na <link=member>house member\\l of House " + 
				intrigueReceiver.getLinkedName(), intrigueSender, inviteReceiver, World.getInstance().getWorldTime());
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		if (receiver.getMales() > 0 && Math.random() < 0.5f) {
			receiver.changeMales(-1);
		} else if (receiver.getFemales() > 0 && Math.random() < 0.5f) {
			receiver.changeFemales(-1);
		}
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		(receiver.isRealmNeighbour(sender) || receiver.haveSameOverlordWith(sender))
				&&	sender.hasSpy(receiver)
				&&  (receiver.getMales() > 0 || receiver.getFemales() > 0);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText(" (1)", receiver.isRealmNeighbour(sender) || receiver.haveSameOverlordWith(sender) ) + " Bordering realms or same Overlord ";
		disabledText += Util.getFlaggedText("\n (2)", sender.hasSpy(receiver) ) + " Infiltrated with a spy ";
		disabledText += Util.getFlaggedText("\n (3)", receiver.getMales() > 0 || receiver.getFemales() > 0 ) + " Has assassinateable targets ";
	}	


	@Override
	public int getMaxPoints(House sender, House receiver, int[] options) {
		return sender.hasSpy(receiver) ? MAX_POINTS / 2 : MAX_POINTS;
	}

	@Override
	public String toString(House sender, House receiver, int[] options) {
		return "Assassinate random family member of " + receiver.getLinkedName() + ".";
	}
	
	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		return new HeaderedMail("Death of our Kin!\nAssassinated!", "One of our <link=member> family members\\l has been\ncruelly assassinated! We have at least managed\nto kill the assailant.", sender, receiver);
	}

	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		return new HeaderedMail("Intrigue: Assassinate", 
								"Assassinate a <link=member>house member\\l of House\n" +
								receiver.getLinkedName() + ".\n\n" +
								"They get -1<img MAIN_GUI MALE_ICON> or -1<img MAIN_GUI FEMALE_ICON>\nbut the local spy will be lost.\n\n",
								sender,  receiver);
	}

}
