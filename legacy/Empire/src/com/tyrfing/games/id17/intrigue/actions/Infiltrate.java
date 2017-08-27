package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;

public class Infiltrate extends IntrigueAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8001554562499661678L;
	public static final ScaledVector2 MALE_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x * 0.15f, HeaderedMail.MAIN_PAPER_SIZE.y * 0.85f, 1);
	public static final ScaledVector2 FEMALE_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x - MALE_POS.x - HeaderedMail.ACCEPT_SIZE.x, MALE_POS.y, 1);
	
	public static final int MAX_POINTS = 35;
	
	public Infiltrate() {
		super("Infiltrate");
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		return new HeaderedMail("Intrigue: Infiltrate", 
								"Infiltrate house of " + receiver.getLinkedName() + " with a spy.\n" + 
								"We gain insight into their military\n" + 
								"power and ease our plotting activities.\n\n" + 
								"We need " + Util.getFlaggedText(String.valueOf(MAX_POINTS), false) + "\\# plot points.\n" +
								"Infiltrate using a... ", sender, receiver) {
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
	
				final PaperButton female = new PaperButton(this.getName() + "/ACCEPT/BUTTON", ACCEPT2_POS.get().add(ACCEPT_POS_OFFSET.get()), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "<#ff0000>-1\\#<img MAIN_GUI FEMALE_ICON> female");
				female.setReceiveTouchEvents(true);
				frame.addChild(female);

				final PaperButton male =  new PaperButton(this.getName() + "/REJECT/BUTTON", REJECT2_POS.get().add(ACCEPT_POS_OFFSET.get()), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "<#ff0000>-1\\#<img MAIN_GUI MALE_ICON> male");
				male.setReceiveTouchEvents(true);
				frame.addChild(male);
				
				female.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						remove();
						startProject(sender, receiver, new int[] { 0 });
					}
				});
				
				male.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						remove();
						startProject(sender, receiver, new int[] { 1 });
					}
				});
				
			}
		};
	}

	@Override
	public Mail getSuccessMail(House sender, House receiver, int[] options) {
		Mail mail = new HeaderedMail("Intrigue: Infiltrate", "We have successfully infiltrated\n" +
									 "house of " + receiver.getLinkedName() + "!", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail = new YesNoMail(	"Intrigue: Infiltrate", 
				"We have received an offer from\nhouse of " + 
				intrigueSender.getLinkedName() + " to support their efforts in planting\na spy in house of " + 
				intrigueReceiver.getLinkedName(), intrigueSender, inviteReceiver, World.getInstance().getWorldTime());
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		if (options[0] == 0) {
			if (sender.getFemales() > 0) {
				sender.changeFemales(-1);
				sender.addSpy(receiver);
			}
		} else {
			if (sender.getMales() > 0) {
				sender.changeMales(-1);
				sender.addSpy(receiver);
			}
		}
		
	}

	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText(" (1)", sender.getHouseStat(receiver, House.HAS_SPY) == 0 ) + " No current spy ";
	}	
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.getHouseStat(receiver, House.HAS_SPY) == 0;
	}

	@Override
	public int getMaxPoints(House sender, House receiver, int[] options) {
		return MAX_POINTS;
	}

	@Override
	public String toString(House sender, House receiver, int[] options) {
		return "Infiltrate " + receiver.getLinkedName() + " with a spy.";
	}

	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		return null;
	}

	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		return new HeaderedMail("Intrigue: Infiltrate", 
								"Infiltrate house of " + receiver.getLinkedName() + " with a spy.\n" + 
								"Grants insight into their military\n" + 
								"power and eases plotting activities.\n\n",
								sender,  receiver);
	}

}
