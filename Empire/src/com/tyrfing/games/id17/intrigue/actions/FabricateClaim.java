package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.gui.mails.BattleMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.HoldingEntry;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.YesNoMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

/**
 * OPTIONS
 * 0: Holding ID
 *
 */

public class FabricateClaim extends IntrigueAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8316359271788535434L;
	public final int MAX_POINTS_BARONY = 300;
	public final int MAX_POINTS_HOLDING = 150;
	
	public static final String MSG = "- Gain claim on target\n  holding\n- Plot points: <#ff0000>";
	
	public FabricateClaim() {
		super("Claim");		
		disabledText = "FAILED TO UPDATE";
	}
	
	public int getMaxPoints(Holding holding) {
		return holding instanceof Barony ? MAX_POINTS_BARONY : MAX_POINTS_HOLDING;
	}

	@Override
	public Mail getStartMail(final House sender, final House receiver) {
		HeaderedMail mail = new HeaderedMail("Intrigue: Fabricate claim", sender, receiver, false) {
			
			private int selection;
			private Label text;
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);

				this.addRightColumnList("Holding");
				
				 List<Holding> holdings = receiver.getHoldings();
				 for (int i = 0; i < holdings.size(); ++i) {
					 if (!sender.hasClaim(holdings.get(i))) {
						 HoldingEntry entry = new HoldingEntry(holdings.get(i), this);
						 rightColumn.addItemListEntry(entry);
						 entry.setReceiveTouchEvents(true);
						 
						 if (i == 0) {
							 entry.highlight();
							 selection = holdings.get(i).getHoldingID();
						 }
					 }
				 }
				 
				 int points = getMaxPoints(holdings.get(0));
				 if (sender.hasSpy(receiver)) {
				 	points /= 2;
				 }
				 
				 text = (Label) WindowManager.getInstance().createLabel(name + "/MAIN_TEXT", BattleMail.TOTAL_LABEL_POS, MSG + points);
				 text.setColor(Color.BLACK.copy());
				 text.setInheritsAlpha(true);
				 left.addChild(text);

			}
			
			@Override
			public void selectRight(int index) {
				 selection = index;
				
				 int points = getMaxPoints(World.getInstance().getHolding(index));
				 if (sender.hasSpy(receiver)) {
				 	points /= 2;
				 }
				 
				 text.setText(MSG + points);
			}
			
			@Override
			protected void onAccept() {
				remove();
				startProject(sender, receiver, new int[] { selection });
			}
		};
		
		mail.addAcceptButton();
		return mail;
	}

	@Override
	public Mail getSuccessMail(House sender, House receiver, int[] options) {
		Mail mail = new HeaderedMail("Intrigue: Fabricate Claim", "We have successfully fabricated a claim\n" +
				"on " + World.getInstance().getHolding(options[0]).getLinkedName() + " of House "  + receiver.getLinkedName() + "!", sender, receiver);
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options) {
		Mail mail =	new YesNoMail(	"Intrigue: Fabricate Claim", 
				"We have received an offer from\nHouse " + 
				intrigueSender.getLinkedName() + " to support their efforts\nin fabricating a claim on " + 
				World.getInstance().getHolding(options[0]).getLinkedName() + "\nof House " + 
				intrigueReceiver.getLinkedName(), intrigueSender, inviteReceiver, World.getInstance().getWorldTime());
		mail.setIconName("Intrigue");
		return mail;
	}

	@Override
	public void execute(House sender, House receiver, int[] options, List<House> supporters) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			sender.addJustification(new WarJustification(World.getInstance().getHolding(options[0]), sender));
		}
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText(" (1)", receiver.getHoldings().size() > 0) + " Target has holdings ";
	}	

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.getHoldings().size() > 0;
	}

	@Override
	public int getMaxPoints(House sender, House receiver, int[] options) {
		int points = getMaxPoints(World.getInstance().getHolding(options[0]));
		if (sender.hasSpy(receiver)) {
			points /= 2;
		}
		return points;
	}

	@Override
	public String toString(House sender, House receiver, int[] options) {
		return "Fabricate a claim on " + World.getInstance().getHolding(options[0]).getLinkedName() + "\nof House " + receiver.getLinkedName() + ".";
	}
	
	@Override
	public void onUpdate(IntrigueProject project) {
		Holding holding = World.getInstance().getHolding(project.options[0]);
		if (project.receiver != holding.getOwner()) {
			
			if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
				for (int i = 0; i < project.supporters.size(); ++i) {
					if (project.supporters.get(i) == World.getInstance().getPlayerController().getHouse()) {
						if (World.getInstance().getMainGUI().mailboxGUI != null) {
							Mail mail = new HeaderedMail("Intrigue: Fabricate Claim\nFailed!", 
														  "\"My lord, our attempts to fabricate\na claim on House " + project.receiver.getLinkedName() + " have\nproven futile\".\nReason: " + holding.getLinkedName() + " is longer in their\npossession!", 
														  project.supporters.get(i), 
														  project.receiver);
							mail.setIconName("Intrigue");
							World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
						}
					}
				}
			}
			
			project.abort(project.sender);
		}
	}

	@Override
	public Mail getInformMail(House sender, House receiver, int[] options) {
		Holding holding = World.getInstance().getHolding(options[0]);
		return new HeaderedMail("A claim on " + holding.getLinkedName() + "\nhas been fabricated!", "The dirty swine " + sender.getLinkedName() + " have\nfabricated a claim on a holding\nwe possess!", sender, receiver);
	}

	@Override
	public HeaderedMail showInfo(House sender, House receiver, int[] options) {
		return new HeaderedMail("Intrigue: Fabricate Claim", 
								"Fabricates a claim to legitimize\ndeclarations of war.",
								sender,  receiver);
	}
	
}
