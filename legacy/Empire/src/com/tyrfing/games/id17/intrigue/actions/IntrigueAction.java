package com.tyrfing.games.id17.intrigue.actions;

import java.util.List;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.house.ActionGUI;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.networking.NetworkAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

public abstract class IntrigueAction extends Action {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -893293902402018481L;
	
	public IntrigueAction(String name) {
		super(name);
	}
	
	@Override
	public void selectedByUser(House sender, House receiver) {
		String identity = sender.toString()+receiver.toString()+this.toString();
		if (!World.getInstance().getMainGUI().mailboxGUI.showIdentity(identity)) {
			Mail mail = getStartMail(sender, receiver);
			mail.setIdentity(identity);
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
		}
	}
	
	public void startProject(House sender, House receiver, int[] options) {
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			startProjectNoNetwork(sender, receiver, options);
		} 
		
		int index = Intrigue.actions.indexOf(this);
		
		NetworkAction na = new NetworkAction(	NetworkAction.INTRIGUE_ID,
												index, 
												sender.id, 
												receiver.id,
												options, 0);
		EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(na);
	}
	
	public void startProjectNoNetwork(House sender, House receiver, int[] options) {
		sender.intrigueProject = new IntrigueProject(this, sender, receiver, options);
		World.getInstance().getUpdater().addItem(sender.intrigueProject);
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (sender == World.getInstance().getPlayerController().getHouse()) {
				ActionGUI gui = World.getInstance().getMainGUI().houseGUI.intrigueGUI;
				if (gui.isVisible()) {
					gui.hide();
					World.getInstance().getMainGUI().houseGUI.intrigueProjectGUI.show(sender, receiver);
				}
			}
		}
	}
	
	public abstract Mail getStartMail(final House sender, final House receiver);
	public abstract Mail getSuccessMail(House sender, House receiver, int[] options);
	public abstract Mail getInformMail(House sender, House receiver, int[] options);
	public abstract Mail getInviteMail(House intrigueSender, House intrigueReceiver, House inviteReceiver, int[] options);
	public abstract void execute(House sender, House receiver, int[] options, List<House> supporters);
	public abstract boolean isEnabled(House sender, House receiver);
	public abstract String toString(House sender, House receiver, int[] options);
	public abstract int getMaxPoints(House sender, House receiver, int[] options);
	public void onUpdate(IntrigueProject project) { }
	public abstract HeaderedMail showInfo(House house, House receiver, int[] options);
}
