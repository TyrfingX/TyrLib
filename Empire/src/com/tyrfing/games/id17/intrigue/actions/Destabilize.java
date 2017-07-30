package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;

public class Destabilize extends IntrigueAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2276284332777374397L;
	public static final int MAX_POINTS = 100;
	
	public Destabilize() {
		super("Destabilize");
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		return new HeaderedMail("Intrigue: Destabilize", 
								"Support revolting forces in all holdings\n" +
								"possessed by House " + receiver.getLinkedName() + ".\n\n"  +
								"+100% revolt risk for 1 year\n", sender, receiver) {
			
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
		Mail mail = new HeaderedMail("Intrigue: Destabilize", "We have successfully destabilized the realm\n" +
				"of House " + receiver.getLinkedName() + "!", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail = new YesNoMail(	"Intrigue: Destabilize", 
				"We have received an offer from\nhouse of " + 
				intrigueSender.getLinkedName() + " to support their efforts in destabilizing\nthe realm of House " + 
				intrigueReceiver.getLinkedName(), intrigueSender, inviteReceiver, World.getInstance().getWorldTime());
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		receiver.addStatModifier(new StatModifier("Unstable", House.UNSTABLE, receiver, sender, World.SECONDS_PER_DAY * World.DAYS_PER_YEAR, 1) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -418874967993695699L;

			@Override
			public void apply() {
				World.getInstance().getHouses().get(affected).stats[stat] += value;
			}
			
			@Override
			public void unapply() {
				World.getInstance().getHouses().get(affected).stats[stat] -= value;
			}
		});
	}

	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText(" (1)", receiver.stats[House.UNSTABLE] == 0 ) + " Not unstabilized ";
	}	
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.stats[House.UNSTABLE] == 0;
	}

	@Override
	public int getMaxPoints(House sender, House receiver, int[] options) {
		float points = MAX_POINTS;
		if (sender.hasSpy(receiver)) {
			points /= 2;
		}
		return (int) points;
	}

	@Override
	public String toString(House sender, House receiver, int[] options) {
		return "Destabilize realm of House " + receiver.getLinkedName() + ".";
	}
	
	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		return new HeaderedMail("Revolting forces have grown\nin force!", "Propaganda against our rule has\nbeen spread on the streets.\nWe believe this to be the work\nof a foreign power.", sender, receiver);
	}
	
	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		return new HeaderedMail("Intrigue: Destabilize", 
								"Enhances instability and unrest within the\n" +
								"population.\n\n+100% revolt risk for 1 year",
								sender,  receiver);
	}

}
