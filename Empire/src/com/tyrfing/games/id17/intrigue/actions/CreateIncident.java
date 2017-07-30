package com.tyrfing.games.id17.intrigue.actions;

import java.util.Iterator;
import java.util.List;

import com.tyrfing.games.id17.gui.mails.BattleMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.HoldingEntry;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.Village;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class CreateIncident extends IntrigueAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2445942507882066851L;

	public static final int MAX_POINTS = 200;
	
	public static final int REASON_HONORABLE = 1;
	public static final int REASON_TYRANNY = 2;
	public static final int REASON_WARMONGER = 3;
	
	public CreateIncident() {
		super("Create Incident");
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		return new HeaderedMail("Intrigue: Create Incident\nin Border Holding", sender, receiver, false) {
			
			private int selection;
			private int reason;
			private Label text;
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
				this.addAcceptButton();
				accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						remove();
						startProject(sender, receiver, new int[] { selection, reason });
					}
				});
				
				this.addRightColumnList("");
				
				boolean initialEntry = true;
				
				holding: for (int i = 0; i < receiver.getHoldings().size(); ++i) {
					Iterator<Barony> itr = sender.neighbours.iterator();
					
					if ((receiver.getHoldings().get(i) instanceof Barony) || (receiver.getHoldings().get(i) instanceof Village)) {
						while(itr.hasNext()) {
							Barony barony = itr.next();
							for (int j = 0; j < barony.getCountSubHoldings(); ++j) {
								if (barony.getSubHolding(j) == receiver.getHoldings().get(i)) {
									 HoldingEntry entry = new HoldingEntry(receiver.getHoldings().get(i), this);
									 rightColumn.addItemListEntry(entry);
									 entry.setReceiveTouchEvents(true);
									 if (initialEntry) {
										 initialEntry = false;
										 entry.highlight();
										 selection = receiver.getHoldings().get(j).getHoldingID();
									 }
									continue holding;
								}
							}
						}
					}
					
				}
				
				String msg = "";
				
				boolean controversy = false;
				
				if (sender.isActive("Honorable") && !receiver.isActive("Honorable") ) {
					msg = "- are honorable and\n  they are not.";
					reason = REASON_HONORABLE;
					controversy = true;
				}
				
				if (!sender.isActive("Tyrant") && receiver.isActive("Tyrant") ) {
					if (controversy) {
						msg += "\n";
					}
					
					msg = "- are no tyrants but\n  they are.";
					reason = REASON_TYRANNY;
					controversy = true;
				}
				
				if (!sender.isActive("Warmongerer") && receiver.isActive("Warmongerer") ) {
					if (controversy) {
						msg += "\n";
					}
					
					msg = "- are no warmongers but\n  they are.";
					reason = REASON_WARMONGER;
					controversy = true;
				}
				
				if (controversy) {
					msg = "Possible since we\n" +  msg;
				
				} else {
					msg = "- Not possible due\n  to no controversy.";
					accept.disable();
				}
				
				text = (Label) WindowManager.getInstance().createLabel(name + "/MAIN_TEXT", BattleMail.TOTAL_LABEL_POS, msg);
				text.setColor(Color.BLACK.copy());
				text.setInheritsAlpha(true);
				left.addChild(text);

			}
		};
	}

	@Override
	public Mail getSuccessMail(House sender, House receiver, int[] options) {
		Holding holding = World.getInstance().getHolding(options[0]);
		Mail mail = new HeaderedMail("Intrigue: Create Incident", "Our agents orchestrated a\n" +
				"dispute at " + holding.getLinkedName() + ". The people\n" + 
				"would now rather be ruled by us.", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail =  new YesNoMail(	"Intrigue: Create Incident", 
				"We have received an offer from\nhouse of " + 
				intrigueSender.getLinkedName() + " to support their efforts in creating\nan incident at a border\nholding of House " + 
				intrigueReceiver.getLinkedName(), intrigueSender, inviteReceiver, World.getInstance().getWorldTime());
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		Holding holding = World.getInstance().getHolding(options[0]);
		holding.addUnrestSource(new IncidentUnrest(sender, holding));
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
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
		return "Create border incident with " + receiver.getLinkedName() + ".";
	}
	
	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		Holding holding = World.getInstance().getHolding(options[0]);
		String reasonText = "";
		switch(options[1]) {
		case REASON_HONORABLE:
			reasonText = "abiding the codex of honor.";
			break;
		case REASON_TYRANNY:
			reasonText = "not ruling by tyranny.";
			break;
		case REASON_WARMONGER:
			reasonText = "not consumed by constant war.";
			break;
		}
		
		return new HeaderedMail("The people of " + holding.getLinkedName() + "\nwould not be ruled by us!", 
								"Propaganda about " + sender.getLinkedName() + " being the better\nruler has been spread.\n" + 
								"They would rather serve a\nHouse " + reasonText, sender, receiver);
	}
	
	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		return new HeaderedMail("Intrigue: Create Incident", 
								"Propagates a dispute within the population.\n" +
								"The People will prefer being ruled by somebody else.",
								sender,  receiver);
	}

}
