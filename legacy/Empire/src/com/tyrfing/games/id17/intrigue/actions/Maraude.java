package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.actions.DeclareWar;
import com.tyrfing.games.id17.gui.mails.BaronyEntry;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.HoldingEntry;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;

public class Maraude extends IntrigueAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -714801613462061501L;

	public static final int MAX_POINTS = 2;
	
	/** OPTIONS 
	 * 	0: Target Holding
	 * 	1: Source Levy
	 */
	
	
	public Maraude() {
		super("Maraude");
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		final IntrigueAction a = this;
		return new HeaderedMail("Intrigue: Maraude\nHave a levy pillage under no banner", 
								sender, receiver, false) {
			private int targetHolding;
			private int srcLevy;
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);
				this.addAcceptButton();
				
				this.addRightColumnList("Holding");
				this.addLeftColumnList("Levy");
				
				List<Barony> baronies = receiver.getAllBaronies();
				for (int i = 0; i < baronies.size(); ++i) {
					BaronyEntry entry = new BaronyEntry(baronies.get(i), this);
					rightColumn.addItemListEntry(entry);
					entry.setReceiveTouchEvents(true);
					
					if (i == 0) {
						entry.highlight();
						entry.addSubHoldings();
						targetHolding = baronies.get(i).getHoldingID();
					}
				}
				
				srcLevy = -1;
				baronies = sender.getBaronies();
				for (int i = 0; i < baronies.size(); ++i) {
					HoldingEntry entry = new HoldingEntry(baronies.get(i), this, false);
					leftColumn.addItemListEntry(entry);

					Army levy = baronies.get(i).getLevy();
					entry.setEnabled(true);
					
					if (levy.isRaised()) {
						entry.setEnabled(false);
					} else {
						entry.setReceiveTouchEvents(true);
						entry.setEnabled(true);
					}
					
					if (srcLevy == -1 && entry.isEnabled()) {
						entry.highlight();
						srcLevy = levy.id;
					}
				}
				
				accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						remove();
						startProject(sender, receiver, new int[] { srcLevy, targetHolding });
					}
				});
				
				if (srcLevy == -1) {
					accept.disable();
				} else {
					updateText();
				}
				
			}
			
			@Override
			public void selectRight(int index) {
				 targetHolding = index;
				 updateText();
			}
			
			@Override
			public void selectLeft(int index) {
				 srcLevy = ((Barony)World.getInstance().getHolding(index)).getLevy().id;
				 updateText();
			}
			
			private void updateText() {
				if (srcLevy != -1) {
				 for (int i = 0; i < rightColumn.getCountEntries();++i) {
					 HoldingEntry entry = (HoldingEntry) rightColumn.getEntry(i);
					 int[] opt = {srcLevy, entry.holding.getHoldingID() };
					 entry.nameLabel.setText(entry.holding.getName() +  " " + IntrigueProject.getEstimatedRemainingDays(a, sender, receiver, opt) + "d");
				 }
				}
			}
		};
	}

	@Override
	public Mail getSuccessMail(House sender, House receiver, int[] options) {
		Mail mail = new HeaderedMail("Intrigue: Assassinate", 
				"Successfully sent our levy into the realm of\n" + 
				"House " + World.getInstance().getHoldings().get(options[1]).getLinkedName() + ".", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail = new YesNoMail(	
			"Intrigue: Maraude", 
			"House of " + intrigueSender.getLinkedName() + " seeks our support in\n" +
			"smuggling an army of theirs into the realm\n" +
			"of House " + intrigueReceiver.getLinkedName() + ".", 
			intrigueSender, inviteReceiver, 
			World.getInstance().getWorldTime()
		);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		Holding h = World.getInstance().getHolding(options[1]);
		House faction = createMaraudeSubFaction((short) options[0]);
		
		WarGoal goal = new WarGoal(null, null, WarGoal.MARAUDE);
		DeclareWar.declareWar(faction, h.getOwner().getSupremeOverlord(), goal, WarJustification.NO_CLAIM_JUSTIFICATION);
		
		Army a = World.getInstance().getArmy(options[0]);
		a.raise(h);
	}
	
	public static House createMaraudeSubFaction(short armyID) {
		Army a = World.getInstance().getArmy(armyID);
		
		short id = (short) World.getInstance().getHouses().size();
		
		House marauderFaction = new House(House.MARAUDER_FACTION_NAME, a.getOwner().getController(), id);
		marauderFaction.setIsNPCFaction(true);
		marauderFaction.totalTroops = a.getTotalTroops();
		a.setOwner(marauderFaction);
		marauderFaction.armies.add(a.id);
		
		World.getInstance().getHouses().add(marauderFaction);
		
		return marauderFaction;
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		(receiver.isRealmNeighbour(sender) && receiver.isIndependend());
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText("(1)", receiver.isIndependend()) + " They are independent\n";
		disabledText += Util.getFlaggedText("(2)", receiver.isRealmNeighbour(sender)) + " Neighbouring Realm";
	}	


	@Override
	public int getMaxPoints(House sender, House receiver, int[] options) {
		Holding home = World.getInstance().getArmy(options[0]).getHome();
		float distance = World.getInstance().getMap().getDistance(home.getHoldingID(), options[1]);
		return (int)(distance * (sender.hasSpy(receiver) ? MAX_POINTS / 2 : MAX_POINTS));
	}

	@Override
	public String toString(House sender, House receiver, int[] options) {
		return "Raise levy as marauders in " + World.getInstance().getHoldings().get(options[1]).getName() + ".";
	}
	
	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		return new HeaderedMail(
			"Maurauding bandits\nhave infested our lands!", 
			"An army of marauders has appeared in\n" +
			World.getInstance().getHoldings().get(options[1]).getLinkedName() + ". We must fight them by\n" +
			"force to rid them from our realm.", 
			receiver, receiver
		);
	}

	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		Holding h = World.getInstance().getHoldings().get(options[1]);
		return new HeaderedMail(
			"Intrigue: Maraude", 
			"Levy of " + World.getInstance().getArmy(options[0]).getHome().getLinkedName() + " will be\n" +
			"disguised as marauders and raised in\n" +
			 h.getLinkedName() + " of House " + h.getOwner().getLinkedName() + ".",
			receiver, receiver
		);
	}

}
