package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.networking.Connection;

public class NetworkAction extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3255994320443439339L;

	public static final int DIPLO_ID = 0;
	public static final int INTRIGUE_ID = 1;
	public static final int INTRIGUE_EXECUTE_ID = 2;
	
	public final int type;
	public final int id;
	public final int senderID;
	public final int receiverID;
	public final int[] options;
	public final int response;
	
	/**
	 * 
	 * @param type
	 * @param id
	 * @param senderID
	 * @param receiverID
	 * @param options
	 * @param response
	 */
	
	public NetworkAction(int type, int id, int senderID, int receiverID, int[] options, int response) {
		this.type = type;
		this.id = id;
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.options = options;
		this.response = response;
	}
	
	public NetworkAction(Message m) {
		type = DIPLO_ID;
		id = m.action.id;
		senderID = m.sender.id;
		receiverID = m.receiver.id;
		options = m.options;
		response = m.response;
	}
	
	
	@Override
	public void process(Connection c) {
		final List<House> houses = World.getInstance().getHouses();
		
		if (type == NetworkAction.DIPLO_ID) {
			DiploAction a = Diplomacy.actions.get(id);
			final Message m = new Message(a, houses.get(senderID), houses.get(receiverID), options);
			m.response = response;
		
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {	
				AIThread.getInstance().addMessage(m);
			} else {
				World.getInstance().getPlayerController().informMessage(m);	
			}
		} else if (type == NetworkAction.INTRIGUE_ID) {
			final IntrigueAction a = Intrigue.getInstance().getActions().get(id);
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				AIThread.getInstance().addIntrigue(new IntrigueProject(	a, 
																		houses.get(senderID),
																		houses.get(receiverID), 
																		options));
			} else {
				a.startProjectNoNetwork(houses.get(senderID), houses.get(receiverID), options);	
			}
		} else if (type == NetworkAction.INTRIGUE_EXECUTE_ID) {
			if (houses.get(senderID).intrigueProject != null) {
				houses.get(senderID).intrigueProject.execute();
			}
		}
	}
}
