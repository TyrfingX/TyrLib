package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.AIController;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.RebelController;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.PopulationType;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.intrigue.actions.IncidentUnrest;
import com.tyrfing.games.id17.networking.Revolt;
import com.tyrfing.games.id17.war.RebelArmy;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;

public class HandleRevolt extends DiploAction {

	public static final int TYRANNY = 5;
	public static final float TYRANNY_DECAY = -1f / World.DAYS_PER_YEAR;
	
	/**
	 * OPTIONS:
	 * 0 HOLDING ID
	 * 1 UNREST ID
	 * 2 CAN SUPPRESS
	 */
	private static final long serialVersionUID = 6556457664910019299L;

	public final static int ID = 19;
	
	public HandleRevolt(int responses) {
		super("Handle Revolt", ID, responses, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
	}
	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		
		if (this.responses == 0) {
			comply(sender, options);
			return;
		}
		
		Holding holding = World.getInstance().getHolding(options[0]);
		UnrestSource source = holding.getUnrestSource(options[1]);
		RebelArmy revoltArmy = source.revolt(holding);
		
		short id = (short) World.getInstance().getHouses().size();
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new Revolt((int)revoltArmy.totalTroops, id));
		}
		
		House rebelFaction = new House(House.REBEL_FACTION_NAME, new RebelController(new BehaviorModel(), revoltArmy), id);
		rebelFaction.setIsNPCFaction(true);
		revoltArmy.setOwner(rebelFaction);
		revoltArmy.raise(holding);
		rebelFaction.totalTroops = revoltArmy.getTotalTroops();
		rebelFaction.armies.add(revoltArmy.id);
		
		World.getInstance().getHouses().add(rebelFaction);
		WarGoal goal = new WarGoal(source);
		
		DeclareWar.declareWar(rebelFaction, holding.getOwner(), goal, WarJustification.NO_CLAIM_JUSTIFICATION);
		
		AIThread.getInstance().addAI((AIController)rebelFaction.getController());
		World.getInstance().getUpdater().addItem(rebelFaction);
		
		if (source instanceof IncidentUnrest) {
			IncidentUnrest incident = (IncidentUnrest) source;
			Message m = new Message(new SupportRevolt(revoltArmy), holding.getOwner(), incident.preferredRuler, new int[] {});
			m.action.send(m.sender, m.receiver, m.options);
		}
		
		sender.addStatModifier(new VaryingStatModifier("Suppression", House.TYRANNY, sender, -1, TYRANNY, TYRANNY_DECAY, 0));
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} else {
			comply(message.sender, message.options);
		}
	}
	
	private void comply(House house, int[] options) {
		Holding holding = World.getInstance().getHolding(options[0]);
		UnrestSource source = holding.getUnrestSource(options[1]);
		source.comply(house);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		
		Holding holding = World.getInstance().getHolding(message.options[0]);
		UnrestSource source = holding.getUnrestSource(message.options[1]);
		
		int revoltees = 0;
		for (int i = 0; i < PopulationType.VALUES.length; ++i) {
			revoltees += (int) (holding.holdingData.population[i] * source.strength);
		}
		
		Mail mail = null;
		
		if (message.options[2] > 0) {
			mail = new DiploYesNoMail("In " + holding.getLinkedName() + " <#ff0000>" + revoltees + "\\#\ncitizens are about to revolt!", source.text + "\n\n(1) \"Off with their heads\"\n(2) Comply with their demands", message, "(1) Suppress", "(2) Comply");
		} else {
			mail = new HeaderedMail("In " + holding.getLinkedName() + " <#ff0000>" + revoltees + "\\#\ncitizens are about to revolt!", source.text + "\n\nWe have no armies to suppress them\nand thus have to accept.", message.sender, message.receiver);
		}
		
		mail.setIconName("Revolt");
		
		return mail;
	}

}
