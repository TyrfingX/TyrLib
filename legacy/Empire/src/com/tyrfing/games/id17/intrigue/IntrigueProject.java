package com.tyrfing.games.id17.intrigue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.networking.JoinIntrigue;
import com.tyrfing.games.id17.networking.NetworkAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.scene.SceneManager;

public class IntrigueProject implements IUpdateable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1420723933634727700L;

	public final List<House> supporters = new ArrayList<House>();
	
	private float points;
	private float plotSpeed;
	
	public final IntrigueAction action;
	public final House sender;
	public final House receiver;
	public final int options[];
	
	public static final float BASE_PLOT_POWER = 10;
	
	private boolean finished = false;
	
	private boolean revealed = false;
	
	public IntrigueProject(IntrigueAction action, House sender, House receiver, int options[]) {
		this.action = action;
		this.sender = sender;
		this.receiver = receiver;
		this.options = options;
		
		addSupporter(sender);
		
		points += BASE_PLOT_POWER;
	}
	
	public void addSupporter(House house) {
		supporters.add(house);
		float plotSpeedInc = 0.1f / supporters.size();
		if (house.hasSpy(receiver)) {
			plotSpeedInc *= 3;
		}
		
		if (house.getRival() == receiver) {
			plotSpeedInc *= 1.25f;
		}
		
		plotSpeed += plotSpeedInc;
		points += house.getCourtPower(receiver);
		house.intrigueProject = this;
		
		if (house != sender && EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			 EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new JoinIntrigue(house.id, sender.id));
		}
	}
	
	public void removeSupporter(House house) {
		supporters.remove(house);
		float plotSpeedInc = 0.1f / supporters.size();
		if (house.hasSpy(receiver)) {
			plotSpeedInc *= 2;
		}
		
		if (house.getRival() == receiver) {
			plotSpeedInc *= 1.25f;
		}
		
		plotSpeed -= plotSpeedInc;
		points -= house.getCourtPower(receiver);
		
		house.intrigueProject = null;
	}
	
	public void acceptInvite(House house) {
		addSupporter(house);
	}
	
	public void rejectInvite(House house) {
	
	}
	
	public float getPoints() {
		return points;
	}

	@Override
	public void onUpdate(float time) {
		points += plotSpeed*time*World.getInstance().getPlaySpeed() / World.SECONDS_PER_DAY;
		
		float max = getMaxPoints();
		
		if (points >= max) {
			points = max;
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				execute();
			}
			finished = true;
		}
		
		action.onUpdate(this);
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
	
	public void abort(House house) {
		if (house != sender) {
			removeSupporter(house);
		} else{
			House playerHouse = World.getInstance().getPlayerController().getHouse();
			if (playerHouse != sender && supporters.contains(playerHouse)) {
				World.getInstance().getMainGUI().mailboxGUI.addMail(
						new HeaderedMail(
								"Intrigue: Intrigue we have been\nsupporting aborted!",
								"The ungrateful " + sender.getLinkedName() + " have abandoned\n" + 
								"their plot and all effort has been lost.\n",
								sender, playerHouse
						), 
				false);
			}
			
			for (int i = 0; i < supporters.size(); ++i) {
				removeSupporter(supporters.get(i));
			}
			
			finished = true;
		}
		
	}
	
	public void execute() {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			NetworkAction na = new NetworkAction(	NetworkAction.INTRIGUE_EXECUTE_ID, 
													Intrigue.actions.indexOf(action), 
													sender.id, receiver.id, options, 0);
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(na);
		}
		
		action.execute(sender, receiver, options, supporters);
		
		for (int i = 0; i < supporters.size(); ++i) {
			supporters.get(i).intrigueProject = null;
		}
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			if (sender == World.getInstance().getPlayerController().getHouse()) {
				if (World.getInstance().getMainGUI().mailboxGUI != null) {
					Mail mail = action.getSuccessMail(sender, receiver, options);
					World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
				}
			}
		}
	}
	
	public void reveal() {
		revealed = true;
	}
	
	public int getMaxPoints() {
		int maxPoints = action.getMaxPoints(sender, receiver, options);
		
		if (revealed) {
			maxPoints *= 4;
		}
		
		return maxPoints;
	}
	
	public float getPlotSpeed() {
		return plotSpeed;
	}
	
	@Override
	public String toString() {
		return action.toString(sender, receiver, options);
	}

	public int getEstimatedRemainingDays() {
		return (int) ((getMaxPoints() - points) / (plotSpeed / World.SECONDS_PER_DAY));
	}
	
	public static int getEstimatedRemainingDays(IntrigueAction a, House sender, House receiver, int[] options) {
		float plotSpeed = 0.1f;
		if (sender.hasSpy(receiver)) {
			plotSpeed *= 2;
		}
		
		float reqPoints = Math.max(0, (a.getMaxPoints(receiver, receiver, options) - sender.getCourtPower(receiver) - BASE_PLOT_POWER));
		
		return (int) (reqPoints / (plotSpeed / World.SECONDS_PER_DAY));
	}
}
