package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class LevyAction extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2294651324828853306L;
	
	public static final byte RAISE = 0;
	public static final byte UNRAISE = 1;
	public static final byte MOVETO = 2;
	public static final byte ARRIVE = 3;
	public static final byte PILLAGE = 4;
	public static final byte KILL = 5;
	public static final byte DESTROY = 6;
	public static final byte CHANGE_FORMATION = 7;
	public static final byte TRANSFER = 8;
	public static final byte STOP = 9;
	public static final byte RETREATTO = 10;
	
	public final short levyID;
	public final byte actionID;
	public final short param;
	
	public LevyAction(short baronyID, byte actionID, short param) {
		this.levyID = baronyID;
		this.actionID = actionID;
		this.param = param;
	}
	
	public String toString() {
		return "LevyAction: " + levyID + "," + actionID + "," + param;
	}
	
	@Override
	public void process(Connection c) {
		
		if (levyID >= World.getInstance().armies.size()) return;
		
		Army army = World.getInstance().armies.get(levyID);
		int index1, index2;
		
		
		switch(actionID) {
		case RAISE:
			army.raise(World.getInstance().getHolding(param));
			break;
		case MOVETO:
			army.moveTo(World.getInstance().getHolding(param));
			break;
		case RETREATTO:
			army.retreating = true;
			army.moveTo(World.getInstance().getHolding(param));
			break;
		case KILL:
			army.kill();
			break;
		case UNRAISE:
			army.unraise();
			break;
		case LevyAction.PILLAGE: 
			army.pillage();
			break;
		case LevyAction.ARRIVE:
			army.arriveAtWaypoint();
			break;
		case LevyAction.DESTROY:
			army.destroy();
			break;
		case CHANGE_FORMATION:
			index1 = param & 0xff;
			index2 = (param >> 8);
			army.switchRegiments(index1, index2);
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(this);
			}
			break;
		case TRANSFER:
			index1 = param & 0xff;
			index2 = (param >> 8);
			
			if (army.getHome().getLevy() == army) {
				army.switchRegiments(index1, army.getHome().getGarrison(), index2);
			} else {
				army.switchRegiments(index1, army.getHome().getLevy(), index2);
			}
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(this);
			}
			break;
		case STOP:
			army.endTravel();
			
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(this);
			}
			break;
		default:
			throw new RuntimeException("Invalid LevyAction");
		}
	}

}
